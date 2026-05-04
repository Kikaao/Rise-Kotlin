package com.rise.fitrpg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rise.fitrpg.AppConstants
import com.rise.fitrpg.data.models.ClassProgress
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.StreakTier
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.data.models.WorkoutSession
import com.rise.fitrpg.data.repository.CardioRepository
import com.rise.fitrpg.data.repository.QuestRepository
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.systems.LevelSystem
import com.rise.fitrpg.systems.QuestSystem
import com.rise.fitrpg.systems.StreakSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ============================================================
// HomeViewModel
// Layer: ViewModel
// ============================================================
// Feeds the home screen with all data the user sees on launch:
//   - User state (class, level, stats, gold, streak)
//   - Current class progress + XP bar
//   - Active quests for the week
//   - Recent workout sessions
//
// Also responsible for week reset detection on app launch:
//   - Evaluates the streak (did the user hit their weekly goal?)
//   - Generates new quests if the week rolled over
//
// Read-heavy ViewModel — most state comes from reactive flows.
// The only writes happen during week reset processing.
// ============================================================

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository,
    private val questRepository: QuestRepository
) : ViewModel() {

    private val userId = AppConstants.CURRENT_USER_ID

    // ── REACTIVE STATE ─────────────────────────────────────

    // User state — updates automatically when any game event changes the user.
    // The home screen observes this for class, level, streak, gold, stats.
    val user: StateFlow<User?> = userRepository.getUserFlow(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active quests — updates when quest progress changes or new quests are generated.
    // nowMs is set at init time; for a more accurate filter the UI can re-trigger loading.
    private val _activeQuests = MutableStateFlow<List<Quest>>(emptyList())
    val activeQuests: StateFlow<List<Quest>> = _activeQuests.asStateFlow()

    // Recent workouts — last 5 sessions for the home screen summary.
    private val _recentWorkouts = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val recentWorkouts: StateFlow<List<WorkoutSession>> = _recentWorkouts.asStateFlow()

    // Week reset feedback — set once after week evaluation, consumed by the UI
    // to show a dialog/snackbar (e.g. "Streak saved by freeze!" or "Streak broken").
    private val _weekResetEvent = MutableStateFlow<WeekResetEvent?>(null)
    val weekResetEvent: StateFlow<WeekResetEvent?> = _weekResetEvent.asStateFlow()

    // ── DERIVED HELPERS (computed from user state) ──────────

    // Current class progress for the XP bar on the home screen.
    // Returns null if user hasn't loaded yet.
    fun getCurrentClassProgress(user: User): ClassProgress? {
        return user.classMap[user.currentClass]
    }

    // 0.0–1.0 progress toward next level — feeds the XP bar width.
    fun getLevelProgressFraction(user: User): Double {
        val progress = getCurrentClassProgress(user) ?: return 0.0
        return LevelSystem.getProgressToNextLevel(progress)
    }

    // XP needed for the next level — shown as "245 / 3000 XP" on the bar.
    fun getXpForNextLevel(user: User): Int {
        val progress = getCurrentClassProgress(user) ?: return 0
        return LevelSystem.getXpForLevel(progress.level + 1)
    }

    // Streak multiplier currently active — shown next to the streak counter.
    fun getStreakMultiplier(user: User): Double {
        return StreakSystem.getCurrentMultiplier(user.currentStreak)
    }

    // Weekly minimum for the user's current streak tier.
    fun getWeeklyMinimum(user: User): Int {
        return StreakSystem.getWeeklyMinimum(user.streakTier)
    }

    // ── INIT ───────────────────────────────────────────────

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val nowMs = System.currentTimeMillis()

            // Load recent workouts (one-shot, not reactive — home screen doesn't
            // need live updates for workout history, just a snapshot on launch)
            _recentWorkouts.value = workoutRepository.getRecentSessions(userId, limit = 5)

            // Load active quests
            _activeQuests.value = questRepository.getActiveQuests(userId, nowMs)

            // Check for week reset — must happen after user flow has emitted at least once
            checkWeekReset(nowMs)
        }
    }

    // ── WEEK RESET ─────────────────────────────────────────
    // Called on app launch. If a full week has passed since the user's
    // weekStartDate, we need to:
    //   1. Evaluate the streak (did they hit their weekly goal?)
    //   2. Generate new quests for the fresh week
    //   3. Save updated user state
    //
    // This is the only write path in HomeViewModel.

    private suspend fun checkWeekReset(nowMs: Long) {
        val currentUser = userRepository.getUser(userId) ?: return

        // Step 1: Evaluate the week — returns null if a week hasn't passed yet
        val weekResult = StreakSystem.evaluateWeek(currentUser, nowMs) ?: return

        // A new week has started — process the result
        val updatedUser = when (weekResult) {
            is StreakSystem.WeeklyEvaluationResult.GoalMet -> {
                _weekResetEvent.value = WeekResetEvent.GoalMet(
                    workoutsLogged = weekResult.workoutsLogged,
                    minimumRequired = weekResult.minimumRequired
                )
                weekResult.updatedUser
            }
            is StreakSystem.WeeklyEvaluationResult.FreezeConsumed -> {
                _weekResetEvent.value = WeekResetEvent.FreezeConsumed(
                    workoutsLogged = weekResult.workoutsLogged,
                    minimumRequired = weekResult.minimumRequired,
                    freezesRemaining = weekResult.freezesRemaining
                )
                weekResult.updatedUser
            }
            is StreakSystem.WeeklyEvaluationResult.StreakBroken -> {
                _weekResetEvent.value = WeekResetEvent.StreakBroken(
                    workoutsLogged = weekResult.workoutsLogged,
                    minimumRequired = weekResult.minimumRequired
                )
                weekResult.updatedUser
            }
        }

        // Save user with updated streak/weekly state
        userRepository.saveUser(updatedUser)

        // Step 2: Generate new quests for the week if none exist
        val hasQuests = questRepository.hasActiveQuests(userId, nowMs)
        if (!hasQuests) {
            val allWorkouts = workoutRepository.getAllSessions(userId)
            val allCardio = cardioRepository.getAllSessions(userId)

            val questResult = QuestSystem.generateQuests(
                user = updatedUser,
                allWorkoutSessions = allWorkouts,
                allCardioSessions = allCardio,
                nowMs = nowMs
            )

            questRepository.saveQuests(questResult.allQuests, userId)
            _activeQuests.value = questRepository.getActiveQuests(userId, nowMs)
        }
    }

    // Called by the UI after displaying the week reset dialog/snackbar.
    // Clears the event so it doesn't show again on recomposition.
    fun consumeWeekResetEvent() {
        _weekResetEvent.value = null
    }

    // Called when returning from workout screen or other screens
    // that may have changed data. Refreshes the snapshot data.
    fun refresh() {
        viewModelScope.launch {
            val nowMs = System.currentTimeMillis()
            _recentWorkouts.value = workoutRepository.getRecentSessions(userId, limit = 5)
            _activeQuests.value = questRepository.getActiveQuests(userId, nowMs)
        }
    }

    // ── WEEK RESET EVENT ───────────────────────────────────
    // One-shot event consumed by the UI to show feedback after week evaluation.

    sealed class WeekResetEvent {
        data class GoalMet(
            val workoutsLogged: Int,
            val minimumRequired: Int
        ) : WeekResetEvent()

        data class FreezeConsumed(
            val workoutsLogged: Int,
            val minimumRequired: Int,
            val freezesRemaining: Int
        ) : WeekResetEvent()

        data class StreakBroken(
            val workoutsLogged: Int,
            val minimumRequired: Int
        ) : WeekResetEvent()
    }
}

// ============================================================
// Factory — manual DI, no Hilt
// ============================================================

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository,
    private val questRepository: QuestRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                userRepository = userRepository,
                workoutRepository = workoutRepository,
                cardioRepository = cardioRepository,
                questRepository = questRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

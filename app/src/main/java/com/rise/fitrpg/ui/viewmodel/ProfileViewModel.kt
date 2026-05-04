package com.rise.fitrpg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rise.fitrpg.AppConstants
import com.rise.fitrpg.data.models.ClassProgress
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.PlayerProfile
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.systems.ClassSwitchSystem
import com.rise.fitrpg.systems.LevelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ============================================================
// ProfileViewModel
// Layer: ViewModel
// ============================================================
// Feeds the profile screen with:
//   - User state (class, level, stats, gold, streak)
//   - Per-class progress (all 6 classes with level + XP)
//   - Player profile (optional physical data — bodyweight, age, gender)
//   - Class switch logic (can I switch? what's the head start? cooldown?)
//   - Lifetime stats (total workouts, total sets, total PRs)
//
// The only write paths are:
//   - Class switching (calls ClassSwitchSystem, saves updated user)
//   - Player profile updates (bodyweight, age, gender)
// ============================================================

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val userId = AppConstants.CURRENT_USER_ID

    // ── REACTIVE STATE ─────────────────────────────────────

    val user: StateFlow<User?> = userRepository.getUserFlow(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val playerProfile: StateFlow<PlayerProfile?> = userRepository.getProfileFlow(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── LIFETIME STATS ─────────────────────────────────────
    // Loaded once on init, refreshed on demand. Not reactive —
    // profile screen doesn't need live updates for these counts.

    private val _totalWorkouts = MutableStateFlow(0)
    val totalWorkouts: StateFlow<Int> = _totalWorkouts.asStateFlow()

    private val _totalSets = MutableStateFlow(0)
    val totalSets: StateFlow<Int> = _totalSets.asStateFlow()

    private val _totalPRs = MutableStateFlow(0)
    val totalPRs: StateFlow<Int> = _totalPRs.asStateFlow()

    // ── CLASS SWITCH STATE ─────────────────────────────────
    // Result of the last switch attempt — consumed by the UI to show feedback.

    private val _classSwitchEvent = MutableStateFlow<ClassSwitchEvent?>(null)
    val classSwitchEvent: StateFlow<ClassSwitchEvent?> = _classSwitchEvent.asStateFlow()

    init {
        loadLifetimeStats()
    }

    private fun loadLifetimeStats() {
        viewModelScope.launch {
            _totalWorkouts.value = workoutRepository.getSessionCount(userId)
            _totalSets.value = workoutRepository.getTotalSetCount(userId)
            _totalPRs.value = workoutRepository.getPRCount(userId)
        }
    }

    // ── DERIVED HELPERS ────────────────────────────────────

    // All 6 classes with their progress — for the class grid on the profile screen.
    fun getAllClassProgress(user: User): List<ClassProgress> {
        return FitnessClass.entries.map { fitnessClass ->
            user.classMap[fitnessClass] ?: ClassProgress(fitnessClass)
        }
    }

    // XP bar for any class — used when showing per-class detail cards.
    fun getLevelProgressFraction(progress: ClassProgress): Double {
        return LevelSystem.getProgressToNextLevel(progress)
    }

    fun getXpForNextLevel(progress: ClassProgress): Int {
        return LevelSystem.getXpForLevel(progress.level + 1)
    }

    // Cooldown time remaining — formatted by the UI.
    fun getCooldownRemainingMs(user: User): Long {
        return ClassSwitchSystem.cooldownRemainingMs(user, System.currentTimeMillis())
    }

    // ── CLASS SWITCH ───────────────────────────────────────

    // Pre-check — can the user switch to this class right now?
    // Called when the user taps a class in the grid to see the preview.
    fun checkCanSwitch(user: User, targetClass: FitnessClass): ClassSwitchSystem.CanSwitchResult {
        return ClassSwitchSystem.canSwitch(user, targetClass, System.currentTimeMillis())
    }

    // Preview — what would the head start be if they switch?
    fun previewHeadStart(user: User, targetClass: FitnessClass): ClassSwitchSystem.HeadStartResult {
        return ClassSwitchSystem.calculateHeadStart(user, targetClass)
    }

    // Execute the switch. Only call after checkCanSwitch returns Allowed.
    fun switchClass(targetClass: FitnessClass) {
        viewModelScope.launch {
            val user = userRepository.getUser(userId) ?: return@launch
            val nowMs = System.currentTimeMillis()

            val canSwitch = ClassSwitchSystem.canSwitch(user, targetClass, nowMs)
            if (canSwitch !is ClassSwitchSystem.CanSwitchResult.Allowed) {
                _classSwitchEvent.value = ClassSwitchEvent.Blocked
                return@launch
            }

            val switchResult = ClassSwitchSystem.applySwitch(
                user = user,
                targetClass = targetClass,
                allowedReason = canSwitch.reason,
                nowMs = nowMs
            )

            userRepository.saveUser(switchResult.updatedUser)

            _classSwitchEvent.value = ClassSwitchEvent.Success(
                previousClass = switchResult.previousClass,
                newClass = switchResult.newClass,
                headStart = switchResult.headStart
            )
        }
    }

    fun consumeClassSwitchEvent() {
        _classSwitchEvent.value = null
    }

    // ── PLAYER PROFILE ─────────────────────────────────────

    fun savePlayerProfile(profile: PlayerProfile) {
        viewModelScope.launch {
            userRepository.saveProfile(profile)
        }
    }

    // Refresh lifetime stats after returning from workout screen.
    fun refresh() {
        loadLifetimeStats()
    }

    // ── CLASS SWITCH EVENT ──────────────────────────────────

    sealed class ClassSwitchEvent {
        data class Success(
            val previousClass: FitnessClass,
            val newClass: FitnessClass,
            val headStart: ClassSwitchSystem.HeadStartResult
        ) : ClassSwitchEvent()

        object Blocked : ClassSwitchEvent()
    }
}

// ============================================================
// Factory
// ============================================================

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                userRepository = userRepository,
                workoutRepository = workoutRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

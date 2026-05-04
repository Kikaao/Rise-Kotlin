package com.rise.fitrpg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rise.fitrpg.AppConstants
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.repository.CardioRepository
import com.rise.fitrpg.data.repository.QuestRepository
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.systems.QuestSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============================================================
// QuestViewModel
// Layer: ViewModel
// ============================================================
// Feeds the quest screen with active and completed quests.
//
// Active quests are filtered by expiry — expired quests are
// invisible even if they haven't been cleaned up from the DB.
//
// Quest progress is NOT updated here — that happens in
// WorkoutViewModel after every session. This ViewModel is
// read-only except for initial quest generation (if the user
// opens the quest screen and quests haven't been generated yet).
// ============================================================

class QuestViewModel(
    private val questRepository: QuestRepository,
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository
) : ViewModel() {

    private val userId = AppConstants.CURRENT_USER_ID

    // ── STATE ──────────────────────────────────────────────

    private val _activeQuests = MutableStateFlow<List<Quest>>(emptyList())
    val activeQuests: StateFlow<List<Quest>> = _activeQuests.asStateFlow()

    private val _completedQuests = MutableStateFlow<List<Quest>>(emptyList())
    val completedQuests: StateFlow<List<Quest>> = _completedQuests.asStateFlow()

    // True while loading quests or generating new ones.
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── INIT ───────────────────────────────────────────────

    init {
        loadQuests()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            _isLoading.value = true
            val nowMs = System.currentTimeMillis()

            // Generate quests if none exist for this week.
            // This handles the case where the user opens the quest screen
            // before the HomeViewModel has run its week reset check.
            val hasQuests = questRepository.hasActiveQuests(userId, nowMs)
            if (!hasQuests) {
                generateNewQuests(nowMs)
            }

            _activeQuests.value = questRepository.getActiveQuests(userId, nowMs)
            _completedQuests.value = questRepository.getCompletedQuests(userId)
            _isLoading.value = false
        }
    }

    private suspend fun generateNewQuests(nowMs: Long) {
        val user = userRepository.getUser(userId) ?: return
        val allWorkouts = workoutRepository.getAllSessions(userId)
        val allCardio = cardioRepository.getAllSessions(userId)

        val result = QuestSystem.generateQuests(
            user = user,
            allWorkoutSessions = allWorkouts,
            allCardioSessions = allCardio,
            nowMs = nowMs
        )

        questRepository.saveQuests(result.allQuests, userId)
    }

    // Called when returning from workout screen — quests may have progressed.
    fun refresh() {
        loadQuests()
    }
}

// ============================================================
// Factory
// ============================================================

class QuestViewModelFactory(
    private val questRepository: QuestRepository,
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestViewModel::class.java)) {
            return QuestViewModel(
                questRepository = questRepository,
                userRepository = userRepository,
                workoutRepository = workoutRepository,
                cardioRepository = cardioRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

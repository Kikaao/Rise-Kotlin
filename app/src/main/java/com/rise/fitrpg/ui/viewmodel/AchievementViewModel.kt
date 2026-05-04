package com.rise.fitrpg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rise.fitrpg.data.models.Achievement
import com.rise.fitrpg.data.models.AchievementCategory
import com.rise.fitrpg.data.models.UserAchievement
import com.rise.fitrpg.data.repository.AchievementRepository
import com.rise.fitrpg.systems.AchievementSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// ============================================================
// AchievementViewModel
// Layer: ViewModel
// ============================================================
// Feeds the achievement gallery screen.
//
// Combines two data sources:
//   1. AchievementSystem.allAchievements — the static definitions
//      (name, description, category, condition, rewards)
//   2. AchievementRepository — per-user progress and unlock state
//
// The UI needs both merged: the definition tells it what to
// display, and the progress tells it whether it's locked,
// what the progress bar shows, and when it was unlocked.
//
// Secret achievements: the definition has isSecret = true.
// The UI hides name/description until isUnlocked = true in
// the progress. This ViewModel exposes both so the UI can
// decide what to show.
//
// Purely read-only — achievement checking happens in
// WorkoutViewModel after each session.
// ============================================================

class AchievementViewModel(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    // ── STATIC DEFINITIONS ─────────────────────────────────
    // These never change at runtime — loaded once from AchievementSystem.
    val allDefinitions: List<Achievement> = AchievementSystem.allAchievements

    // ── REACTIVE PROGRESS ──────────────────────────────────
    // Updates automatically when WorkoutViewModel saves new progress.
    val userProgress: StateFlow<List<UserAchievement>> =
        achievementRepository.getAllFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── MERGED VIEW ────────────────────────────────────────
    // Each item pairs the static definition with the user's progress.
    // If no progress exists for an achievement, it gets a default
    // (locked, zero progress) UserAchievement.

    data class AchievementDisplayItem(
        val definition: Achievement,
        val progress: UserAchievement
    )

    val displayItems: StateFlow<List<AchievementDisplayItem>> =
        userProgress.map { progressList ->
            val progressMap = progressList.associateBy { it.achievementId }

            allDefinitions.map { definition ->
                AchievementDisplayItem(
                    definition = definition,
                    progress = progressMap[definition.id]
                        ?: UserAchievement(achievementId = definition.id)
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── CATEGORY FILTER ────────────────────────────────────
    // The UI can filter by category. This tracks the selected filter.
    // null = show all.

    private val _selectedCategory = MutableStateFlow<AchievementCategory?>(null)
    val selectedCategory: StateFlow<AchievementCategory?> = _selectedCategory.asStateFlow()

    fun selectCategory(category: AchievementCategory?) {
        _selectedCategory.value = category
    }

    // All available categories — used to build the filter tabs in the UI.
    val categories: List<AchievementCategory> = AchievementCategory.entries

    // ── STATS ──────────────────────────────────────────────
    // Summary counts for the header of the achievement gallery.

    val unlockedCount: StateFlow<Int> = userProgress.map { list ->
        list.count { it.isUnlocked }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: Int = allDefinitions.size
}

// ============================================================
// Factory
// ============================================================

class AchievementViewModelFactory(
    private val achievementRepository: AchievementRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementViewModel::class.java)) {
            return AchievementViewModel(
                achievementRepository = achievementRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

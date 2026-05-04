package com.rise.fitrpg.data.repository

import com.rise.fitrpg.data.database.AchievementDao
import com.rise.fitrpg.data.database.toDomain
import com.rise.fitrpg.data.database.toEntity
import com.rise.fitrpg.data.models.UserAchievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ============================================================
// AchievementRepository
// Layer: Repository
// ============================================================
// Handles loading and saving achievement progress.
//
// AchievementSystem handles all the evaluation logic — it reads
// the current progress, checks conditions, and returns what changed.
// This repository only handles storing that result.
//
// The Achievement definitions (name, description, condition, reward)
// live in AchievementSystem.allAchievements — they are hardcoded,
// never stored in the database. Only UserAchievement (per-user
// progress and unlock state) is persisted.
// ============================================================

class AchievementRepository(
    private val achievementDao: AchievementDao
) {

    // ── READ ──────────────────────────────────────────────

    // Reactive stream — used by the achievement gallery screen.
    // Updates automatically when a new achievement is unlocked.
    fun getAllFlow(): Flow<List<UserAchievement>> {
        return achievementDao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // One-shot read of all progress — passed into AchievementSystem.checkAchievements()
    // as the currentProgress parameter after every workout.
    suspend fun getAll(): List<UserAchievement> {
        return achievementDao.getAll().map { it.toDomain() }
    }

    suspend fun getById(achievementId: String): UserAchievement? {
        return achievementDao.getById(achievementId)?.toDomain()
    }

    suspend fun getUnlocked(): List<UserAchievement> {
        return achievementDao.getUnlocked().map { it.toDomain() }
    }

    // ── SAVE ──────────────────────────────────────────────

    // Saves updated progress for a single achievement.
    suspend fun saveProgress(achievement: UserAchievement) {
        achievementDao.insertOrUpdate(achievement.toEntity())
    }

    // Saves the full result of an AchievementSystem check — all updated
    // progress rows in one batch call. Called after every workout session.
    // More efficient than saving one by one when multiple achievements advance.
    suspend fun saveAll(achievements: List<UserAchievement>) {
        achievementDao.insertOrUpdateAll(achievements.map { it.toEntity() })
    }
}

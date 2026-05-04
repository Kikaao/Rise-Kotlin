package com.rise.fitrpg.data.repository

import com.rise.fitrpg.data.database.QuestDao
import com.rise.fitrpg.data.database.toDomain
import com.rise.fitrpg.data.database.toEntity
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.User // 8a xriasti kapote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ============================================================
// QuestRepository
// Layer: Repository
// ============================================================
// Manages quest persistence — generation, progress tracking,
// and expiry filtering.
//
// QuestSystem handles the logic (what quests to generate, how
// to update progress). This repository only handles storage.
//
// Phase 2: when AI replaces QuestSystem's generation algorithm,
// this file does not change — same Quest model, same save call.
// ============================================================

class QuestRepository(
    private val questDao: QuestDao
) {

    // ── SAVE ──────────────────────────────────────────────

    // Saves a freshly generated batch of quests for a user.
    // Called once per week when the week resets.
    suspend fun saveQuests(quests: List<Quest>, userId: Int) {
        val entities = quests.map { it.toEntity(userId = userId) }
        questDao.insertQuests(entities)
    }

    // Saves a single quest after its progress was updated.
    // Called after every workout session where progress advanced.
    suspend fun updateQuest(quest: Quest, userId: Int) {
        questDao.updateQuest(quest.toEntity(userId = userId))
    }

    // ── READ ──────────────────────────────────────────────

    // Reactive stream of active quests (not completed, not expired).
    // Used by the quest screen — updates automatically when progress changes.
    fun getActiveQuestsFlow(userId: Int, nowMs: Long): Flow<List<Quest>> {
        return questDao.getActiveQuestsFlow(userId, nowMs).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // One-shot read of active quests — used by QuestSystem after a workout
    // to check which quests need progress updates.
    suspend fun getActiveQuests(userId: Int, nowMs: Long): List<Quest> {
        return questDao.getActiveQuests(userId, nowMs).map { it.toDomain() }
    }

    // Completed quest history — used by AchievementSystem to evaluate
    // conditions like "complete 50 quests total".
    suspend fun getCompletedQuests(userId: Int): List<Quest> {
        return questDao.getCompletedQuests(userId).map { it.toDomain() }
    }

    suspend fun getCompletedQuestCount(userId: Int): Int {
        return questDao.getCompletedQuestCount(userId)
    }

    // ── WEEK RESET ────────────────────────────────────────

    // Checks if the user has any active quests for the current week.
    // If false, the ViewModel should trigger QuestSystem to generate a new batch.
    // Called on app launch and after a workout is saved.
    suspend fun hasActiveQuests(userId: Int, nowMs: Long): Boolean {
        return questDao.getActiveQuests(userId, nowMs).isNotEmpty()
    }
}

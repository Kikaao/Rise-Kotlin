package com.rise.fitrpg.data.repository

import com.rise.fitrpg.data.database.CardioDao
import com.rise.fitrpg.data.database.toDomain
import com.rise.fitrpg.data.database.toEntity
import com.rise.fitrpg.data.models.CardioSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ============================================================
// CardioRepository
// Layer: Repository
// ============================================================
// Handles saving and loading cardio sessions.
// Simpler than WorkoutRepository — cardio sessions are a single
// table with no child rows (no sets to assemble separately).
// ============================================================

class CardioRepository(
    private val cardioDao: CardioDao
) {

    // ── SAVE ──────────────────────────────────────────────

    suspend fun saveSession(session: CardioSession) {
        cardioDao.insertSession(session.toEntity())
    }

    // ── READ ──────────────────────────────────────────────

    // Reactive stream — used by the history screen.
    fun getAllSessionsFlow(userId: Int): Flow<List<CardioSession>> {
        return cardioDao.getAllSessionsFlow(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // One-shot read — used by AchievementSystem and QuestSystem
    // which need the full cardio history to evaluate conditions.
    suspend fun getAllSessions(userId: Int): List<CardioSession> {
        return cardioDao.getAllSessions(userId).map { it.toDomain() }
    }

    suspend fun getSessionCount(userId: Int): Int {
        return cardioDao.getSessionCount(userId)
    }

    // Total distance across all sessions — used by distance-based achievements
    // (e.g. "Run 100km total"). Null if no distance sessions have been logged yet.
    suspend fun getTotalDistance(userId: Int): Double {
        return cardioDao.getTotalDistance(userId) ?: 0.0
    }
}

package com.rise.fitrpg.data.repository

import com.rise.fitrpg.data.database.WorkoutDao
import com.rise.fitrpg.data.database.toDomain
import com.rise.fitrpg.data.database.toEntity
import com.rise.fitrpg.data.models.PersonalRecord
import com.rise.fitrpg.data.models.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

// ============================================================
// WorkoutRepository
// Layer: Repository
// ============================================================
// Handles saving and loading workout sessions, sets, and PRs.
// All three are managed here because they are always used together:
// saving a workout = insert session + insert sets + update PRs.
//
// WorkoutSession is assembled from two tables:
//   workout_sessions  → the session header (date, class, XP earned)
//   workout_sets      → the individual sets (reps, weight, duration)
// ============================================================

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    // ── SAVE WORKOUT ──────────────────────────────────────

    // Saves a completed workout session with all its sets.
    // Called once per workout when the user finishes and confirms.
    // Returns the inserted session ID — needed to link sets via FK.
    suspend fun saveWorkout(session: WorkoutSession) {
        workoutDao.insertSession(session.toEntity())

        // Sets need the session ID as their FK — use the session's own ID,
        // which was set by the caller (ViewModel) before passing here.
        val setEntities = session.sets.map { it.toEntity(sessionId = session.id) }
        workoutDao.insertSets(setEntities)
    }

    // ── READ SESSIONS ─────────────────────────────────────

    // Reactive stream of all sessions with their sets assembled.
    // Used by the history screen — updates automatically when a new workout is saved.
    fun getAllSessionsFlow(userId: Int): Flow<List<WorkoutSession>> {
        return workoutDao.getAllSessionsFlow(userId).flatMapLatest { sessionEntities ->
            flow {
                emit(sessionEntities.map { sessionEntity ->
                    val sets = workoutDao.getSetsForSession(sessionEntity.id)
                        .map { it.toDomain() }
                    sessionEntity.toDomain(sets)
                })
            }
        }
    }

    // One-shot read of all sessions — used by AchievementSystem and QuestSystem
    // which need the full history to evaluate conditions.
    suspend fun getAllSessions(userId: Int): List<WorkoutSession> {
        return workoutDao.getAllSessions(userId).map { sessionEntity ->
            val sets = workoutDao.getSetsForSession(sessionEntity.id)
                .map { it.toDomain() }
            sessionEntity.toDomain(sets)
        }
    }

    // Recent sessions only — used by the home screen summary widget.
    // Avoids loading the full history when only the last few workouts are needed.
    suspend fun getRecentSessions(userId: Int, limit: Int = 5): List<WorkoutSession> {
        return workoutDao.getRecentSessions(userId, limit).map { sessionEntity ->
            val sets = workoutDao.getSetsForSession(sessionEntity.id)
                .map { it.toDomain() }
            sessionEntity.toDomain(sets)
        }
    }

    suspend fun getSessionCount(userId: Int): Int {
        return workoutDao.getSessionCount(userId)
    }

    suspend fun getTotalSetCount(userId: Int): Int {
        return workoutDao.getTotalSetCount(userId)
    }

    // ── PERSONAL RECORDS ──────────────────────────────────

    // Saves or updates a PR. The DAO uses REPLACE conflict strategy,
    // so if a PR already exists for this exerciseId it gets overwritten.
    suspend fun savePersonalRecord(pr: PersonalRecord) {
        workoutDao.insertOrUpdatePR(pr.toEntity())
    }

    suspend fun getAllPRs(userId: Int): List<PersonalRecord> {
        return workoutDao.getAllPRs(userId).map { it.toDomain() }
    }

    suspend fun getPR(userId: Int, exerciseId: Int): PersonalRecord? {
        return workoutDao.getPR(userId, exerciseId)?.toDomain()
    }

    suspend fun getPRCount(userId: Int): Int {
        return workoutDao.getPRCount(userId)
    }
}

package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// WorkoutDao — Database queries for workouts, sets, and PRs
// ============================================================
// Grouped into one DAO because these are always used together:
// saving a workout = insert session + insert sets + check PRs.
// ============================================================

@Dao
interface WorkoutDao {

    // ── SESSIONS ──────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getAllSessionsFlow(userId: Int): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllSessions(userId: Int): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentSessions(userId: Int, limit: Int): List<WorkoutSessionEntity>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :userId")
    suspend fun getSessionCount(userId: Int): Int

    // ── SETS ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY setNumber")
    suspend fun getSetsForSession(sessionId: Int): List<WorkoutSetEntity>

    @Query("SELECT COUNT(*) FROM workout_sets WHERE sessionId IN (SELECT id FROM workout_sessions WHERE userId = :userId)")
    suspend fun getTotalSetCount(userId: Int): Int

    // ── PERSONAL RECORDS ──────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePR(pr: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE userId = :userId")
    suspend fun getAllPRs(userId: Int): List<PersonalRecordEntity>

    @Query("SELECT * FROM personal_records WHERE userId = :userId AND exerciseId = :exerciseId")
    suspend fun getPR(userId: Int, exerciseId: Int): PersonalRecordEntity?

    @Query("SELECT COUNT(*) FROM personal_records WHERE userId = :userId")
    suspend fun getPRCount(userId: Int): Int
}

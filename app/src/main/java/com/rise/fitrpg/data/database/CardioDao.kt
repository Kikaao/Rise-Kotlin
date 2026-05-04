package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ============================================================
// CardioDao — Database queries for CardioSessionEntity
// ============================================================

@Dao
interface CardioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CardioSessionEntity)

    @Query("SELECT * FROM cardio_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getAllSessionsFlow(userId: Int): Flow<List<CardioSessionEntity>>

    @Query("SELECT * FROM cardio_sessions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllSessions(userId: Int): List<CardioSessionEntity>

    @Query("SELECT COUNT(*) FROM cardio_sessions WHERE userId = :userId")
    suspend fun getSessionCount(userId: Int): Int

    @Query("SELECT SUM(distanceKm) FROM cardio_sessions WHERE userId = :userId AND distanceKm IS NOT NULL")
    suspend fun getTotalDistance(userId: Int): Double?
}

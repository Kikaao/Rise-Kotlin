package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// QuestDao — Database queries for QuestEntity
// ============================================================

@Dao
interface QuestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    // Active quests: not completed and not expired
    @Query("SELECT * FROM quests WHERE userId = :userId AND isCompleted = 0 AND expiryMs > :nowMs")
    fun getActiveQuestsFlow(userId: Int, nowMs: Long): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE userId = :userId AND isCompleted = 0 AND expiryMs > :nowMs")
    suspend fun getActiveQuests(userId: Int, nowMs: Long): List<QuestEntity>

    // Completed quests for history
    @Query("SELECT * FROM quests WHERE userId = :userId AND isCompleted = 1 ORDER BY weekStartMs DESC")
    suspend fun getCompletedQuests(userId: Int): List<QuestEntity>

    @Query("SELECT COUNT(*) FROM quests WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedQuestCount(userId: Int): Int
}

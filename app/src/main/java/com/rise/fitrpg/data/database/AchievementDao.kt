package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// AchievementDao — Database queries for UserAchievementEntity
// ============================================================

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(achievement: UserAchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(achievements: List<UserAchievementEntity>)

    @Query("SELECT * FROM user_achievements")
    fun getAllFlow(): Flow<List<UserAchievementEntity>>

    @Query("SELECT * FROM user_achievements")
    suspend fun getAll(): List<UserAchievementEntity>

    @Query("SELECT * FROM user_achievements WHERE achievementId = :achievementId")
    suspend fun getById(achievementId: String): UserAchievementEntity?

    @Query("SELECT * FROM user_achievements WHERE isUnlocked = 1")
    suspend fun getUnlocked(): List<UserAchievementEntity>
}

// ============================================================
// InventoryDao — Database queries for InventoryEntity
// ============================================================

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryEntity)

    @Query("SELECT * FROM inventory WHERE userId = :userId")
    fun getAllForUserFlow(userId: Int): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE userId = :userId")
    suspend fun getAllForUser(userId: Int): List<InventoryEntity>

    @Query("SELECT * FROM inventory WHERE userId = :userId AND itemType = :itemType")
    suspend fun getItem(userId: Int, itemType: String): InventoryEntity?

    @Query("UPDATE inventory SET quantity = quantity - 1 WHERE userId = :userId AND itemType = :itemType AND quantity > 0")
    suspend fun consumeOne(userId: Int, itemType: String)
}

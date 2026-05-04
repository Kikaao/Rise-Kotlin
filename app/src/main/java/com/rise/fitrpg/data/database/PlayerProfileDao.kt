package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// PlayerProfileDao — Database queries for PlayerProfileEntity
// ============================================================

@Dao
interface PlayerProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: PlayerProfileEntity)

    @Query("SELECT * FROM player_profiles WHERE userId = :userId")
    fun getProfileFlow(userId: Int): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profiles WHERE userId = :userId")
    suspend fun getProfile(userId: Int): PlayerProfileEntity?

    @Update
    suspend fun update(profile: PlayerProfileEntity)
}

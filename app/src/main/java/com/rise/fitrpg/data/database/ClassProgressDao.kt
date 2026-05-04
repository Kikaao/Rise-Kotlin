package com.rise.fitrpg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// ClassProgressDao — Database queries for ClassProgressEntity
// ============================================================

@Dao
interface ClassProgressDao {

    @Query("SELECT * FROM class_progress WHERE userId = :userId")
    fun getAllForUserFlow(userId: Int): Flow<List<ClassProgressEntity>>

    @Query("SELECT * FROM class_progress WHERE userId = :userId")
    suspend fun getAllForUser(userId: Int): List<ClassProgressEntity>

    @Query("SELECT * FROM class_progress WHERE userId = :userId AND fitnessClass = :fitnessClass")
    suspend fun getForClass(userId: Int, fitnessClass: String): ClassProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progress: List<ClassProgressEntity>)

    @Update
    suspend fun update(progress: ClassProgressEntity)

    @Update
    suspend fun updateAll(progress: List<ClassProgressEntity>)
}

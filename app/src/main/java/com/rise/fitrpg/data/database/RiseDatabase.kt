package com.rise.fitrpg.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ============================================================
// RiseDatabase — The single Room database for the app
// ============================================================
// Lists every entity (table) and every DAO (query interface).
// Version starts at 1 — increment when the schema changes.
//
// Uses singleton pattern: only one instance exists at a time.
// Access via RiseDatabase.getInstance(context).
//
// exportSchema = true saves the schema as a JSON file on each build,
// which Room uses for auto-migrations between versions.
// ============================================================

@Database(
    entities = [
        UserEntity::class,
        ClassProgressEntity::class,
        PlayerProfileEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        CardioSessionEntity::class,
        PersonalRecordEntity::class,
        QuestEntity::class,
        UserAchievementEntity::class,
        InventoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RiseDatabase : RoomDatabase() {

    // DAOs — Room generates the implementations at compile time
    abstract fun userDao(): UserDao
    abstract fun classProgressDao(): ClassProgressDao
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun cardioDao(): CardioDao
    abstract fun questDao(): QuestDao
    abstract fun achievementDao(): AchievementDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: RiseDatabase? = null

        // Singleton — only one database connection at a time
        // synchronized block prevents two threads from creating two instances
        fun getInstance(context: Context): RiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RiseDatabase::class.java,
                    "rise_database"         // database file name on disk
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

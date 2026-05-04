package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// UserEntity — Room table for the player's core game state
// Maps to: User (domain model in data.models.Models.kt)
// ============================================================
// Stats (strength, dexterity, endurance, flexibility, power, focus)
// are embedded as 6 columns directly — every user has exactly one Stats object.
// classesSwitchedTo is stored as a comma-separated string (e.g. "CHAMPION,STRIKER")
// and converted via TypeConverter.
// All Long date fields are Unix milliseconds — System.currentTimeMillis()
// ============================================================

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val createdAt: Long = 0L,

    // Current game state
    val currentClass: String = "ADVENTURER",       // FitnessClass enum name
    val streakTier: String = "PEASANT",            // StreakTier enum name
    val currentStreak: Int = 0,
    val weeklyWorkoutCount: Int = 0,
    val weekStartDate: Long = 0L,
    val lastWorkoutDate: Long = 0L,
    val lastClassSwitch: Long = 0L,
    val hasUsedFreeSwitch: Boolean = false,
    val gold: Int = 0,

    // Stored as comma-separated enum names: "CHAMPION,STRIKER,ROGUE"
    // Empty string = no classes switched to yet
    val classesSwitchedTo: String = "",

    // Stats embedded directly — one user always has exactly one Stats
    val statStrength: Int = 0,
    val statDexterity: Int = 0,
    val statEndurance: Int = 0,
    val statFlexibility: Int = 0,
    val statPower: Int = 0,
    val statFocus: Int = 0
)

package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// UserAchievementEntity — Room table for achievement progress
// Maps to: UserAchievement (domain model in data.models.AchievementModels.kt)
// ============================================================
// One row per achievement the player has interacted with.
// Achievement DEFINITIONS (name, description, condition) are NOT in
// the database — they're hardcoded in AchievementSystem. Only the
// player's PROGRESS toward each achievement is stored here.
// All Long date fields are Unix milliseconds.
// ============================================================

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey val achievementId: String,  // matches Achievement.id e.g. "streak_7"
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,           // Unix ms — when this was unlocked
    val currentProgress: Int = 0,
    val timesCompleted: Int = 0             // for repeatable achievements
)

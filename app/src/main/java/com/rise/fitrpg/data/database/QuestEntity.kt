package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// QuestEntity — Room table for quests
// Maps to: Quest (domain model in data.models.QuestModels.kt)
// ============================================================
// One row per quest ever generated. Active quests have isCompleted = false.
// Expired/completed quests stay for history.
// All Long date fields are Unix milliseconds.
// ============================================================

@Entity(
    tableName = "quests",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class QuestEntity(
    @PrimaryKey val id: String,         // unique key e.g. "quest_knight_1_week_42"
    val userId: Int,
    val type: String,                   // QuestType enum name
    val rarity: String,                 // QuestRarity enum name
    val title: String,
    val description: String,

    val targetClass: String,            // FitnessClass enum name
    val isBalanceQuest: Boolean,

    val targetValue: Int,
    val targetMuscleGroup: String? = null, // MuscleGroup enum name, null if not LOG_MUSCLE_GROUP_SETS

    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,

    val xpReward: Int,
    val goldReward: Int = 0,

    val weekStartMs: Long,              // Unix ms — when this quest was generated
    val expiryMs: Long                  // Unix ms — when this quest expires
)

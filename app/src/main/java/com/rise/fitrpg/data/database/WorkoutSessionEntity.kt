package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// WorkoutSessionEntity — Room table for each workout logged
// Maps to: WorkoutSession (domain model in data.models.WorkoutModels.kt)
// ============================================================
// One row per workout. The sets list from the domain model is NOT
// stored here — sets live in WorkoutSetEntity and link back via sessionId.
// All Long date fields are Unix milliseconds.
// ============================================================

@Entity(
    tableName = "workout_sessions",
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
data class WorkoutSessionEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val date: Long,                      // Unix ms
    val fitnessClass: String,            // FitnessClass enum name — class active during this workout
    val workoutType: String,
    val totalXpEarned: Int = 0,
    // Total clock time of the session in seconds. Default 0 for backward compatibility.
    val durationSeconds: Int = 0,
    val notes: String? = null
)
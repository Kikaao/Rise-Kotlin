package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// WorkoutSetEntity — Room table for each set within a workout
// Maps to: WorkoutSet (domain model in data.models.WorkoutModels.kt)
// ============================================================
// Many rows per session. If the user did 4 sets of bench and 3 sets
// of curls, that's 7 rows here all pointing to the same sessionId.
// This is the most-written table in the entire database.
// ============================================================

@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class WorkoutSetEntity(
    @PrimaryKey val id: Int,
    val sessionId: Int,                 // links to WorkoutSessionEntity.id
    val exerciseId: Int,
    val setNumber: Int,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val xpEarned: Int = 0
)

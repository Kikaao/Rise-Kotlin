package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// CardioSessionEntity — Room table for each cardio session
// Maps to: CardioSession (domain model in data.models.CardioModels.kt)
// ============================================================
// One row per cardio workout. Distance-based sessions use distanceKm
// and durationMinutes. Interval-based sessions use sets and metersPerSet.
// All Long date fields are Unix milliseconds.
// ============================================================

@Entity(
    tableName = "cardio_sessions",
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
data class CardioSessionEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val cardioExerciseId: Int,
    val date: Long,                     // Unix ms

    // Distance-based fields
    val distanceKm: Double? = null,
    val durationMinutes: Double? = null,

    // Interval-based fields
    val sets: Int? = null,
    val metersPerSet: Int? = null,

    val xpEarned: Int = 0,
    val notes: String? = null
)

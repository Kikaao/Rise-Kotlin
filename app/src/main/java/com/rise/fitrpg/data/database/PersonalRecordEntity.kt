package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// PersonalRecordEntity — Room table for personal records
// Maps to: PersonalRecord (domain model in data.models.WorkoutModels.kt)
// ============================================================
// One row per exercise the user has ever done. Updated whenever
// they beat their previous best. If they've done 30 different
// exercises in their lifetime, that's 30 rows here.
// All Long date fields are Unix milliseconds.
// ============================================================

@Entity(
    tableName = "personal_records",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("userId", "exerciseId", unique = true)]
)
data class PersonalRecordEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val exerciseId: Int,
    val date: Long,                     // Unix ms — when this PR was set
    val bestWeightKg: Double? = null,
    val bestReps: Int? = null,
    val bestDurationSeconds: Int? = null
)

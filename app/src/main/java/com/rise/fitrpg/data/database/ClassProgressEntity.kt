package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// ClassProgressEntity — Room table for per-class XP and level
// Maps to: ClassProgress (domain model in data.models.Models.kt)
// ============================================================
// 6 rows per user, one for each FitnessClass.
// userId links back to UserEntity — if the user is deleted, progress goes too.
// ============================================================

@Entity(
    tableName = "class_progress",
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
data class ClassProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val fitnessClass: String,           // FitnessClass enum name
    val xp: Int = 0,                    // current level XP (resets on level up)
    val totalXp: Int = 0,               // lifetime XP (never resets)
    val level: Int = 0
)

package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// PlayerProfileEntity — Room table for the player's physical profile
// Maps to: PlayerProfile (domain model in data.models.PlayerProfile.kt)
// ============================================================
// One row per user. Separate from UserEntity because it's a different
// concern — User is game state, PlayerProfile is physical reality.
// Used by MuscleSystem to normalize muscle scores by bodyweight/gender/age.
// ============================================================

@Entity(
    tableName = "player_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId", unique = true)]
)
data class PlayerProfileEntity(
    @PrimaryKey val userId: Int,
    val gender: String,                 // Gender enum name
    val ageYears: Int,
    val heightCm: Double,
    val weightKg: Double
)

package com.rise.fitrpg.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================
// InventoryEntity — Room table for the player's inventory
// Maps to: InventoryItem (domain model in data.models.InventoryModels.kt)
// ============================================================
// One row per item type per user. Currently just STREAK_FREEZE,
// but designed to expand when the full item system is built.
// ============================================================

@Entity(
    tableName = "inventory",
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
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val itemType: String,               // ItemType enum name
    val quantity: Int
)

package models


// AI Genarated. I just created the idea. In need for study and progress
// ============================================================
// InventoryModels.kt
// Layer: Models
// ============================================================
// Minimal inventory model — designed to unblock StreakSystem.kt.
// Full item system (timers, durability, stacking limits, XP multipliers,
// class rechoice token) is on the waiting list for a dedicated design session.
// To add a new item later: add a value to ItemType — zero structural changes.
// ============================================================

// ─────────────────────────────────────────────────────
// ITEM TYPE
// Each enum value is one distinct item in the game.
// Expand this list when the full item system is designed.
// ─────────────────────────────────────────────────────

enum class ItemType {
    STREAK_FREEZE   // Protects streak when weekly goal is missed — consumed automatically
    // Future items added here: XP_BOOST, CLASS_RECHOICE, etc.
}

// ─────────────────────────────────────────────────────
// INVENTORY ITEM
// One entry per ItemType in the player's inventory.
// quantity = how many of this item the player currently holds.
// ─────────────────────────────────────────────────────

data class InventoryItem(
    val itemType: ItemType,
    val quantity: Int
)

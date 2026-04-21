package models

// ============================================================
// QuestModels.kt
// Layer: Models
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================

// ─────────────────────────────────────────────────────
// QUEST TYPE
// Four types of quests a player can receive.
// Phase 2: AI replaces the generation algorithm but uses
// the same Quest model — zero structural changes needed.
// ─────────────────────────────────────────────────────

enum class QuestType {
    COMPLETE_WORKOUTS,      // Complete X workout sessions this week
    RUN_DISTANCE,           // Log X km of cardio this week
    HIT_STREAK,             // Reach a total streak of X days
    LOG_MUSCLE_GROUP_SETS   // Log X sets targeting a specific muscle group
}

// ─────────────────────────────────────────────────────
// QUEST RARITY
// Drives reward scaling — higher rarity = harder target = better reward.
// ─────────────────────────────────────────────────────

enum class QuestRarity {
    COMMON,     // Easy target, XP only
    UNCOMMON,   // Moderate target, XP + small gold chance
    RARE        // Hard target, XP + guaranteed gold
}

// ─────────────────────────────────────────────────────
// QUEST
// A single active quest for a user.
// Generated weekly by QuestSystem and evaluated automatically
// when a workout or cardio session is logged.
//
// Class quests: targetClass = user's current class, isBalanceQuest = false
// Balance quests: targetClass = neglected class, isBalanceQuest = true
// ─────────────────────────────────────────────────────

data class Quest(
    val id: String,                         // unique key e.g. "quest_knight_1_week_42"
    val type: QuestType,
    val rarity: QuestRarity,
    val title: String,
    val description: String,

    val targetClass: FitnessClass,          // class this quest is optimized for
    val isBalanceQuest: Boolean,            // true = triggered by neglect detection

    val targetValue: Int,                   // X — the number to hit (sets, km, days, etc.)
    val targetMuscleGroup: MuscleGroup? = null, // non-null only for LOG_MUSCLE_GROUP_SETS

    val currentProgress: Int = 0,          // updated automatically on session log
    val isCompleted: Boolean = false,

    val xpReward: Int,                      // XP awarded on completion
    val goldReward: Int = 0,               // Gold awarded — 0 for COMMON quests

    val weekStartMs: Long,                  // Unix ms when this quest was generated
    val expiryMs: Long                      // weekStartMs + 7 days — quest expires here
) {
    // Progress as 0.0–1.0 — used by UI progress bar
    val progressFraction: Double
        get() = (currentProgress.toDouble() / targetValue).coerceIn(0.0, 1.0)
}

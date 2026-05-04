package com.rise.fitrpg.data.models


// Four types of quests a player can receive.
// Phase 2: AI replaces the generation algorithm but uses
// the same Quest model — zero structural changes needed.

enum class QuestType {
    COMPLETE_WORKOUTS,      // Complete X workout sessions this week
    RUN_DISTANCE,           // Log X km of cardio this week
    HIT_STREAK,             // Reach a total streak of X days
    LOG_MUSCLE_GROUP_SETS   // Log X sets targeting a specific muscle group
}

// Higher rarity = harder target = better reward — simple as that
enum class QuestRarity {
    COMMON,     // XP only
    UNCOMMON,   // XP + small gold chance
    RARE        // XP + guaranteed gold
}




// 2 types of quest:
// Class quest — indepence around the player's current class, isBalanceQuest = false
// Balance quest — triggered when the system detects a neglected area (e.g. a Champion
// who hasn't done cardio in 14 days), isBalanceQuest = true, targetClass = the neglected class
// Quest expires after 7 days whether completed or not

data class Quest(
    val id: String,                         // unique key e.g. "quest_knight_1_week_42"
    val type: QuestType,
    val rarity: QuestRarity,
    val title: String,
    val description: String,

    val targetClass: FitnessClass,          // class this quest is optimized for
    val isBalanceQuest: Boolean,            // true = triggered by neglect detection

    val targetValue: Int,                   // X the number to hit (sets, km, days, etc.)
    val targetMuscleGroup: MuscleGroup? = null, // non-null only for LOG_MUSCLE_GROUP_SETS

    val currentProgress: Int = 0,          // updated automatically on session log
    val isCompleted: Boolean = false,

    val xpReward: Int,                      // XP awarded on completion
    val goldReward: Int = 0,               // Gold awarded 0 for COMMON quests

    val weekStartMs: Long,                  // Unix ms when this quest was generated
    val expiryMs: Long                      // weekStartMs + 7 days quest expires here
) {
    //0.0–1.0 UI progress bar
    val progressFraction: Double
        get() = (currentProgress.toDouble() / targetValue).coerceIn(0.0, 1.0)
}

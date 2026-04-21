package models

// ============================================================
// GameConstants.kt
// Layer: Models
// ============================================================
// Shared constants used by multiple systems.
// Lives in the models layer so any system can read it without
// calling another system — which would violate layer rules.
//
// Rule: if a constant is needed by more than one system,
// it belongs here, not inside any single system file.
// ============================================================

object GameConstants {

    // --------------------------------------------------------
    // STREAK MULTIPLIER THRESHOLDS
    // Used by both XpSystem (to calculate XP) and StreakSystem
    // (to display the active multiplier on the home screen).
    // TUNABLE — adjust to change how streak bonuses feel.
    // --------------------------------------------------------

    const val STREAK_DAYS_TIER_1 = 5       // TUNABLE — days needed for first bonus
    const val STREAK_DAYS_TIER_2 = 10      // TUNABLE
    const val STREAK_DAYS_TIER_3 = 30      // TUNABLE

    const val STREAK_MULTIPLIER_TIER_1 = 1.05  // TUNABLE — 5+ day streak bonus
    const val STREAK_MULTIPLIER_TIER_2 = 1.10  // TUNABLE — 10+ day streak bonus
    const val STREAK_MULTIPLIER_TIER_3 = 1.30  // TUNABLE — 30+ day streak bonus

    // --------------------------------------------------------
    // WEEKLY WORKOUT MINIMUMS PER STREAK TIER
    // Fix: these values previously lived in two places —
    //   StreakSystem.getWeeklyMinimum() and QuestSystem.getBalanceQuestCount()
    // — as independent when-blocks. If one changed, the other drifted silently.
    // Centralizing them here matches the same pattern applied to streak multipliers
    // in v2.1 audit fix #25. Both systems now read from this single source of truth.
    //
    // Rule: PEASANT 1-2 days/week, KNIGHT 3-4, ELITE 5-7. The MINIMUM is the lower
    // bound (what the player must hit to maintain streak). The MAX is the upper bound
    // (used by QuestSystem.getTargetValue for HIT_STREAK quests).
    // --------------------------------------------------------

    const val WEEKLY_MIN_PEASANT = 1       // TUNABLE
    const val WEEKLY_MIN_KNIGHT  = 3       // TUNABLE
    const val WEEKLY_MIN_ELITE   = 5       // TUNABLE

    const val WEEKLY_MAX_PEASANT = 2       // TUNABLE — upper bound of PEASANT tier
    const val WEEKLY_MAX_KNIGHT  = 4       // TUNABLE — upper bound of KNIGHT tier
    const val WEEKLY_MAX_ELITE   = 7       // TUNABLE — upper bound of ELITE tier
}

package models

// Weekly workout minimums per streak tier
// StreakSystem and QuestSystem. Which worked fine until it didn't

// Fix = pull them here, both systems read the same values
// Now it's optimiced — change it once, it changes everywhere
// MIN is what the player needs to maintain streak
// MAX is the upper bound of the tier

// Finito00
object GameConstants {

    // STREAK MULTIPLIER THRESHOLDS
    // Both XpSystem and StreakSystem live here


    const val STREAK_DAYS_TIER_1 = 5       // TUNABLE — days needed for first bonus
    const val STREAK_DAYS_TIER_2 = 10      // TUNABLE
    const val STREAK_DAYS_TIER_3 = 30      // TUNABLE

    const val STREAK_MULTIPLIER_TIER_1 = 1.05  // TUNABLE — 5+ day streak bonus
    const val STREAK_MULTIPLIER_TIER_2 = 1.10  // TUNABLE — 10+ day streak bonus
    const val STREAK_MULTIPLIER_TIER_3 = 1.30  // TUNABLE — 30+ day streak bonus


    // Rule: PEASANT 1-2 days/week, KNIGHT 3-4, ELITE 5-7

    const val WEEKLY_MIN_PEASANT = 1       // TUNABLE — lower bound of PEASANT tier
    const val WEEKLY_MIN_KNIGHT  = 3       // TUNABLE - lower bound of KNIGHT tier
    const val WEEKLY_MIN_ELITE   = 5       // TUNABLE = lower bound of ELITE tier

    const val WEEKLY_MAX_PEASANT = 2       // TUNABLE — upper bound of PEASANT tier
    const val WEEKLY_MAX_KNIGHT  = 4       // TUNABLE — upper bound of KNIGHT tier
    const val WEEKLY_MAX_ELITE   = 7       // TUNABLE — upper bound of ELITE tier
}

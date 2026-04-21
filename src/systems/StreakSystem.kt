package systems

import models.GameConstants
import models.InventoryItem
import models.ItemType
import models.StreakTier
import models.User

// ============================================================
// StreakSystem.kt
// Layer: Systems
// Communicates with: Models only (User, StreakTier, ItemType,
//                    InventoryItem)
// Never communicates with: UI, ViewModel, Repository, Database
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================
//
// HOW THE STREAK WORKS:
//
// Streak is counted in DAYS. Every day the user logs at least one
// workout, currentStreak increments by 1.
//
// At the end of each week, the system checks if the user hit their
// weekly minimum (defined by their StreakTier):
//   PEASANT  → minimum 1 day/week
//   KNIGHT   → minimum 3 days/week
//   ELITE    → minimum 5 days/week
//
// If the user exceeded their minimum, every day counts.
// e.g. KNIGHT logs 6 days → currentStreak gets +6, not just +3.
//
// If the user missed their minimum:
//   → Auto-check inventory for a STREAK_FREEZE
//   → If freeze available: consume 1, streak survives, player notified
//   → If no freeze: streak resets to 0
//
// ============================================================

object StreakSystem {

    // --------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------

    // How long a "week" is in milliseconds — used to detect week boundaries
    private const val WEEK_IN_MS = 7L * 24L * 60L * 60L * 1000L


    // --------------------------------------------------------
    // RESULT TYPES
    // --------------------------------------------------------

    /**
     * Returned after logging a workout day.
     * Tells the caller (and eventually the UI) exactly what happened.
     */
    sealed class WorkoutLoggedResult {
        data class StreakIncremented(
            val updatedUser: User,
            val newStreakDays: Int
        ) : WorkoutLoggedResult()

        data class AlreadyLoggedToday(
            val currentStreakDays: Int
        ) : WorkoutLoggedResult()
    }

    /**
     * Returned after a weekly evaluation (called when a new week starts).
     * Three possible outcomes: goal met, freeze consumed, streak broken.
     */
    sealed class WeeklyEvaluationResult {
        data class GoalMet(
            val updatedUser: User,
            val workoutsLogged: Int,
            val minimumRequired: Int
        ) : WeeklyEvaluationResult()

        data class FreezeConsumed(
            val updatedUser: User,
            val workoutsLogged: Int,
            val minimumRequired: Int,
            val freezesRemaining: Int
        ) : WeeklyEvaluationResult()

        data class StreakBroken(
            val updatedUser: User,
            val workoutsLogged: Int,
            val minimumRequired: Int
        ) : WeeklyEvaluationResult()
    }


    // --------------------------------------------------------
    // PUBLIC FUNCTIONS
    // --------------------------------------------------------

    /**
     * Called every time a user logs a workout session.
     * Increments currentStreak by 1 if this is the first workout today.
     * If the user already logged a workout today, streak does not double-count.
     *
     * Also increments weeklyWorkoutCount — used at week end to check goal.
     *
     * @param user          The current user
     * @param nowMs         Current timestamp in Unix ms (System.currentTimeMillis())
     */
    fun onWorkoutLogged(user: User, nowMs: Long): WorkoutLoggedResult {
        val alreadyLoggedToday = isSameDay(user.lastWorkoutDate, nowMs)

        if (alreadyLoggedToday) {
            // Don't double-count streak days — but session still gets saved normally
            return WorkoutLoggedResult.AlreadyLoggedToday(
                currentStreakDays = user.currentStreak
            )
        }

        val updatedUser = user.copy(
            currentStreak = user.currentStreak + 1,
            weeklyWorkoutCount = user.weeklyWorkoutCount + 1,
            lastWorkoutDate = nowMs
        )

        return WorkoutLoggedResult.StreakIncremented(
            updatedUser = updatedUser,
            newStreakDays = updatedUser.currentStreak
        )
    }

    /**
     * Called when a new week begins (detected by comparing nowMs to weekStartDate).
     * Evaluates whether the user hit their weekly goal last week.
     *
     * Flow:
     * 1. Check if a new week has actually started — if not, do nothing
     * 2. Compare weeklyWorkoutCount against streakTier minimum
     * 3. If goal met → reset weekly count, advance weekStartDate
     * 4. If goal missed → check for freeze → consume or break streak
     *
     * @param user      The current user
     * @param nowMs     Current timestamp in Unix ms
     */
    fun evaluateWeek(user: User, nowMs: Long): WeeklyEvaluationResult? {
        // Only evaluate if a full week has actually passed
        if (!isNewWeek(user.weekStartDate, nowMs)) return null

        val minimum = getWeeklyMinimum(user.streakTier)
        val logged = user.weeklyWorkoutCount

        return if (logged >= minimum) {
            // Goal met — reset weekly counter, advance week start
            val updatedUser = user.copy(
                weeklyWorkoutCount = 0,
                weekStartDate = nowMs
            )
            WeeklyEvaluationResult.GoalMet(
                updatedUser = updatedUser,
                workoutsLogged = logged,
                minimumRequired = minimum
            )
        } else {
            // Goal missed — check for freeze
            val freezeCount = user.inventory[ItemType.STREAK_FREEZE]?.quantity ?: 0

            if (freezeCount > 0) {
                // Auto-consume one freeze — Duolingo style, no player input needed
                val updatedInventory = user.inventory.toMutableMap()
                updatedInventory[ItemType.STREAK_FREEZE] = InventoryItem(
                    itemType = ItemType.STREAK_FREEZE,
                    quantity = freezeCount - 1
                )

                val updatedUser = user.copy(
                    inventory = updatedInventory,
                    weeklyWorkoutCount = 0,
                    weekStartDate = nowMs
                    // currentStreak survives — freeze protected it
                )

                WeeklyEvaluationResult.FreezeConsumed(
                    updatedUser = updatedUser,
                    workoutsLogged = logged,
                    minimumRequired = minimum,
                    freezesRemaining = freezeCount - 1
                )
            } else {
                // No freeze — streak breaks
                val updatedUser = user.copy(
                    currentStreak = 0,
                    weeklyWorkoutCount = 0,
                    weekStartDate = nowMs
                )

                WeeklyEvaluationResult.StreakBroken(
                    updatedUser = updatedUser,
                    workoutsLogged = logged,
                    minimumRequired = minimum
                )
            }
        }
    }

    /**
     * Returns the current streak multiplier for a given streak day count.
     * Reads directly from GameConstants — no cross-system dependency needed.
     * Exposed here so the UI can display the active multiplier on the home screen.
     */
    fun getCurrentMultiplier(currentStreakDays: Int): Double {
        return when {
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_3 -> GameConstants.STREAK_MULTIPLIER_TIER_3
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_2 -> GameConstants.STREAK_MULTIPLIER_TIER_2
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_1 -> GameConstants.STREAK_MULTIPLIER_TIER_1
            else -> 1.0
        }
    }

    /**
     * Returns the minimum workouts per week required for a given StreakTier.
     * Fix: values now read from GameConstants.kt — previously this when-block and
     * QuestSystem's getBalanceQuestCount had duplicate copies of the same table.
     * Single source of truth eliminates drift if Stamatis tunes the minimums.
     *
     * PEASANT  → WEEKLY_MIN_PEASANT (1)
     * KNIGHT   → WEEKLY_MIN_KNIGHT  (3)
     * ELITE    → WEEKLY_MIN_ELITE   (5)
     */
    fun getWeeklyMinimum(streakTier: StreakTier): Int {
        return when (streakTier) {
            StreakTier.PEASANT -> GameConstants.WEEKLY_MIN_PEASANT
            StreakTier.KNIGHT  -> GameConstants.WEEKLY_MIN_KNIGHT
            StreakTier.ELITE   -> GameConstants.WEEKLY_MIN_ELITE
        }
    }


    // --------------------------------------------------------
    // PRIVATE HELPERS
    // --------------------------------------------------------

    /**
     * Checks if a new week has started relative to the user's weekStartDate.
     * A week is exactly 7 days (WEEK_IN_MS).
     */
    private fun isNewWeek(weekStartDateMs: Long, nowMs: Long): Boolean {
        return nowMs >= weekStartDateMs + WEEK_IN_MS
    }

    /**
     * Checks if two timestamps fall on the same calendar day in the device's local timezone.
     * Applies the timezone offset before dividing by day length — without this, users in
     * non-UTC timezones could have their streak miscounted around midnight.
     *
     * Phase 2: replace with Android Calendar API using device timezone for full accuracy.
     */
    private fun isSameDay(timestampA: Long, timestampB: Long): Boolean {
        val dayInMs = 24L * 60L * 60L * 1000L
        val tz = java.util.TimeZone.getDefault()
        val offsetA = tz.getOffset(timestampA)
        val offsetB = tz.getOffset(timestampB)
        return ((timestampA + offsetA) / dayInMs) == ((timestampB + offsetB) / dayInMs)
    }
}
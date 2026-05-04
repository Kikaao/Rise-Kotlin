package com.rise.fitrpg.systems

import com.rise.fitrpg.data.models.GameConstants
import com.rise.fitrpg.data.models.InventoryItem
import com.rise.fitrpg.data.models.ItemType
import com.rise.fitrpg.data.models.StreakTier
import com.rise.fitrpg.data.models.User

// StreakSystem.kt
// Layer: Systems
// Communicates with: Models only

// Streak is counted in DAYS. Every day the user logs at least one
// workout, currentStreak increments by 1.

// At the end of each week, the system checks if the user hit their
// weekly minimum (defined by their StreakTier):
//   PEASANT  → minimum 1 day/week
//   KNIGHT   → minimum 3 days/week
//   ELITE    → minimum 5 days/week

// If the user exceeded their minimum, every day counts.
// e.g. KNIGHT logs 6 days → currentStreak gets +6, not just +3.

// If the user missed their minimum:
//   → Auto-check inventory for a STREAK_FREEZE
//   → If freeze available: consume 1, streak survives, player notified
//   → If no freeze: streak resets to 0


object StreakSystem {


    // How long a "week" is in milliseconds — used to detect week boundaries
    private const val WEEK_IN_MS = 7L * 24L * 60L * 60L * 1000L


    // What comes back after a workout is logged

    sealed class WorkoutLoggedResult {
        data class StreakIncremented(
            val updatedUser: User,
            val newStreakDays: Int
        ) : WorkoutLoggedResult()

        data class AlreadyLoggedToday(
            val currentStreakDays: Int
        ) : WorkoutLoggedResult()
    }

    // what comes back after a weekly evaluation
    // outcomes: goal met, freeze consumed, streak broken
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


    // Called every time a workout session is logged
    // weeklyWorkoutCount also goes up here used at week end to check if the goal was hit
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

    // Called when a new week begins
    // Returns null if a full week hasn't passed yet
    // If the goal was met: reset weekly count and advance the week start
    // If the goal was missed: consume a freeze or break the streak
    fun evaluateWeek(user: User, nowMs: Long): WeeklyEvaluationResult? {
        // a full week has actually passed
        if (!isNewWeek(user.weekStartDate, nowMs)) return null

        val minimum = getWeeklyMinimum(user.streakTier)
        val logged = user.weeklyWorkoutCount

        return if (logged >= minimum) {
            // Goal met  reset weekly counter
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
            // Goal missed  check for freeze
            val freezeCount = user.inventory[ItemType.STREAK_FREEZE]?.quantity ?: 0

            if (freezeCount > 0) {
                // Auto-consume one freeze
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
                // No freeze streak breaks ;(
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

    // Returns the active XP multiplier for a given streak length
    fun getCurrentMultiplier(currentStreakDays: Int): Double {
        return when {
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_3 -> GameConstants.STREAK_MULTIPLIER_TIER_3
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_2 -> GameConstants.STREAK_MULTIPLIER_TIER_2
            currentStreakDays >= GameConstants.STREAK_DAYS_TIER_1 -> GameConstants.STREAK_MULTIPLIER_TIER_1
            else -> 1.0
        }
    }


    fun getWeeklyMinimum(streakTier: StreakTier): Int {
        return when (streakTier) {
            StreakTier.PEASANT -> GameConstants.WEEKLY_MIN_PEASANT
            StreakTier.KNIGHT  -> GameConstants.WEEKLY_MIN_KNIGHT
            StreakTier.ELITE   -> GameConstants.WEEKLY_MIN_ELITE
        }
    }


    //Checks if a new week has started relative to the user's weekStartDate.
    private fun isNewWeek(weekStartDateMs: Long, nowMs: Long): Boolean {
        return nowMs >= weekStartDateMs + WEEK_IN_MS
    }


    // AI STUF I HAVE A HEADACHE
    //Phase 2: replace with Android Calendar API using device timezone for full accuracy.

    private fun isSameDay(timestampA: Long, timestampB: Long): Boolean {
        val dayInMs = 24L * 60L * 60L * 1000L
        val tz = java.util.TimeZone.getDefault()
        val offsetA = tz.getOffset(timestampA)
        val offsetB = tz.getOffset(timestampB)
        return ((timestampA + offsetA) / dayInMs) == ((timestampB + offsetB) / dayInMs)
    }
}
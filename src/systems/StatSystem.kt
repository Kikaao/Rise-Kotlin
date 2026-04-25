package systems

import models.CardioExercise
import models.Exercise
import models.Stats
import models.StatContribution


// how it works
//
// Every workout set earns XP. A portion of that XP flows into
// the player's stats based on the exercise's StatContribution weights.
//
// Formula: statGain = finalXp × statWeight
// Example: Bench Press earns 100 XP. StatContribution: strength=0.8, power=0.2
//   → +80 strength, +20 power
//
// Stats are global they never reset on class switch.
// Stats are capped at MAX_STAT_VALUE (1000).
//
// Cardio also contributes to stats via its own StatContribution weights.
// The same formula applies  finalXp × statWeight.
//
// Focus stat is special: it grows ONLY from streak days and quest completions,
// never from exercise StatContribution weights. Even if an exercise has a
// focus weight, it is intentionally ignored in applyStatGains().
// See addFocusFromStreak() and addFocusFromQuestCompletion() below.
//
// ============================================================

object StatSystem {



    // Focus XP awarded per streak day when the weekly goal is met
    private const val FOCUS_XP_PER_STREAK_DAY = 10      // TUNABLE

    // Focus XP awarded when a quest is completed
    private const val FOCUS_XP_PER_QUEST = 50           // TUNABLE

    // Stat growth is capped at MAX_STAT_VALUE
    private const val MAX_STAT = models.MAX_STAT_VALUE



    // StatsBefore and statsAfter let the UI show exactly what changed
    // e.g. "+80 Strength, +20 Power" on the post-workout summary screen
    data class StatGainResult(
        val statsBefore: Stats,
        val statsAfter: Stats,
        val strengthGained: Int,
        val dexterityGained: Int,
        val enduranceGained: Int,
        val flexibilityGained: Int,
        val powerGained: Int,
        val focusGained: Int
    )


    // Called once per WorkoutSet after XpSystem has calculated the final XP

    fun applyStrengthStatGains(
        currentStats: Stats,
        exercise: Exercise,
        finalXp: Int
    ): StatGainResult {
        return applyStatGains(currentStats, exercise.statContribution, finalXp)
    }

    // Called once per CardioSession after XpSystem has calculated the final XP
    fun applyCardioStatGains(
        currentStats: Stats,
        cardioExercise: CardioExercise,
        finalXp: Int
    ): StatGainResult {
        return applyStatGains(currentStats, cardioExercise.statContribution, finalXp)
    }

    // streakDays x FOCUS_XP_PER_STREAK_DAY rewards consistency over time
    fun addFocusFromStreak(
        currentStats: Stats,
        streakDays: Int
    ): StatGainResult {
        val focusGain = (streakDays * FOCUS_XP_PER_STREAK_DAY)
            .coerceAtMost(MAX_STAT - currentStats.focus)
            .coerceAtLeast(0)

        val updatedStats = currentStats.copy(
            focus = currentStats.focus + focusGain
        )

        return buildResult(currentStats, updatedStats)
    }

    // called after QuestSystem returns a newly completed quest
    fun addFocusFromQuestCompletion(
        currentStats: Stats
    ): StatGainResult {
        val focusGain = FOCUS_XP_PER_QUEST
            .coerceAtMost(MAX_STAT - currentStats.focus)
            .coerceAtLeast(0)

        val updatedStats = currentStats.copy(
            focus = currentStats.focus + focusGain
        )

        return buildResult(currentStats, updatedStats)
    }




    /** AI
     * Core stat gain formula — applies to both strength and cardio.
     * statGain per field = (finalXp × statWeight).toInt(), capped at MAX_STAT.
     *
     * Fix: Focus is intentionally hardcoded to 0 here. Focus only grows from
     * streak progression and quest completions — never from exercise weights.
     * Even if contribution.focus > 0 for some exercise, it is ignored here
     * to prevent double-dipping with addFocusFromStreak/addFocusFromQuestCompletion.
     */
    private fun applyStatGains(
        currentStats: Stats,
        contribution: StatContribution,
        finalXp: Int
    ): StatGainResult {
        val strengthGain  = gainFor(currentStats.strength,    contribution.strength,    finalXp)
        val dexterityGain = gainFor(currentStats.dexterity,   contribution.dexterity,   finalXp)
        val enduranceGain = gainFor(currentStats.endurance,   contribution.endurance,   finalXp)
        val flexGain      = gainFor(currentStats.flexibility, contribution.flexibility, finalXp)
        val powerGain     = gainFor(currentStats.power,       contribution.power,       finalXp)
        // Fix: Focus intentionally excluded — grows only from streak/quest, not exercises.
        val focusGain     = 0

        val updatedStats = Stats(
            strength    = currentStats.strength    + strengthGain,
            dexterity   = currentStats.dexterity   + dexterityGain,
            endurance   = currentStats.endurance   + enduranceGain,
            flexibility = currentStats.flexibility + flexGain,
            power       = currentStats.power       + powerGain,
            focus       = currentStats.focus       // unchanged — Focus never grows here
        )

        return StatGainResult(
            statsBefore       = currentStats,
            statsAfter        = updatedStats,
            strengthGained    = strengthGain,
            dexterityGained   = dexterityGain,
            enduranceGained   = enduranceGain,
            flexibilityGained = flexGain,
            powerGained       = powerGain,
            focusGained       = focusGain
        )
    }


    // (finalXp x weight).toInt() capped so the stat never exceeds MAX_STAT
    private fun gainFor(currentValue: Int, weight: Double, finalXp: Int): Int {
        if (weight <= 0.0) return 0
        val raw = (finalXp * weight).toInt()
        return raw.coerceAtMost(MAX_STAT - currentValue).coerceAtLeast(0)
    }

    // Diffs before and after stats
    private fun buildResult(before: Stats, after: Stats): StatGainResult {
        return StatGainResult(
            statsBefore       = before,
            statsAfter        = after,
            strengthGained    = after.strength    - before.strength,
            dexterityGained   = after.dexterity   - before.dexterity,
            enduranceGained   = after.endurance   - before.endurance,
            flexibilityGained = after.flexibility - before.flexibility,
            powerGained       = after.power       - before.power,
            focusGained       = after.focus       - before.focus
        )
    }
}

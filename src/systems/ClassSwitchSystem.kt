package systems

import models.ClassProgress
import models.FitnessClass
import models.Stats
import models.User

// ============================================================
// ClassSwitchSystem.kt
// Layer: Systems
// Communicates with: Models only (User, FitnessClass,
//                    ClassProgress, Stats)
// Never communicates with: UI, ViewModel, Repository, Database
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================
//
// HOW CLASS SWITCHING WORKS:
//
// Players can switch class once every 2 months (SWITCH_COOLDOWN_MS).
// Every new account gets 1 free switch that ignores the cooldown.
// The free switch does not stack — once used it is gone permanently
// unless a CLASS_RECHOICE item is granted by the item system.
//
// HEAD START FORMULA:
// When switching to a class never played before, the player receives
// a starting level calculated from their existing stats:
//
//   Regular classes:
//     startPoints = (primaryStat x 0.8) + (secondary1 x 0.4) + (secondary2 x 0.4)
//
//   Paragon (no primary stat - rewards balance):
//     startPoints = (strength + dexterity + endurance + flexibility + power + focus) x 0.27
//     Weight 0.27 derived from 1.6 / 6 = 0.267 = 0.27 to match total weight of regular formula.
//
//   startingLevel = (startPoints / HEAD_START_DIVISOR).toInt()
//                   capped at HEAD_START_MAX_LEVEL
//
// When switching BACK to a class already played, the player returns
// to exactly the level they left at - no head start recalculation,
// no penalty, no bonus. Their progress is preserved in classMap.
//
// BYPASS:
// A CLASS_RECHOICE item (future item system) can bypass the cooldown.
// The caller is responsible for consuming the item - this system only
// needs to know whether a bypass is active via the bypassCooldown flag.
//
// Fix: the free switch is checked BEFORE the bypass item check. This ensures
// that a new player who uses a CLASS_RECHOICE item still consumes their free
// switch first, rather than wasting both a free switch and an item.
//
// ============================================================

object ClassSwitchSystem {

    private const val SWITCH_COOLDOWN_MS = 60L * 24L * 60L * 60L * 1000L  // TUNABLE - 60 days
    private const val PRIMARY_WEIGHT   = 0.8    // TUNABLE
    private const val SECONDARY_WEIGHT = 0.4    // TUNABLE
    private const val PARAGON_WEIGHT   = 0.27   // TUNABLE - derived: 1.6 / 6 = 0.267 = 0.27
    private const val HEAD_START_DIVISOR = 70.0 // TUNABLE - 488 pts / 70 = Level 7 (doc example)
    private const val HEAD_START_MAX_LEVEL = 30 // TUNABLE

    sealed class CanSwitchResult {
        data class Allowed(val reason: SwitchAllowedReason) : CanSwitchResult()
        data class Blocked(
            val lastSwitchMs: Long,
            val cooldownEndsMs: Long,
            val msRemaining: Long
        ) : CanSwitchResult()
        object AlreadyOnThisClass : CanSwitchResult()
    }

    enum class SwitchAllowedReason {
        FREE_SWITCH,
        COOLDOWN_PASSED,
        BYPASS_ITEM
    }

    data class HeadStartResult(
        val targetClass: FitnessClass,
        val isReturning: Boolean,
        val startingLevel: Int,
        val headStartPoints: Double,
        val wasCapApplied: Boolean
    )

    data class SwitchResult(
        val updatedUser: User,
        val previousClass: FitnessClass,
        val newClass: FitnessClass,
        val headStart: HeadStartResult
    )

    /**
     * Checks whether a player is allowed to switch class right now.
     * Rules (in order):
     * 1. If target is current class - AlreadyOnThisClass
     * 2. If free switch not yet used - Allowed(FREE_SWITCH)   [checked BEFORE bypass item]
     * 3. If cooldown has passed - Allowed(COOLDOWN_PASSED)    [checked BEFORE bypass item]
     * 4. If bypassCooldown = true - Allowed(BYPASS_ITEM)      [only if natural switch not available]
     * 5. Otherwise - Blocked with time remaining
     *
     * Fix: free switch was already checked before bypass (v2.1 audit fix #CS1) so that a new
     * player doesn't waste a CLASS_RECHOICE item when they have a free switch available.
     * Extended that fix: natural cooldown expiry is ALSO checked before bypass, for the
     * same reason — a player whose cooldown has already passed should not consume an item
     * they don't need. Item bypass is now the last resort before Blocked, as intended.
     *
     * Order matters: ALL free/natural switch paths must be exhausted before we consume an item.
     */
    fun canSwitch(
        user: User,
        targetClass: FitnessClass,
        nowMs: Long,
        bypassCooldown: Boolean = false
    ): CanSwitchResult {
        if (user.currentClass == targetClass) return CanSwitchResult.AlreadyOnThisClass

        // Free switch — consume before burning an item
        if (!user.hasUsedFreeSwitch) {
            return CanSwitchResult.Allowed(SwitchAllowedReason.FREE_SWITCH)
        }

        // Fix: check natural cooldown expiry BEFORE checking bypass item.
        // A player whose 2-month cooldown has already passed should not consume
        // a CLASS_RECHOICE item — they can switch for free.
        val cooldownEndsMs = user.lastClassSwitch + SWITCH_COOLDOWN_MS
        if (nowMs >= cooldownEndsMs) {
            return CanSwitchResult.Allowed(SwitchAllowedReason.COOLDOWN_PASSED)
        }

        // Item bypass - free switch used AND cooldown not yet passed, so item is genuinely needed
        if (bypassCooldown) {
            return CanSwitchResult.Allowed(SwitchAllowedReason.BYPASS_ITEM)
        }

        return CanSwitchResult.Blocked(
            lastSwitchMs = user.lastClassSwitch,
            cooldownEndsMs = cooldownEndsMs,
            msRemaining = cooldownEndsMs - nowMs
        )
    }

    /**
     * Calculates the head start level for switching to a target class.
     * If the player has played this class before, they return to their existing
     * progress with no recalculation.
     *
     * Fix: "has played this class before" was previously checked as level > 0, but a
     * player could earn XP at level 0 (before reaching level 1) and still have level == 0.
     * In that case the old code classified them as new and gave them a head start that
     * overwrote their 80 XP of real progress. Now uses totalXp > 0 — totalXp is the
     * cumulative never-resetting counter, so any prior activity in this class shows up.
     */
    fun calculateHeadStart(user: User, targetClass: FitnessClass): HeadStartResult {
        val existingProgress = user.classMap[targetClass]
        val isReturning = (existingProgress?.totalXp ?: 0) > 0

        if (isReturning) {
            return HeadStartResult(
                targetClass = targetClass,
                isReturning = true,
                startingLevel = existingProgress!!.level,
                headStartPoints = 0.0,
                wasCapApplied = false
            )
        }

        val points = calculateHeadStartPoints(user.stats, targetClass)
        val rawLevel = (points / HEAD_START_DIVISOR).toInt()
        val cappedLevel = rawLevel.coerceAtMost(HEAD_START_MAX_LEVEL)

        return HeadStartResult(
            targetClass = targetClass,
            isReturning = false,
            startingLevel = cappedLevel,
            headStartPoints = points,
            wasCapApplied = rawLevel > HEAD_START_MAX_LEVEL
        )
    }

    /**
     * Applies the class switch. Call only after canSwitch() returns Allowed.
     * Returning players restore their saved level. New class players get head start.
     * totalXp stays 0 for head start - it is a level grant, not earned XP.
     */
    fun applySwitch(
        user: User,
        targetClass: FitnessClass,
        allowedReason: SwitchAllowedReason,
        nowMs: Long
    ): SwitchResult {
        val headStart = calculateHeadStart(user, targetClass)
        val previousClass = user.currentClass

        val updatedClassMap = user.classMap.toMutableMap()
        if (!headStart.isReturning && headStart.startingLevel > 0) {
            val current = updatedClassMap[targetClass] ?: ClassProgress(targetClass)
            updatedClassMap[targetClass] = current.copy(
                level = headStart.startingLevel,
                xp = 0,
                totalXp = 0
            )
        }

        val updatedClassesSwitchedTo = user.classesSwitchedTo.toMutableSet()
        updatedClassesSwitchedTo.add(targetClass)

        val updatedUser = user.copy(
            currentClass = targetClass,
            lastClassSwitch = nowMs,
            hasUsedFreeSwitch = if (allowedReason == SwitchAllowedReason.FREE_SWITCH) true
                                 else user.hasUsedFreeSwitch,
            classesSwitchedTo = updatedClassesSwitchedTo,
            classMap = updatedClassMap
        )

        return SwitchResult(
            updatedUser = updatedUser,
            previousClass = previousClass,
            newClass = targetClass,
            headStart = headStart
        )
    }

    /**
     * Returns ms remaining until the cooldown expires.
     * Returns 0 if cooldown has passed or free switch is still available.
     */
    fun cooldownRemainingMs(user: User, nowMs: Long): Long {
        if (!user.hasUsedFreeSwitch) return 0L
        val cooldownEndsMs = user.lastClassSwitch + SWITCH_COOLDOWN_MS
        return (cooldownEndsMs - nowMs).coerceAtLeast(0L)
    }

    /**
     * Calculates raw head start points from stats.
     * Regular: (primary x 0.8) + (secondary1 x 0.4) + (secondary2 x 0.4). Max = 1600.
     * Paragon: (sum of all 6 stats) x 0.27. Max = 1620.
     */
    private fun calculateHeadStartPoints(stats: Stats, targetClass: FitnessClass): Double {
        return when (targetClass) {
            FitnessClass.CHAMPION ->
                (stats.strength  * PRIMARY_WEIGHT) +
                (stats.endurance * SECONDARY_WEIGHT) +
                (stats.power     * SECONDARY_WEIGHT)

            FitnessClass.STRIKER ->
                (stats.power     * PRIMARY_WEIGHT) +
                (stats.dexterity * SECONDARY_WEIGHT) +
                (stats.strength  * SECONDARY_WEIGHT)

            FitnessClass.ROGUE ->
                (stats.dexterity * PRIMARY_WEIGHT) +
                (stats.power     * SECONDARY_WEIGHT) +
                (stats.endurance * SECONDARY_WEIGHT)

            FitnessClass.MONK ->
                (stats.flexibility * PRIMARY_WEIGHT) +
                (stats.endurance   * SECONDARY_WEIGHT) +
                (stats.focus       * SECONDARY_WEIGHT)

            FitnessClass.ADVENTURER ->
                (stats.endurance   * PRIMARY_WEIGHT) +
                (stats.dexterity   * SECONDARY_WEIGHT) +
                (stats.flexibility * SECONDARY_WEIGHT)

            FitnessClass.PARAGON ->
                (stats.strength + stats.dexterity + stats.endurance +
                 stats.flexibility + stats.power + stats.focus) * PARAGON_WEIGHT
        }
    }
}

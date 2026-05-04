package com.rise.fitrpg.systems

import com.rise.fitrpg.data.models.ClassProgress
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.Stats
import com.rise.fitrpg.data.models.User

// How class switching wsorks:
// Players can switch class once every 2 months
// Every new account gets 1 free switch that ignores the cooldown - does not stack
// A CLASS_RECHOICE item (future item system) can also bypass the cooldown
//
// Head start formula - when switching to a class never played before:
//   Regular classes:
//     startPoints = (primaryStat x 0.8) + (secondary1 x 0.4) + (secondary2 x 0.4)
//   Paragon (no primary stat - rewards balance across all stats):
//     startPoints = (all 6 stats summed) x 0.27
//     0.27 is derived from 1.6 / 6 = 0.267, rounded up to match the weight total of regular classes
//
//   startingLevel = (startPoints / HEAD_START_DIVISOR).toInt(), capped at HEAD_START_MAX_LEVEL
//
// Switching back to a class already played restores exactly the level left at
// No recalculation, no penalty, no bonus - progress is preserved in classMap
//
// Check order in canSwitch matters:
// Free switch is checked before bypass item - a new player with both available
// should burn their free switch first, not waste an item they didn't need

object ClassSwitchSystem {

    private const val SWITCH_COOLDOWN_MS = 60L * 24L * 60L * 60L * 1000L  //  - 60 days
    private const val PRIMARY_WEIGHT   = 0.8
    private const val SECONDARY_WEIGHT = 0.4
    private const val PARAGON_WEIGHT   = 0.27   // derived: 1.6 / 6 = 0.267 = 0.27
    private const val HEAD_START_DIVISOR = 70.0 // 488 pts / 70 = Level 7 (doc example)
    private const val HEAD_START_MAX_LEVEL = 30

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

    // Checks whether the player can switch class right now
    // Order matters - free switch and natural cooldown expiry are both checked BEFORE the bypass item
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


    // Calculates the head start level for switching to a target class
    // Returns early if the player has been in this class before - they just get their old level back
    //
    // "Has played before" is checked via totalXp > 0, not level > 0
    // A player could earn XP at level 0 and still have level == 0
    // Using level > 0 in that case would give them a head start that overwrites real progress
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

    // Applies the switch only call this after canSwitch() returns Allowed
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

    // How long until the player can switch again - returns 0 if free switch is still available
    fun cooldownRemainingMs(user: User, nowMs: Long): Long {
        if (!user.hasUsedFreeSwitch) return 0L
        val cooldownEndsMs = user.lastClassSwitch + SWITCH_COOLDOWN_MS
        return (cooldownEndsMs - nowMs).coerceAtLeast(0L)
    }

    // Regular: (primary x 0.8) + (secondary1 x 0.4) + (secondary2 x 0.4) - max 1600
    // Paragon: (sum of all 6 stats) x 0.27 - max 1620
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

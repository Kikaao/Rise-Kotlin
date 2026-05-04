package com.rise.fitrpg.systems

import com.rise.fitrpg.data.models.CardioExercise
import com.rise.fitrpg.data.models.CardioSession
import com.rise.fitrpg.data.models.CardioExerciseType
import com.rise.fitrpg.data.models.Exercise
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.GameConstants
import com.rise.fitrpg.data.models.StatContribution
import com.rise.fitrpg.data.models.TrackingType
import com.rise.fitrpg.data.models.WorkoutSet


// Communicates with: Models
// Everything that touches XP lives here strength sets, cardio sessions, streak bonuses
// Pain


object XpSystem {

    // Class multiplier: how much bonus XP a class gets when the
    // exercise strongly matches its primary stat
    private const val CLASS_MULTIPLIER_PRIMARY = 1.3    // exercise is a strong match for this class
    private const val CLASS_MULTIPLIER_SECONDARY = 1.1  // exercise is a partial match
    private const val CLASS_MULTIPLIER_BASE = 1.0       // exercise has no match for this class

    // How much of an exercise's StatContribution weight counts as primary vs secondary
    private const val PRIMARY_STAT_THRESHOLD = 0.5
    private const val SECONDARY_STAT_THRESHOLD = 0.2

    // Paragon gets a flat multiplier regardless of exercise type
    // because it rewards all training equally — no primary stat
    private const val PARAGON_CLASS_MULTIPLIER = 1.15

    // Difficulty multiplier scale factors divide volume/reps/seconds
    // by these to produce a 0.0–1.0 raw difficulty score before adding 1.0
    // Example: 100kg × 10 reps = 1000 volume / 500 = 2.0 raw → capped at max
    private const val DIFFICULTY_SCALE_REPS_WEIGHT = 500.0  // tunable — kg × reps divisor
    private const val DIFFICULTY_SCALE_REPS_ONLY = 20.0     // tunable — reps divisor
    private const val DIFFICULTY_SCALE_TIME = 120.0         // tunable — seconds divisor

    // Hard capped so nobody gets absurd XP from extreme inputs
    private const val DIFFICULTY_MAX_MULTIPLIER = 2.0   // tunable — max 2× from difficulty alone

    // Streak multipliers live in GameConstants
    // so StreakSystem can also read them without calling XpSystem directly.
    private val STREAK_DAYS_TIER_1 get() = GameConstants.STREAK_DAYS_TIER_1
    private val STREAK_DAYS_TIER_2 get() = GameConstants.STREAK_DAYS_TIER_2
    private val STREAK_DAYS_TIER_3 get() = GameConstants.STREAK_DAYS_TIER_3
    private val STREAK_MULTIPLIER_TIER_1 get() = GameConstants.STREAK_MULTIPLIER_TIER_1
    private val STREAK_MULTIPLIER_TIER_2 get() = GameConstants.STREAK_MULTIPLIER_TIER_2
    private val STREAK_MULTIPLIER_TIER_3 get() = GameConstants.STREAK_MULTIPLIER_TIER_3




    data class XpResult(
        val baseXp: Int,
        val classMultiplier: Double,
        val difficultyMultiplier: Double,
        val streakMultiplier: Double,
        val finalXp: Int
    )

    data class CardioXpResult(
        val baseXp: Int,
        val paceTierMultiplier: Double,    // 1.0 for interval exercises (no pace tier)
        val streakMultiplier: Double,
        val finalXp: Int
    )


    // XP for a single strength or flexibility set — called once per WorkoutSet when a session is saved
    // When provided and the exercise is bodyweight, it gets factored into the difficulty calculation
    // A 100kg person doing pull-ups is more than a 60kg person doing the same reps

    fun calculateStrengthXP(
        exercise: Exercise,
        set: WorkoutSet,
        currentClass: FitnessClass,
        currentStreakDays: Int,
        playerBodyweightKg: Double? = null
    ): XpResult {
        val baseXp = exercise.baseXp
        val classMultiplier = getClassMultiplier(exercise.statContribution, currentClass)
        val difficultyMultiplier = getDifficultyMultiplier(
            exercise.trackingType,
            set,
            if (exercise.usesBodyweight) playerBodyweightKg else null
        )
        val streakMultiplier = applyStreakMultiplier(currentStreakDays)

        val finalXp = (baseXp * classMultiplier * difficultyMultiplier * streakMultiplier).toInt()

        return XpResult(
            baseXp = baseXp,
            classMultiplier = classMultiplier,
            difficultyMultiplier = difficultyMultiplier,
            streakMultiplier = streakMultiplier,
            finalXp = finalXp
        )
    }

    // XP for a complete cardio session logged per session, not per set
    // Interval exercises store sets inside the CardioSession itself
    fun calculateCardioXP(
        cardioExercise: CardioExercise,
        session: CardioSession,
        currentStreakDays: Int
    ): CardioXpResult {
        val streakMultiplier = applyStreakMultiplier(currentStreakDays)

        return when (cardioExercise.type) {
            CardioExerciseType.DISTANCE_BASED -> calculateDistanceXP(cardioExercise, session, streakMultiplier)
            CardioExerciseType.INTERVAL_BASED -> calculateIntervalXP(cardioExercise, session, streakMultiplier)
        }
    }

    // StreakSystem calls this too when displaying streak status
    // Streak counts in days  exceeding your weekly minimum still counts every extra day
    // e.g. KNIGHT minimum is 3 days/week logging 6 days counts as 6 streak days, not 3
    fun applyStreakMultiplier(currentStreakDays: Int): Double {
        return when {
            currentStreakDays >= STREAK_DAYS_TIER_3 -> STREAK_MULTIPLIER_TIER_3
            currentStreakDays >= STREAK_DAYS_TIER_2 -> STREAK_MULTIPLIER_TIER_2
            currentStreakDays >= STREAK_DAYS_TIER_1 -> STREAK_MULTIPLIER_TIER_1
            else -> 1.0 // No bonus below 5 days — base rate
        }
    }

    // Checks how well the exercise matches the player's current class primary stat
    // Paragon is handled separately
    private fun getClassMultiplier(
        statContribution: StatContribution,
        currentClass: FitnessClass
    ): Double {
        if (currentClass == FitnessClass.PARAGON) {
            // Paragon has no primary stat — rewards all training equally
            return PARAGON_CLASS_MULTIPLIER
        }

        val primaryWeight = getPrimaryStatWeight(statContribution, currentClass)

        return when {
            primaryWeight >= PRIMARY_STAT_THRESHOLD -> CLASS_MULTIPLIER_PRIMARY
            primaryWeight >= SECONDARY_STAT_THRESHOLD -> CLASS_MULTIPLIER_SECONDARY
            else -> CLASS_MULTIPLIER_BASE
        }
    }

    // Returns the StatContribution weight for the given class's primary stat

    private fun getPrimaryStatWeight(
        statContribution: StatContribution,
        currentClass: FitnessClass
    ): Double {
        return when (currentClass) {
            FitnessClass.CHAMPION   -> statContribution.strength
            FitnessClass.STRIKER    -> statContribution.power
            FitnessClass.ROGUE      -> statContribution.dexterity
            FitnessClass.MONK       -> statContribution.flexibility
            FitnessClass.ADVENTURER -> statContribution.endurance
            FitnessClass.PARAGON    -> 0.0 // Never reached — Paragon handled above
        }
    }


    // calculates difficulty from what was actually logged // AI SITS getDifficultyMultiplier should not forget to check it
    // Formula: 1.0 + (raw score) — minimum is always 1.0, never penalizes a light set
    // Raw score is capped before adding 1.0 so the final result never exceeds DIFFICULTY_MAX_MULTIPLIER
    //
    // For REPS_ONLY exercises with usesBodyweight = true, playerBodyweightKg is folded in
    // as the effective load — same formula as REPS_WEIGHT but weight comes from profile, not the set
    private fun getDifficultyMultiplier(trackingType: TrackingType, set: WorkoutSet, playerBodyweightKg: Double? = null): Double {
        val rawScore = when (trackingType) {
            TrackingType.REPS_WEIGHT -> {
                val volume = (set.weightKg ?: 0.0) * (set.reps ?: 0)
                volume / DIFFICULTY_SCALE_REPS_WEIGHT
            }
            TrackingType.REPS_ONLY -> {
                // if bodyweight is provided, treat it like REPS_WEIGHT so a heavier person gets more XP
                if (playerBodyweightKg != null) {
                    val volume = playerBodyweightKg * (set.reps ?: 0)
                    volume / DIFFICULTY_SCALE_REPS_WEIGHT
                } else {
                    (set.reps ?: 0) / DIFFICULTY_SCALE_REPS_ONLY
                }
            }
            TrackingType.TIME -> {
                (set.durationSeconds ?: 0) / DIFFICULTY_SCALE_TIME
            }
        }

        // Add 1.0 so the base is never below 1.0, then cap at max
        return (1.0 + rawScore).coerceAtMost(DIFFICULTY_MAX_MULTIPLIER)
    }



    // Distance-based cardio: distanceKm × baseXpPerKm × paceTierMultiplier × streakMultiplier
    // Pace bonus when both distance and time are logged and distance clears the minimum
    // No time logged = no bonus, never a penalty — logging time is optional by design

    private fun calculateDistanceXP(
        cardioExercise: CardioExercise,
        session: CardioSession,
        streakMultiplier: Double
    ): CardioXpResult {
        val distance = session.distanceKm ?: 0.0
        val baseXp = (distance * cardioExercise.baseXpPerKm).toInt()

        val paceTierMultiplier = if (
            session.distanceKm != null &&
            session.durationMinutes != null &&
            distance >= cardioExercise.minDistanceForBonusKm
        ) {
            // Both distance and time logged, minimum distance met — calculate pace bonus
            getPaceTierMultiplier(session.paceMinPerKm, cardioExercise)
        } else {
            // Time not logged or distance too short — base rate, no penalty
            1.0
        }

        val finalXp = (baseXp * paceTierMultiplier * streakMultiplier).toInt()

        return CardioXpResult(
            baseXp = baseXp,
            paceTierMultiplier = paceTierMultiplier,
            streakMultiplier = streakMultiplier,
            finalXp = finalXp
        )
    }


    // Interval-based cardio: (totalMeters / 10) × baseXpPer10Meters × streakMultiplier
    // No pace tier — XP is purely volume based
    // paceTierMultiplier is hardcoded to 1.0 in the result so the UI
    // can treat both types the samerMultiplier is always 1.0 in the result for UI consistency.

    private fun calculateIntervalXP(
        cardioExercise: CardioExercise,
        session: CardioSession,
        streakMultiplier: Double
    ): CardioXpResult {
        val totalMeters = session.totalMeters?.toDouble() ?: 0.0
        val baseXp = ((totalMeters / 10.0) * cardioExercise.baseXpPer10Meters).toInt()
        val finalXp = (baseXp * streakMultiplier).toInt()

        return CardioXpResult(
            baseXp = baseXp,
            paceTierMultiplier = 1.0, // Interval exercises have no pace tier
            streakMultiplier = streakMultiplier,
            finalXp = finalXp
        )
    }

    // Finds which pace tier the user's pace falls into and returns its multiplier
    private fun getPaceTierMultiplier(
        paceMinPerKm: Double?,
        cardioExercise: CardioExercise
    ): Double {
        if (paceMinPerKm == null) return 1.0

        // Lower min/km = faster pace, so faster = smaller number
        if (paceMinPerKm < cardioExercise.maxRealisticPaceMinPerKm) return 1.0


        val matchedTier = cardioExercise.paceThresholds
            .firstOrNull { paceMinPerKm <= it.maxPaceMinPerKm }
            ?.tier

        return matchedTier?.multiplier ?: 1.0
    }
}
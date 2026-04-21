package systems

import models.CardioExercise
import models.CardioSession
import models.CardioExerciseType
import models.Exercise
import models.FitnessClass
import models.GameConstants
import models.PaceTier
import models.StatContribution
import models.TrackingType
import models.WorkoutSet

// ============================================================
// XpSystem.kt
// Layer: Systems
// Communicates with: Models only (Exercise, CardioExercise,
//                    WorkoutSet, CardioSession, FitnessClass)
// Never communicates with: UI, ViewModel, Repository, Database
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================

object XpSystem {

    // --------------------------------------------------------
    // TUNABLE CONSTANTS — change these to adjust game feel
    // without touching any logic or structure
    // --------------------------------------------------------

    // Class multiplier: how much bonus XP a class gets when the
    // exercise strongly matches its primary stat
    private const val CLASS_MULTIPLIER_PRIMARY = 1.3    // TUNABLE — exercise is a strong match for this class
    private const val CLASS_MULTIPLIER_SECONDARY = 1.1  // TUNABLE — exercise is a partial match
    private const val CLASS_MULTIPLIER_BASE = 1.0       // TUNABLE — exercise has no match for this class

    // Thresholds on StatContribution weight that decide which tier a class falls into
    // e.g. if Champion's strength weight >= 0.5 → primary multiplier
    private const val PRIMARY_STAT_THRESHOLD = 0.5      // TUNABLE
    private const val SECONDARY_STAT_THRESHOLD = 0.2    // TUNABLE

    // Paragon gets a flat multiplier regardless of exercise type
    // because it rewards all training equally — no primary stat
    private const val PARAGON_CLASS_MULTIPLIER = 1.15   // TUNABLE

    // Difficulty multiplier scale factors — divide raw volume/reps/seconds
    // by these to produce a 0.0–1.0 raw difficulty score before adding 1.0
    // Example: 100kg × 10 reps = 1000 volume / 500 = 2.0 raw → capped at max
    private const val DIFFICULTY_SCALE_REPS_WEIGHT = 500.0  // TUNABLE — kg × reps divisor
    private const val DIFFICULTY_SCALE_REPS_ONLY = 20.0     // TUNABLE — reps divisor
    private const val DIFFICULTY_SCALE_TIME = 120.0         // TUNABLE — seconds divisor

    // Hard cap on difficulty multiplier — prevents absurd XP from extreme inputs
    private const val DIFFICULTY_MAX_MULTIPLIER = 2.0   // TUNABLE — max 2× from difficulty alone

    // Streak multiplier thresholds — defined in GameConstants (models layer)
    // so StreakSystem can also read them without calling XpSystem directly.
    private val STREAK_DAYS_TIER_1 get() = GameConstants.STREAK_DAYS_TIER_1
    private val STREAK_DAYS_TIER_2 get() = GameConstants.STREAK_DAYS_TIER_2
    private val STREAK_DAYS_TIER_3 get() = GameConstants.STREAK_DAYS_TIER_3
    private val STREAK_MULTIPLIER_TIER_1 get() = GameConstants.STREAK_MULTIPLIER_TIER_1
    private val STREAK_MULTIPLIER_TIER_2 get() = GameConstants.STREAK_MULTIPLIER_TIER_2
    private val STREAK_MULTIPLIER_TIER_3 get() = GameConstants.STREAK_MULTIPLIER_TIER_3


    // --------------------------------------------------------
    // RESULT DATA CLASSES
    // These are the "receipts" returned by every calculation.
    // The UI uses finalXp to store XP.
    // The UI also uses the breakdown to show the player exactly
    // how they earned their XP (e.g. "+47 XP ×1.3 class bonus").
    // --------------------------------------------------------

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


    // --------------------------------------------------------
    // PUBLIC FUNCTIONS
    // --------------------------------------------------------

    /**
     * Calculates XP for a single strength or flexibility set.
     * Called once per WorkoutSet when a session is finished.
     *
     * @param exercise      The exercise being logged
     * @param set           The specific set (reps, weight, or duration)
     * @param currentClass  The player's active FitnessClass at time of logging
     * @param currentStreakDays  Total consecutive days on streak
     */
    fun calculateStrengthXP(
        exercise: Exercise,
        set: WorkoutSet,
        currentClass: FitnessClass,
        currentStreakDays: Int
    ): XpResult {
        val baseXp = exercise.baseXp
        val classMultiplier = getClassMultiplier(exercise.statContribution, currentClass)
        val difficultyMultiplier = getDifficultyMultiplier(exercise.trackingType, set)
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

    /**
     * Calculates XP for a complete cardio session.
     * Cardio is logged per session, not per set (except interval exercises
     * which store sets inside the CardioSession itself).
     *
     * @param cardioExercise    The cardio exercise definition from CardioExerciseLibrary
     * @param session           The logged cardio session (distance, time, sets, etc.)
     * @param currentStreakDays Total consecutive days on streak
     */
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

    /**
     * Returns the streak XP multiplier for a given streak length in days.
     * Public because StreakSystem.kt also calls this directly when
     * displaying streak status to the user.
     *
     * Streak counts in days. Players set a weekly minimum (StreakTier).
     * If they exceed their minimum, every extra day still counts.
     * e.g. KNIGHT minimum is 3 days/week — logging 6 days counts as 6.
     */
    fun applyStreakMultiplier(currentStreakDays: Int): Double {
        return when {
            currentStreakDays >= STREAK_DAYS_TIER_3 -> STREAK_MULTIPLIER_TIER_3
            currentStreakDays >= STREAK_DAYS_TIER_2 -> STREAK_MULTIPLIER_TIER_2
            currentStreakDays >= STREAK_DAYS_TIER_1 -> STREAK_MULTIPLIER_TIER_1
            else -> 1.0 // No bonus below 5 days — base rate
        }
    }


    // --------------------------------------------------------
    // PRIVATE HELPERS — CLASS MULTIPLIER
    // --------------------------------------------------------

    /**
     * Determines the class multiplier by checking how much the exercise's
     * StatContribution matches the player's current class primary stat.
     * Paragon is handled separately — it always gets a flat bonus.
     */
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

    /**
     * Returns the StatContribution weight for the primary stat of a given class.
     * This is the number between 0.0 and 1.0 that represents how much
     * this exercise trains that class's primary stat.
     */
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


    // --------------------------------------------------------
    // PRIVATE HELPERS — DIFFICULTY MULTIPLIER
    // --------------------------------------------------------

    /**
     * Calculates difficulty multiplier from the set's logged data.
     * Formula: 1.0 + (raw score) — minimum is always 1.0 (never penalizes),
     * maximum is capped at DIFFICULTY_MAX_MULTIPLIER.
     *
     * REPS_WEIGHT: volume = weightKg × reps (heavier + more reps = harder)
     * REPS_ONLY:   based on rep count alone (bodyweight volume)
     * TIME:        based on duration in seconds (longer hold = harder)
     */
    private fun getDifficultyMultiplier(trackingType: TrackingType, set: WorkoutSet): Double {
        val rawScore = when (trackingType) {
            TrackingType.REPS_WEIGHT -> {
                val volume = (set.weightKg ?: 0.0) * (set.reps ?: 0)
                volume / DIFFICULTY_SCALE_REPS_WEIGHT
            }
            TrackingType.REPS_ONLY -> {
                (set.reps ?: 0) / DIFFICULTY_SCALE_REPS_ONLY
            }
            TrackingType.TIME -> {
                (set.durationSeconds ?: 0) / DIFFICULTY_SCALE_TIME
            }
        }

        // Add 1.0 so the base is never below 1.0, then cap at max
        return (1.0 + rawScore).coerceAtMost(DIFFICULTY_MAX_MULTIPLIER)
    }


    // --------------------------------------------------------
    // PRIVATE HELPERS — CARDIO XP
    // --------------------------------------------------------

    /**
     * XP for distance-based cardio (running, cycling, rowing, etc.).
     * Formula: distanceKm × baseXpPerKm × paceTierMultiplier × streakMultiplier
     *
     * Pace bonus only applies if:
     * (1) the user logged both distance AND time
     * (2) distance meets the minimum threshold for that exercise
     * Never penalizes for not logging time — only rewards precision.
     */
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

    /**
     * XP for interval-based cardio (Jump Rope, Sprinting).
     * Formula: (totalMeters / 10) × baseXpPer10Meters × streakMultiplier
     *
     * No pace tier for interval exercises — XP is purely volume based.
     * paceTierMultiplier is always 1.0 in the result for UI consistency.
     */
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

    /**
     * Finds the PaceTier for a given pace and applies its multiplier.
     *
     * Anti-gaming rule: if pace is faster than maxRealisticPaceMinPerKm
     * (lower min/km = faster), the input is treated as invalid and 1.0 is returned.
     *
     * PaceThreshold lookup: iterates the exercise's thresholds to find
     * which tier the user's pace falls into.
     */
    private fun getPaceTierMultiplier(
        paceMinPerKm: Double?,
        cardioExercise: CardioExercise
    ): Double {
        if (paceMinPerKm == null) return 1.0

        // Anti-gaming: pace faster than realistic cap → ignore
        // Lower min/km = faster pace, so faster = smaller number
        if (paceMinPerKm < cardioExercise.maxRealisticPaceMinPerKm) return 1.0

        // Thresholds are sorted fastest to slowest (ascending maxPaceMinPerKm)
        // Pick the first threshold where the user's pace is at or below the ceiling
        val matchedTier = cardioExercise.paceThresholds
            .firstOrNull { paceMinPerKm <= it.maxPaceMinPerKm }
            ?.tier

        // PaceTier enum already carries its multiplier — use it directly
        return matchedTier?.multiplier ?: 1.0
    }
}

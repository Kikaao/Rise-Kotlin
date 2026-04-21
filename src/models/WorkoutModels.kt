package models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()

// ─────────────────────────────────────────────────────
// WORKOUT SET
// A single set logged during a strength workout session.
// Only the fields relevant to the exercise's TrackingType are filled in:
//   REPS_WEIGHT  → reps + weightKg
//   REPS_ONLY    → reps only
//   TIME         → durationSeconds only
// ─────────────────────────────────────────────────────

data class WorkoutSet(
    val id: Int,
    val exerciseId: Int,
    val setNumber: Int,
    val reps: Int? = null,                // REPS_WEIGHT and REPS_ONLY
    val weightKg: Double? = null,         // REPS_WEIGHT only
    val durationSeconds: Int? = null,     // TIME only
    val xpEarned: Int = 0
)

// ─────────────────────────────────────────────────────
// WORKOUT SESSION
// A complete strength training session logged by the user.
// Contains one or more WorkoutSets across one or more exercises.
// fitnessClass records which class was active at the time — needed to apply
// the correct class XP multiplier even if the user switches class later.
// ─────────────────────────────────────────────────────

data class WorkoutSession(
    val id: Int,
    val userId: Int,
    val date: Long,                         // Unix timestamp in ms
    val fitnessClass: FitnessClass,         // active class during this session
    val sets: List<WorkoutSet>,
    val totalXpEarned: Int = 0,
    val notes: String? = null
) {
    val totalSets: Int get() = sets.size
    val uniqueExercises: Int get() = sets.map { it.exerciseId }.distinct().size
}

// ─────────────────────────────────────────────────────
// PERSONAL RECORD
// The best performance a user has ever logged for a specific exercise.
// One record per user per exercise — updated whenever a new best is achieved.
//
//   REPS_WEIGHT  → bestWeightKg (heaviest single set regardless of reps)
//                  bestReps (most reps ever at any weight)
//   REPS_ONLY    → bestReps
//   TIME         → bestDurationSeconds
// ─────────────────────────────────────────────────────

data class PersonalRecord(
    val id: Int,
    val userId: Int,
    val exerciseId: Int,
    val date: Long,                             // date the PR was set, Unix timestamp in ms
    val bestWeightKg: Double? = null,           // REPS_WEIGHT — heaviest weight lifted
    val bestReps: Int? = null,                  // REPS_WEIGHT / REPS_ONLY — most reps
    val bestDurationSeconds: Int? = null        // TIME — longest hold or set
)

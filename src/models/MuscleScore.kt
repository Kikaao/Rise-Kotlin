package models

// ============================================================
// MuscleScore.kt
// Layer: Models
// ============================================================
// Stores the rank and score for a single muscle group for one user.
// Calculated by MuscleSystem — never written directly by UI or other systems.
//
// Muscle ranks are global (not per-class) and never reset on class switch.
// They reflect the player's physical reality, not their RPG progression.
// ============================================================

// ─────────────────────────────────────────────────────
// MUSCLE RANK
// F → S scale. Thresholds are based on the ratio:
//   REPS_WEIGHT exercises: bestWeightKg / (bodyweightKg × combined coefficient)
//   REPS_ONLY exercises:   bestReps     / (bodyweightKg × combined coefficient)
//   Cardio-driven muscles: distanceKm   / CARDIO_SCALE_FACTOR
//
// If a player has both a REPS_WEIGHT and REPS_ONLY PR for the same
// primary muscle, whichever gives the higher score is used.
//
// All thresholds are TUNABLE — adjust during playtesting.
// ─────────────────────────────────────────────────────

enum class MuscleRank(val label: String, val minScore: Double) {
    F("F", 0.0),        // TUNABLE — unranked / beginner
    D("D", 0.5),        // TUNABLE — below average
    C("C", 0.75),       // TUNABLE — average
    B("B", 1.0),        // TUNABLE — above average (e.g. benching your bodyweight)
    A("A", 1.5),        // TUNABLE — strong (e.g. 1.5× bodyweight)
    S("S", 2.0)         // TUNABLE — elite (e.g. 2× bodyweight)
}

// ─────────────────────────────────────────────────────
// MUSCLE SCORE
// One instance per muscle group per user.
// rawScore is stored so the UI can show exact progress within a rank
// (e.g. "B rank — 1.23 / 1.50 to A").
//
// scoringMethod records how the score was derived — useful for UI
// display ("Based on your best bench press" vs "Based on push-up PR").
// ─────────────────────────────────────────────────────

enum class MuscleScoringMethod {
    WEIGHT_BASED,   // REPS_WEIGHT PR — liftedKg / bodyweight
    REPS_BASED,     // REPS_ONLY PR  — bestReps / bodyweight
    CARDIO_BASED,   // Cardio session distance — for Quads, Hamstrings, Calves, Glutes
    UNRANKED        // No PR logged for this muscle yet
}

data class MuscleScore(
    val userId: Int,
    val muscleGroup: MuscleGroup,
    val rawScore: Double,                   // the computed ratio — used for progress display
    val rank: MuscleRank,                   // derived from rawScore vs MuscleRank thresholds
    val scoringMethod: MuscleScoringMethod, // how the score was calculated
    val basedOnExerciseId: Int? = null,     // which exercise's PR drove this score (null if UNRANKED)
    val lastUpdated: Long = 0L              // Unix ms — when this score was last recalculated
) {
    // Progress within current rank as 0.0–1.0 — used by UI progress bar
    // e.g. score=1.23, current rank B (1.0), next rank A (1.5) → (1.23-1.0)/(1.5-1.0) = 0.46
    val progressToNextRank: Double
        get() {
            val nextRank = MuscleRank.entries.firstOrNull { it.minScore > rank.minScore }
                ?: return 1.0 // already S rank — full bar
            return ((rawScore - rank.minScore) / (nextRank.minScore - rank.minScore))
                .coerceIn(0.0, 1.0)
        }

    // Whether this muscle has any data at all
    val isRanked: Boolean
        get() = scoringMethod != MuscleScoringMethod.UNRANKED
}

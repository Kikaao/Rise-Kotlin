package com.rise.fitrpg.data.models

// Muscle ranks are global — they don't reset on class switch and don't care about RPG progression
// They reflect the player's actual physical strength, not which class they've been grinding
// Calculated entirely by MuscleSystem — nothing else writes to these directly


// MUSCLE RANKS
// F → S scale. Thresholds are based on the ratio:
//   REPS_WEIGHT exercises: bestWeightKg / (bodyweightKg × combined coefficient)
//   REPS_ONLY exercises:   bestReps     / (bodyweightKg × combined coefficient)
//   Cardio-driven muscles: distanceKm   / CARDIO_SCALE_FACTOR
//
// If a player has both a REPS_WEIGHT and REPS_ONLY PR for the same
// primary muscle, whichever gives the higher score is used.


enum class MuscleRank(val label: String, val minScore: Double) {
    F("F", 0.0),        // unranked / beginner
    D("D", 0.5),        // below average
    C("C", 0.75),       // average
    B("B", 1.0),        // above average (e.g. benching your bodyweight)
    A("A", 1.5),        // strong (e.g. 1.5× bodyweight)
    S("S", 2.0)         // elite (e.g. 2× bodyweight)
}


// MUSCLE SCORE
// One instance per muscle group per user.
// rawScore is stored so the UI can show exact progress within a rank
// (e.g. "B rank — 1.23 / 1.50 to A").
//
// scoringMethod records how the score was derived — useful for UI
// display ("Based on your best bench press" vs "Based on push-up PR").
//
//If a 50kg 150cm girl lift 100kg bench press she is a monster
//A 100kg 180 cm lift 100kg he is average
enum class MuscleScoringMethod {
    WEIGHT_BASED,   // REPS_WEIGHT PR  liftedKg / bodyweight
    REPS_BASED,     // REPS_ONLY PR   bestReps / bodyweight
    CARDIO_BASED,   // Cardio session distance  for Quads, Hamstrings, Calves, Glutes
    UNRANKED        // No PR logged for this muscle yet
}

//rawscore is separeted from the rank
data class MuscleScore(
    val userId: Int,
    val muscleGroup: MuscleGroup,
    val rawScore: Double,                   // the computed ratio used for progress display
    val rank: MuscleRank,                   // derived from rawScore vs MuscleRank thresholds
    val scoringMethod: MuscleScoringMethod, // how the score was calculated
    val basedOnExerciseId: Int? = null,     // which exercise's PR drove this score (null if UNRANKED)
    val lastUpdated: Long = 0L              // Unix ms when this score was last recalculated
) {
    // Ai stuff for the future
    // Progress within current rank as 0.0–1.0 by UI progress bar
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

package com.rise.fitrpg.data.models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()


// DISTANCE_BASED  → running, cycling, rowing (log km + optional time)
// INTERVAL_BASED  → sprinting sets, jump rope intervals (log sets × meters)


enum class CardioExerciseType { DISTANCE_BASED, INTERVAL_BASED }


// Applied only when the user logs both distance AND time
// Gives a bonus multiplier on top of base XP

enum class PaceTier(val multiplier: Double, val label: String) {
    ELITE(1.5, "Elite"),
    FAST(1.2, "Fast"),
    INTERMEDIATE(1.0, "Intermediate"),
    SLOW(0.8, "Slow")
}


// Maps a pace (min/km) to a PaceTier
// List must be sorted fastest to slowest (ascending maxPaceMinPerKm)

// Example for running:
// [PaceThreshold(4.0, ELITE), PaceThreshold(6.0, FAST),
//  PaceThreshold(8.0, INTERMEDIATE), PaceThreshold(MAX, SLOW)]

data class PaceThreshold(
    val maxPaceMinPerKm: Double,  // min/km    lower = faster
    val tier: PaceTier
)

// Separate model from Exercise since cardio has no primary muscle group

data class CardioExercise(
    val id: Int,
    val name: String,
    val type: CardioExerciseType,
    val description: String,
    val equipmentType: EquipmentType,
    val statContribution: StatContribution,

    // DISTANCE_BASED
    val baseXpPerKm: Double = 0.0,
    val paceThresholds: List<PaceThreshold> = emptyList(),
    val minDistanceForBonusKm: Double = 0.0,       // below this distance → base XP only, no pace bonus
    val maxRealisticPaceMinPerKm: Double = 0.0,     // anti-gaming cap — pace faster than this is ignored

    // INTERVAL_BASED
    val baseXpPer10Meters: Double = 0.0
)


// What gets logged when the user does a cardio session

// Distance-based: distanceKm required, durationMinutes optional (unlocks pace bonus)

// Interval-based: sets + metersPerSet required, durationMinutes optional

data class CardioSession(
    val id: Int,
    val userId: Int,
    val cardioExerciseId: Int,
    val date: Long,                                // Unix timestamp

    // Distance-based
    val distanceKm: Double? = null,
    val durationMinutes: Double? = null,

    // Interval-based
    val sets: Int? = null,
    val metersPerSet: Int? = null,

    val xpEarned: Int = 0,
    val notes: String? = null
) {
    // Valid only for distance-based sessions where time was logged
    val paceMinPerKm: Double?
        get() = if (distanceKm != null && durationMinutes != null && distanceKm > 0)
            durationMinutes / distanceKm
        else null

    // Valid only for interval-based sessions
    val totalMeters: Int?
        get() = if (sets != null && metersPerSet != null) sets * metersPerSet else null
}

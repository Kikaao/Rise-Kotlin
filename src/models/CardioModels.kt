package models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()

// ─────────────────────────────────────────────────────
// CARDIO EXERCISE TYPE
// DISTANCE_BASED  → running, cycling, rowing (log km + optional time)
// INTERVAL_BASED  → sprinting sets, jump rope intervals (log sets × meters)
// ─────────────────────────────────────────────────────

enum class CardioExerciseType { DISTANCE_BASED, INTERVAL_BASED }

// ─────────────────────────────────────────────────────
// PACE TIER
// Applied only when the user logs both distance AND time
// Gives a bonus multiplier on top of base XP
// ─────────────────────────────────────────────────────

enum class PaceTier(val multiplier: Double, val label: String) {
    ELITE(1.5, "Elite"),
    FAST(1.2, "Fast"),
    INTERMEDIATE(1.0, "Intermediate"),
    SLOW(0.8, "Slow")
}

// ─────────────────────────────────────────────────────
// PACE THRESHOLD
// Each entry maps a pace ceiling (min/km) to a PaceTier
// List must be sorted fastest to slowest (ascending maxPaceMinPerKm)
// System picks the first threshold where userPace <= maxPaceMinPerKm
//
// Example for running:
// [PaceThreshold(4.0, ELITE), PaceThreshold(6.0, FAST),
//  PaceThreshold(8.0, INTERMEDIATE), PaceThreshold(MAX, SLOW)]
// ─────────────────────────────────────────────────────

data class PaceThreshold(
    val maxPaceMinPerKm: Double,  // pace in min/km — lower = faster
    val tier: PaceTier
)

// ─────────────────────────────────────────────────────
// CARDIO EXERCISE
// Separate model from Exercise since cardio has no primary muscle group
// and has fundamentally different XP and tracking logic
// ─────────────────────────────────────────────────────

data class CardioExercise(
    val id: Int,
    val name: String,
    val type: CardioExerciseType,
    val description: String,
    val equipmentType: EquipmentType,
    val statContribution: StatContribution,

    // DISTANCE_BASED fields
    val baseXpPerKm: Double = 0.0,
    val paceThresholds: List<PaceThreshold> = emptyList(),
    val minDistanceForBonusKm: Double = 0.0,       // below this distance → base XP only, no pace bonus
    val maxRealisticPaceMinPerKm: Double = 0.0,     // anti-gaming cap — pace faster than this is ignored

    // INTERVAL_BASED fields
    val baseXpPer10Meters: Double = 0.0
)

// ─────────────────────────────────────────────────────
// CARDIO SESSION
// What gets logged when a user does a cardio workout
//
// Distance-based session:
//   distanceKm    required
//   durationMinutes optional (enables pace bonus if provided)
//
// Interval-based session:
//   sets          required
//   metersPerSet  required
//   durationMinutes optional
// ─────────────────────────────────────────────────────

data class CardioSession(
    val id: Int,
    val userId: Int,                               // Fix: was missing, now required
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
    // Derived — pace in min/km (only valid for distance-based with time logged)
    val paceMinPerKm: Double?
        get() = if (distanceKm != null && durationMinutes != null && distanceKm > 0)
            durationMinutes / distanceKm
        else null

    // Derived — total meters for interval-based
    val totalMeters: Int?
        get() = if (sets != null && metersPerSet != null) sets * metersPerSet else null
}

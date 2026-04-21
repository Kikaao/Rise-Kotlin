package models

enum class MuscleGroup {
    CHEST, FRONT_SHOULDERS, SIDE_SHOULDERS, REAR_SHOULDERS, TRICEPS,
    LATS, UPPER_BACK, BICEPS, FOREARMS,
    ABS, OBLIQUES, LOWER_BACK,
    QUADS, HAMSTRINGS, GLUTES, CALVES, HIP_FLEXORS
}

// Fix: added OUTDOOR for exercises like outdoor cycling and swimming that require
// external environment/equipment but are not BODYWEIGHT or CARDIO_MACHINE.
enum class EquipmentType {
    BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT,
    KETTLEBELL, CARDIO_MACHINE, RESISTANCE_BAND,
    RINGS, EZ_BAR, SMITH_MACHINE, HOME, OUTDOOR
}

// Fix: removed AGILITY — zero exercises ever used this category.
// Add back only when agility exercises are actually added to the library.
enum class ExerciseCategory { STRENGTH, EXPLOSIVE, FLEXIBILITY }

enum class TrackingType {
    REPS_WEIGHT,  // sets × reps × weight  (bench press)
    REPS_ONLY,    // sets × reps            (pull-up)
    TIME          // sets × seconds         (plank, jump rope set)
}

// ─────────────────────────────────────────────────────
// MODE SYSTEM
// Used for exercises that can be logged in more than one context.
// e.g. Jump Rope → Strength (calf sets) OR Cardio (interval session)
// Single-mode exercises leave modes = emptyList()
// ─────────────────────────────────────────────────────

enum class ExerciseContext { STRENGTH, CARDIO, FLEXIBILITY }

enum class XpType { STRENGTH_XP, CARDIO_XP }

// Fix: trackingType is nullable — when context = CARDIO, tracking is handled
// entirely by CardioSession and CardioExercise, not by TrackingType.
// STRENGTH and FLEXIBILITY modes always have a non-null trackingType.
data class ExerciseMode(
    val context: ExerciseContext,
    val trackingType: TrackingType?,   // null when context = CARDIO
    val xpType: XpType
)

// ─────────────────────────────────────────────────────
// STAT CONTRIBUTION
// All six values should sum to 1.0 per exercise.
// ─────────────────────────────────────────────────────

data class StatContribution(
    val strength: Double = 0.0,
    val dexterity: Double = 0.0,
    val endurance: Double = 0.0,
    val flexibility: Double = 0.0,
    val power: Double = 0.0,
    val focus: Double = 0.0
)

// ─────────────────────────────────────────────────────
// EXERCISE
// modes is empty for single-context exercises (the vast majority).
// modes is non-empty only for dual-context exercises (Jump Rope, Sprinting).
// ─────────────────────────────────────────────────────

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: MuscleGroup,
    val primaryMuscles: List<MuscleGroup>,
    val secondaryMuscles: List<MuscleGroup>,
    val category: ExerciseCategory,
    val equipmentType: EquipmentType,
    val trackingType: TrackingType,
    val baseXp: Int,
    val statContribution: StatContribution,
    val description: String,
    val modes: List<ExerciseMode> = emptyList()
) {
    val isMultiMode: Boolean get() = modes.isNotEmpty()
    val cardioMode: ExerciseMode? get() = modes.find { it.context == ExerciseContext.CARDIO }
    val strengthMode: ExerciseMode? get() = modes.find { it.context == ExerciseContext.STRENGTH }
}

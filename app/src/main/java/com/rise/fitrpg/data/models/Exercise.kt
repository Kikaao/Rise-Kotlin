package com.rise.fitrpg.data.models

enum class MuscleGroup {
    CHEST, FRONT_SHOULDERS, SIDE_SHOULDERS, REAR_SHOULDERS, TRICEPS,
    LATS, UPPER_BACK, BICEPS, FOREARMS,
    ABS, OBLIQUES, LOWER_BACK,
    QUADS, HAMSTRINGS, GLUTES, CALVES, HIP_FLEXORS
}

// added outdoor for exercises like outdoor cycling and swimming that require
// external environment/equipment but are not BODYWEIGHT or CARDIO_MACHINE v2.1
enum class EquipmentType {
    BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT,
    KETTLEBELL, CARDIO_MACHINE, RESISTANCE_BAND,
    RINGS, EZ_BAR, SMITH_MACHINE, HOME, OUTDOOR
}

// removed AGILITY no exercises used this category
// Add them back only when we find exercises
enum class ExerciseCategory { STRENGTH, EXPLOSIVE, FLEXIBILITY }


enum class TrackingType {
    REPS_WEIGHT,  // sets × reps × weight  (bench press, sholder press, cable curl...)
    REPS_ONLY,    // sets × reps            (pullup, pushup, squads....)
    TIME          // sets × seconds         (plank, jump rope ....)
}



// ExerciseContext is how the system knows which "version" of the exercise is being logged

enum class ExerciseContext { STRENGTH, CARDIO, FLEXIBILITY }

enum class XpType { STRENGTH_XP, CARDIO_XP }

// tracking is owned by CardioSession (cardiomodels)
data class ExerciseMode(
    val context: ExerciseContext,
    val trackingType: TrackingType?,   // null when context = CARDIO
    val xpType: XpType
)


// All six values should sum to 1.0 per exercise.
data class StatContribution(
    val strength: Double = 0.0,
    val dexterity: Double = 0.0,
    val endurance: Double = 0.0,
    val flexibility: Double = 0.0,
    val power: Double = 0.0,
    val focus: Double = 0.0
)


// modes is empty list, useit when an exercise logs 2 stats, jump rope is strength or cardio

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
    val usesBodyweight: Boolean = false,
    val modes: List<ExerciseMode> = emptyList(),
) {
    // shortcuts for modes
    val isMultiMode: Boolean get() = modes.isNotEmpty()
    val cardioMode: ExerciseMode? get() = modes.find { it.context == ExerciseContext.CARDIO }
    val strengthMode: ExerciseMode? get() = modes.find { it.context == ExerciseContext.STRENGTH }
}
//into of mode at ver 3.3
package models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()





data class WorkoutSet(
    val id: Int,
    val exerciseId: Int,
    val setNumber: Int,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val xpEarned: Int = 0
)


data class WorkoutSession(
    val id: Int,
    val userId: Int,
    val date: Long,
    val fitnessClass: FitnessClass,
    val sets: List<WorkoutSet>,
    val totalXpEarned: Int = 0,
    val notes: String? = null
) {
    val totalSets: Int get() = sets.size
    val uniqueExercises: Int get() = sets.map { it.exerciseId }.distinct().size
}

// PERSONAL RECORD
// The best performance a user has ever logged for a specific exercise.
// One record per user per exercise — updated whenever a new best is achieved.


data class PersonalRecord(
    val id: Int,
    val userId: Int,
    val exerciseId: Int,
    val date: Long,
    val bestWeightKg: Double? = null,
    val bestReps: Int? = null,
    val bestDurationSeconds: Int? = null
)

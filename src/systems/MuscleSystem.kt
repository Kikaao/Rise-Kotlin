package systems

import models.CardioExercise
import models.CardioExerciseType
import models.CardioSession
import models.Exercise
import models.MuscleGroup
import models.MuscleRank
import models.MuscleScore
import models.MuscleScoringMethod
import models.PersonalRecord
import models.PlayerProfile
import models.ProfileCoefficients
import models.TrackingType

// Score is always relative to the player's own body:
//   REPS_WEIGHT PR = bestWeightKg / (bodyweightKg x combined coefficient)
//   REPS_ONLY PR   = bestReps.toDouble() / (bodyweightKg x combined coefficient)
//   If both exist  = whichever gives the higher score wins
//   Cardio muscles = bestDistanceKm / (CARDIO_SCALE_FACTOR x combined coefficient)
//
// Only the exercise primary muscle group drives the score - secondary muscles are ignored
// Muscle scores are global and never reset on class switch
// HIP_FLEXORS is excluded  it exists in MuscleGroup for library accuracy but has no screen

object MuscleSystem {

    // Example: 15km run, male 20s (coefficient 1.0) = 15 / (12.0 x 1.0) = 1.25 = B rank
    // Same run, female 20s (coefficient 0.75) = 15 / (12.0 x 0.75) = 1.67 = A rank
    private const val CARDIO_SCALE_FACTOR = 12.0   // TUNABLE

    private val CARDIO_MUSCLES = setOf(
        MuscleGroup.QUADS,
        MuscleGroup.HAMSTRINGS,
        MuscleGroup.CALVES,
        MuscleGroup.GLUTES
    )

    // HIP_FLEXORS kept in MuscleGroup enum for exercise library accuracy but excluded here
    private val EXCLUDED_MUSCLES = setOf(MuscleGroup.HIP_FLEXORS)

    data class MuscleUpdateResult(
        val updatedScores: List<MuscleScore>,
        val changed: List<MuscleScore>,
        val newRanks: List<Pair<MuscleGroup, MuscleRank>>
    )

    // Recalculates all muscle scores from scratch after every session AI assisted
    // Returns only what changed and any rank-ups for UI notifications
    fun recalculateAll(
        profile: PlayerProfile,
        allPRs: List<PersonalRecord>,
        allExercises: List<Exercise>,
        bestCardioSessions: Map<Int, CardioSession>, // cardioExerciseId - best session
        allCardioExercises: List<CardioExercise>,
        existingScores: List<MuscleScore>,
        nowMs: Long
    ): MuscleUpdateResult {

        val coefficient = ProfileCoefficients.combined(profile)
        val existingMap = existingScores.associateBy { it.muscleGroup }
        val newScores = mutableListOf<MuscleScore>()
        val prsByMuscle = buildPrsByMuscle(allPRs, allExercises)

        for (muscleGroup in MuscleGroup.entries) {
            if (muscleGroup in EXCLUDED_MUSCLES) continue

            val prs = prsByMuscle[muscleGroup]

            val weightScore = prs
                ?.filter { pr -> pr.bestWeightKg != null }
                ?.mapNotNull { pr ->
                    val exercise = allExercises.find { it.id == pr.exerciseId }
                        ?: return@mapNotNull null
                    if (exercise.trackingType != TrackingType.REPS_WEIGHT) return@mapNotNull null
                    val score = pr.bestWeightKg!! / (profile.weightKg * coefficient)
                    Triple(score, MuscleScoringMethod.WEIGHT_BASED, pr.exerciseId)
                }
                ?.maxByOrNull { it.first }

                // Coefficient applied so gender and age normalization is consistent with weight scoring
                // Without it, a 50kg woman and a 120kg man running the same distance get identical scores
            val repsScore = prs
                ?.filter { pr -> pr.bestReps != null }
                ?.mapNotNull { pr ->
                    val exercise = allExercises.find { it.id == pr.exerciseId }
                        ?: return@mapNotNull null
                    if (exercise.trackingType != TrackingType.REPS_ONLY) return@mapNotNull null
                    val score = pr.bestReps!!.toDouble() / (profile.weightKg * coefficient)
                    Triple(score, MuscleScoringMethod.REPS_BASED, pr.exerciseId)
                }
                ?.maxByOrNull { it.first }

            // Fix: coefficient passed to cardio scoring so gender/age normalization is
            // consistent with weight/reps scoring. Without it, a 50kg woman and a 120kg
            // man running the same distance would get identical cardio muscle scores.
            val cardioScore = if (muscleGroup in CARDIO_MUSCLES) {
                calculateCardioScoreForMuscle(bestCardioSessions, allCardioExercises, coefficient)
                    ?.let { Triple(it, MuscleScoringMethod.CARDIO_BASED, null as Int?) }
            } else null

            val best = listOfNotNull(weightScore, repsScore, cardioScore)
                .maxByOrNull { it.first }

            val muscleScore = if (best != null) {
                MuscleScore(
                    userId = profile.userId,
                    muscleGroup = muscleGroup,
                    rawScore = best.first,
                    rank = scoreToRank(best.first),
                    scoringMethod = best.second,
                    basedOnExerciseId = best.third,
                    lastUpdated = nowMs
                )
            } else {
                MuscleScore(
                    userId = profile.userId,
                    muscleGroup = muscleGroup,
                    rawScore = 0.0,
                    rank = MuscleRank.F,
                    scoringMethod = MuscleScoringMethod.UNRANKED,
                    basedOnExerciseId = null,
                    lastUpdated = nowMs
                )
            }

            newScores.add(muscleScore)
        }

        val changed = newScores.filter { new ->
            val old = existingMap[new.muscleGroup]
            old == null || old.rawScore != new.rawScore
        }

        val newRanks = newScores.mapNotNull { new ->
            val old = existingMap[new.muscleGroup]
            if (old != null && new.rank > old.rank) Pair(new.muscleGroup, new.rank)
            else null
        }

        return MuscleUpdateResult(updatedScores = newScores, changed = changed, newRanks = newRanks)
    }

    // Single muscle score for the UI detail screen
    fun getScoreForMuscle(muscleGroup: MuscleGroup, allScores: List<MuscleScore>): MuscleScore? =
        allScores.find { it.muscleGroup == muscleGroup }

    // All ranked muscles sorted strongest first for the UI overview
    fun getRankedMusclesSorted(allScores: List<MuscleScore>): List<MuscleScore> =
        allScores
            .filter { it.isRanked }
            .sortedWith(compareByDescending<MuscleScore> { it.rank }.thenByDescending { it.rawScore })

    // All unranked muscles for UI suggestions on what to train next
    fun getUnrankedMuscles(allScores: List<MuscleScore>): List<MuscleGroup> =
        allScores.filter { !it.isRanked }.map { it.muscleGroup }

    // Groups all PRs by the primary muscle group of their exercise
    private fun buildPrsByMuscle(
        allPRs: List<PersonalRecord>,
        allExercises: List<Exercise>
    ): Map<MuscleGroup, List<PersonalRecord>> {
        val exerciseMap = allExercises.associateBy { it.id }
        return allPRs
            .mapNotNull { pr ->
                val exercise = exerciseMap[pr.exerciseId] ?: return@mapNotNull null
                Pair(exercise.muscleGroup, pr)
            }
            .groupBy({ it.first }, { it.second })
    }

    // Best distance across all distance-based sessions, normalized by scale factor and coefficient
    // Returns null if no distance-based sessions exist yet
    private fun calculateCardioScoreForMuscle(
        bestCardioSessions: Map<Int, CardioSession>,
        allCardioExercises: List<CardioExercise>,
        coefficient: Double
    ): Double? {
        val bestDistance = bestCardioSessions.values
            .filter { session ->
                val exercise = allCardioExercises.find { it.id == session.cardioExerciseId }
                exercise?.type == CardioExerciseType.DISTANCE_BASED
            }
            .mapNotNull { it.distanceKm }
            .maxOrNull() ?: return null

        return bestDistance / (CARDIO_SCALE_FACTOR * coefficient)
    }

    // Find the highest MuscleRank threshold the raw score meets
    private fun scoreToRank(rawScore: Double): MuscleRank =
        MuscleRank.entries
            .sortedByDescending { it.minScore }
            .firstOrNull { rawScore >= it.minScore }
            ?: MuscleRank.F
}

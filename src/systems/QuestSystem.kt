package systems

import kotlin.math.roundToInt
import models.CardioSession
import models.FitnessClass
import models.MuscleGroup
import models.Quest
import models.QuestRarity
import models.QuestType
import models.StreakTier
import models.User
import models.WorkoutSession



// how it works
//
// quests are generated once per week when the week resets.
// Count is determined by StreakTier minimum:
//   PEASANT  → 1 class quest  + 1 balance quest
//   KNIGHT   → 3 class quests + 2 balance quests
//   ELITE    → 5 class quests + 3 balance quests
//
// Class quests: optimized for the player's current class
// Balance quests: target the most neglected stat/class
//
// Progress updates automatically every time a session is logged.
// Quests expire after 7 days regardless of completion.
//
// Cool AI STUF API and i have MONEY to spend on uselles api:(
// Phase 2: AI API replaces generateClassQuests() and generateBalanceQuests()
// with no model changes same Quest data class, same progress system.
//
// this file is AI assisted most of the time it will be written



object QuestSystem {

//AI STUF
    private const val WEEK_IN_MS = 7L * 24L * 60L * 60L * 1000L

    // Quest target ranges per rarity — TUNABLE
    // COMPLETE_WORKOUTS targets
    private const val WORKOUTS_COMMON = 2      // TUNABLE
    private const val WORKOUTS_UNCOMMON = 3    // TUNABLE
    private const val WORKOUTS_RARE = 5        // TUNABLE

    // RUN_DISTANCE targets in km
    private const val DISTANCE_COMMON_KM = 5   // TUNABLE
    private const val DISTANCE_UNCOMMON_KM = 10 // TUNABLE
    private const val DISTANCE_RARE_KM = 20    // TUNABLE

    // LOG_MUSCLE_GROUP_SETS targets
    private const val SETS_COMMON = 10         // TUNABLE
    private const val SETS_UNCOMMON = 20       // TUNABLE
    private const val SETS_RARE = 35           // TUNABLE

    // XP rewards per rarity — TUNABLE
    private const val XP_REWARD_COMMON = 100   // TUNABLE
    private const val XP_REWARD_UNCOMMON = 250 // TUNABLE
    private const val XP_REWARD_RARE = 500     // TUNABLE

    // Gold rewards — only UNCOMMON and RARE give gold — TUNABLE
    private const val GOLD_REWARD_UNCOMMON = 25  // TUNABLE
    private const val GOLD_REWARD_RARE = 75      // TUNABLE

    // Days of inactivity on a stat before a balance quest is triggered
    private const val NEGLECT_THRESHOLD_DAYS = 14  // TUNABLE




    data class QuestGenerationResult(
        val classQuests: List<Quest>,
        val balanceQuests: List<Quest>
    ) {
        val allQuests: List<Quest> get() = classQuests + balanceQuests
    }

    data class QuestProgressResult(
        val updatedQuests: List<Quest>,
        val newlyCompleted: List<Quest>,
        val totalXpEarned: Int,
        val totalGoldEarned: Int
    )


    // Generates a fresh set of quests for the week
    // at the start of new week
    fun generateQuests(
        user: User,
        allWorkoutSessions: List<WorkoutSession>,
        allCardioSessions: List<CardioSession>,
        nowMs: Long
    ): QuestGenerationResult {
        val classQuestCount = getClassQuestCount(user.streakTier)
        val balanceQuestCount = getBalanceQuestCount(user.streakTier)

        val classQuests = generateClassQuests(
            user = user,
            count = classQuestCount,
            nowMs = nowMs
        )

        val balanceQuests = generateBalanceQuests(
            user = user,
            count = balanceQuestCount,
            allWorkoutSessions = allWorkoutSessions,
            allCardioSessions = allCardioSessions,
            nowMs = nowMs
        )

        return QuestGenerationResult(
            classQuests = classQuests,
            balanceQuests = balanceQuests
        )
    }

    // Updates progress on all active quests after a session is logged
    // Returns updated quests, newly completed ones, and rewards earned this call
    fun updateQuestProgress(
        activeQuests: List<Quest>,
        workoutSession: WorkoutSession?,
        cardioSession: CardioSession?,
        user: User,
        nowMs: Long
    ): QuestProgressResult {
        val updatedQuests = activeQuests.map { quest ->
            if (quest.isCompleted || isExpired(quest, nowMs)) return@map quest
            updateSingleQuestProgress(quest, workoutSession, cardioSession, user)
        }

        val newlyCompleted = updatedQuests.filter { updated ->
            val wasCompleted = activeQuests.find { it.id == updated.id }?.isCompleted ?: false
            updated.isCompleted && !wasCompleted
        }

        val totalXp = newlyCompleted.sumOf { it.xpReward }
        val totalGold = newlyCompleted.sumOf { it.goldReward }

        return QuestProgressResult(
            updatedQuests = updatedQuests,
            newlyCompleted = newlyCompleted,
            totalXpEarned = totalXp,
            totalGoldEarned = totalGold
        )
    }

  //returns true if its expired
    fun isExpired(quest: Quest, nowMs: Long): Boolean {
        return nowMs >= quest.expiryMs
    }


    fun getClassQuestCount(streakTier: StreakTier): Int {
        return when (streakTier) {
            StreakTier.PEASANT -> 1
            StreakTier.KNIGHT  -> 3
            StreakTier.ELITE   -> 5
        }
    }

//
//     returns the number of balance quests for a given Streaktier
//     (minimum days / 2) minimum 1 always
//
//     PEASANT: (1/2) = 1
//     KNIGHT:  (3/2) = 2
//     ELITE:   (5/2) = 3
//
    fun getBalanceQuestCount(streakTier: StreakTier): Int {
        val minimum = when (streakTier) {
            StreakTier.PEASANT -> 1
            StreakTier.KNIGHT  -> 3
            StreakTier.ELITE   -> 5
        }
        return maxOf(1, (minimum + 1) / 2)
    }


    // --------------------------------------------------------
    // PRIVATE — QUEST GENERATION
    // --------------------------------------------------------


    //AI STUF
    // Phase 2: this whole function gets replaced by an AI call
    private fun generateClassQuests(
        user: User,
        count: Int,
        nowMs: Long
    ): List<Quest> {
        val quests = mutableListOf<Quest>()
        val currentClass = user.currentClass

        // Primary muscle group for the current class — used for muscle group quests
        val primaryMuscleGroup = getPrimaryMuscleGroup(currentClass)

        // Distribute rarities across quest slots
        // First quest is always COMMON (easy win), last is RARE if count >= 3
        val rarities = buildRarityList(count)

        val questTypes = getQuestTypesForClass(currentClass, count)

        for (i in 0 until count) {
            val rarity = rarities[i]
            val type = questTypes[i]

            quests.add(
                buildQuest(
                    index = i,
                    type = type,
                    rarity = rarity,
                    targetClass = currentClass,
                    isBalanceQuest = false,
                    primaryMuscleGroup = primaryMuscleGroup,
                    streakTier = user.streakTier,
                    nowMs = nowMs
                )
            )
        }

        return quests
    }

    // Generates balance quests trying to balance things out
    // Phase 2: this whole function gets replaced by an AI call
    private fun generateBalanceQuests(
        user: User,
        count: Int,
        allWorkoutSessions: List<WorkoutSession>,
        allCardioSessions: List<CardioSession>,
        nowMs: Long
    ): List<Quest> {
        val quests = mutableListOf<Quest>()

        // Find neglected classes — ordered by most neglected first
        val neglectedClasses = findNeglectedClasses(
            user = user,
            allWorkoutSessions = allWorkoutSessions,
            allCardioSessions = allCardioSessions,
            nowMs = nowMs
        )

        for (i in 0 until count) {
            // Use neglected class if available, otherwise pick lowest level class
            val targetClass = neglectedClasses.getOrElse(i) {
                findLowestLevelClass(user, exclude = neglectedClasses.take(i).toSet())
            }

            val rarity = if (i == 0) QuestRarity.COMMON else QuestRarity.UNCOMMON

            quests.add(
                buildQuest(
                    index = i,
                    type = getBalanceQuestType(targetClass),
                    rarity = rarity,
                    targetClass = targetClass,
                    isBalanceQuest = true,
                    primaryMuscleGroup = getPrimaryMuscleGroup(targetClass),
                    streakTier = user.streakTier,
                    nowMs = nowMs
                )
            )
        }

        return quests
    }

    //AI assisted
    private fun buildQuest(
        index: Int,
        type: QuestType,
        rarity: QuestRarity,
        targetClass: FitnessClass,
        isBalanceQuest: Boolean,
        primaryMuscleGroup: MuscleGroup?,
        streakTier: StreakTier,
        nowMs: Long
    ): Quest {
        val target = getTargetValue(type, rarity, streakTier)
        val title = buildTitle(type, rarity, targetClass, target, primaryMuscleGroup)
        val description = buildDescription(type, target, targetClass, primaryMuscleGroup)
        val xpReward = getXpReward(rarity)
        val goldReward = getGoldReward(rarity)

        return Quest(
            id = "quest_${targetClass.name.lowercase()}_${if (isBalanceQuest) "balance" else "class"}_$index",
            type = type,
            rarity = rarity,
            title = title,
            description = description,
            targetClass = targetClass,
            isBalanceQuest = isBalanceQuest,
            targetValue = target,
            targetMuscleGroup = if (type == QuestType.LOG_MUSCLE_GROUP_SETS) primaryMuscleGroup else null,
            xpReward = xpReward,
            goldReward = goldReward,
            weekStartMs = nowMs,
            expiryMs = nowMs + WEEK_IN_MS
        )
    }



    // Basicly it add stuf to quests when working out
    //
    // If the player switches class mid-week, class quests for the old class stop progressing
    // AI assisted
    private fun updateSingleQuestProgress(
        quest: Quest,
        workoutSession: WorkoutSession?,
        cardioSession: CardioSession?,
        user: User
    ): Quest {
        val addedProgress = when (quest.type) {

            QuestType.COMPLETE_WORKOUTS -> {
                // Fix: count only if the session's class matches the quest's target class
                if (workoutSession != null && workoutSession.fitnessClass == quest.targetClass) 1 else 0
            }

            QuestType.RUN_DISTANCE -> {
                // Round to nearest km — toInt() would truncate 4.9km to 4km and
                // undercount progress. roundToInt() gives fair credit.
                // Called as extension function on Double — roundToInt(it) is a compile error.
                // No class check: RUN_DISTANCE is always cardio, and cardio is class-agnostic
                // for quest progression purposes (the neglect detector already routes this
                // to ADVENTURER balance quests).
                cardioSession?.distanceKm?.let { it.roundToInt() } ?: 0
            }

            QuestType.HIT_STREAK -> {
                // Streak is not incremental — set progress directly to current streak
                // Return 0 here and handle as absolute below.
                // No class check: streak is global across all classes.
                0
            }

            QuestType.LOG_MUSCLE_GROUP_SETS -> {
                // TODO: cross-reference ExerciseLibrary to get muscle group by exerciseId,
                // AND verify workoutSession.fitnessClass == quest.targetClass.
                // Returns 0 until library is injectable at Repository layer.
                // Quest will not progress until this is implemented.
                0
            }
        }

        // HIT_STREAK uses absolute progress (current streak value), not incremental
        val newProgress = if (quest.type == QuestType.HIT_STREAK) {
            user.currentStreak
        } else {
            quest.currentProgress + addedProgress
        }

        val isNowCompleted = newProgress >= quest.targetValue

        return quest.copy(
            currentProgress = newProgress,
            isCompleted = isNowCompleted
        )
    }


    // Finds classes whose relevant sessions have gone quiet for too long
    // Ordered from most neglected to least neglected
    private fun findNeglectedClasses(
        user: User,
        allWorkoutSessions: List<WorkoutSession>,
        allCardioSessions: List<CardioSession>,
        nowMs: Long
    ): List<FitnessClass> {
        val dayInMs = 24L * 60L * 60L * 1000L
        val thresholdMs = nowMs - (NEGLECT_THRESHOLD_DAYS * dayInMs)

        // Check each class except the current one
        return FitnessClass.entries
            .filter { it != user.currentClass }
            .filter { fitnessClass ->
                val lastRelevantSession = getLastRelevantSessionMs(
                    fitnessClass = fitnessClass,
                    allWorkoutSessions = allWorkoutSessions,
                    allCardioSessions = allCardioSessions
                )
                // Neglected if no relevant session in threshold window
                lastRelevantSession < thresholdMs
            }
            .sortedBy { fitnessClass ->
                // Most neglected = earliest last session
                getLastRelevantSessionMs(fitnessClass, allWorkoutSessions, allCardioSessions)
            }
    }

    /**
     * Returns the timestamp of the most recent session relevant to a class.
     * ADVENTURER checks cardio sessions. All others check workout sessions.
     * Returns 0L if no relevant session exists (never trained = most neglected).
     */
    private fun getLastRelevantSessionMs(
        fitnessClass: FitnessClass,
        allWorkoutSessions: List<WorkoutSession>,
        allCardioSessions: List<CardioSession>
    ): Long {
        return when (fitnessClass) {
            FitnessClass.ADVENTURER -> {
                allCardioSessions.maxOfOrNull { it.date } ?: 0L
            }
            else -> {
                allWorkoutSessions
                    .filter { it.fitnessClass == fitnessClass }
                    .maxOfOrNull { it.date } ?: 0L
            }
        }
    }

    /**
     * Finds the class with the lowest level, excluding already-used classes.
     * Used as fallback when not enough neglected classes are found.
     */
    private fun findLowestLevelClass(
        user: User,
        exclude: Set<FitnessClass>
    ): FitnessClass {
        return user.classMap
            .filter { it.key != user.currentClass && it.key !in exclude }
            .minByOrNull { it.value.level }?.key
            ?: FitnessClass.ADVENTURER // fallback
    }


    // --------------------------------------------------------
    // PRIVATE — HELPERS
    // --------------------------------------------------------

    /**
     * Builds a rarity list for N quests.
     * First is always COMMON, last is RARE if count >= 3, rest UNCOMMON.
     */
    private fun buildRarityList(count: Int): List<QuestRarity> {
        return List(count) { index ->
            when {
                index == 0           -> QuestRarity.COMMON
                index == count - 1 && count >= 3 -> QuestRarity.RARE
                else                 -> QuestRarity.UNCOMMON
            }
        }
    }

    /**
     * Returns quest types for a class quest slot list.
     * Distributes types to give variety — no two adjacent quests of same type.
     */
    private fun getQuestTypesForClass(
        fitnessClass: FitnessClass,
        count: Int
    ): List<QuestType> {
        // Base rotation per class — class-appropriate types first
        val rotation = when (fitnessClass) {
            // Fix: ADVENTURER previously had LOG_MUSCLE_GROUP_SETS in slot 5. Adventurer's
            // getPrimaryMuscleGroup returns null (cardio-only class), which meant that at
            // ELITE tier (count=5) the 5th class quest was generated with targetMuscleGroup=null,
            // a broken title ("Log X muscle Sets" fallback), and no way to progress once
            // LOG_MUSCLE_GROUP_SETS tracking is implemented at the Repository layer. Replaced
            // with COMPLETE_WORKOUTS, which is cardio-agnostic and always progresses correctly.
            FitnessClass.ADVENTURER -> listOf(
                QuestType.RUN_DISTANCE,
                QuestType.COMPLETE_WORKOUTS,
                QuestType.HIT_STREAK,
                QuestType.RUN_DISTANCE,
                QuestType.COMPLETE_WORKOUTS
            )
            FitnessClass.CHAMPION, FitnessClass.STRIKER -> listOf(
                QuestType.LOG_MUSCLE_GROUP_SETS,
                QuestType.COMPLETE_WORKOUTS,
                QuestType.HIT_STREAK,
                QuestType.LOG_MUSCLE_GROUP_SETS,
                QuestType.COMPLETE_WORKOUTS
            )
            FitnessClass.MONK -> listOf(
                QuestType.LOG_MUSCLE_GROUP_SETS,
                QuestType.HIT_STREAK,
                QuestType.COMPLETE_WORKOUTS,
                QuestType.LOG_MUSCLE_GROUP_SETS,
                QuestType.HIT_STREAK
            )
            FitnessClass.ROGUE, FitnessClass.PARAGON -> listOf(
                QuestType.COMPLETE_WORKOUTS,
                QuestType.LOG_MUSCLE_GROUP_SETS,
                QuestType.HIT_STREAK,
                QuestType.RUN_DISTANCE,
                QuestType.COMPLETE_WORKOUTS
            )
        }
        return rotation.take(count)
    }

    /**
     * Returns the most appropriate balance quest type for a neglected class.
     */
    private fun getBalanceQuestType(fitnessClass: FitnessClass): QuestType {
        return when (fitnessClass) {
            FitnessClass.ADVENTURER -> QuestType.RUN_DISTANCE
            FitnessClass.MONK       -> QuestType.LOG_MUSCLE_GROUP_SETS
            else                    -> QuestType.COMPLETE_WORKOUTS
        }
    }

    /**
     * Returns the target value for a quest based on type and rarity.
     * HIT_STREAK uses the upper bound of the player's StreakTier.
     */
    private fun getTargetValue(
        type: QuestType,
        rarity: QuestRarity,
        streakTier: StreakTier
    ): Int {
        return when (type) {
            QuestType.COMPLETE_WORKOUTS -> when (rarity) {
                QuestRarity.COMMON   -> WORKOUTS_COMMON
                QuestRarity.UNCOMMON -> WORKOUTS_UNCOMMON
                QuestRarity.RARE     -> WORKOUTS_RARE
            }
            QuestType.RUN_DISTANCE -> when (rarity) {
                QuestRarity.COMMON   -> DISTANCE_COMMON_KM
                QuestRarity.UNCOMMON -> DISTANCE_UNCOMMON_KM
                QuestRarity.RARE     -> DISTANCE_RARE_KM
            }
            QuestType.LOG_MUSCLE_GROUP_SETS -> when (rarity) {
                QuestRarity.COMMON   -> SETS_COMMON
                QuestRarity.UNCOMMON -> SETS_UNCOMMON
                QuestRarity.RARE     -> SETS_RARE
            }
            QuestType.HIT_STREAK -> when (streakTier) {
                // Upper bound of each tier's day range
                StreakTier.PEASANT -> 2
                StreakTier.KNIGHT  -> 4
                StreakTier.ELITE   -> 7
            }
        }
    }

    private fun getXpReward(rarity: QuestRarity): Int {
        return when (rarity) {
            QuestRarity.COMMON   -> XP_REWARD_COMMON
            QuestRarity.UNCOMMON -> XP_REWARD_UNCOMMON
            QuestRarity.RARE     -> XP_REWARD_RARE
        }
    }

    private fun getGoldReward(rarity: QuestRarity): Int {
        return when (rarity) {
            QuestRarity.COMMON   -> 0
            QuestRarity.UNCOMMON -> GOLD_REWARD_UNCOMMON
            QuestRarity.RARE     -> GOLD_REWARD_RARE
        }
    }

    /**
     * Returns the primary muscle group associated with each class.
     * Used to generate LOG_MUSCLE_GROUP_SETS quests.
     */
    private fun getPrimaryMuscleGroup(fitnessClass: FitnessClass): MuscleGroup? {
        return when (fitnessClass) {
            FitnessClass.CHAMPION   -> MuscleGroup.CHEST     // TUNABLE — could be any major compound group
            FitnessClass.STRIKER    -> MuscleGroup.QUADS     // TUNABLE — explosive lower body
            FitnessClass.ROGUE      -> MuscleGroup.CALVES    // TUNABLE — agility/speed focus
            FitnessClass.MONK       -> MuscleGroup.HIP_FLEXORS // TUNABLE — flexibility focus
            FitnessClass.ADVENTURER -> null                  // Adventurer uses cardio, no muscle group
            FitnessClass.PARAGON    -> MuscleGroup.LATS      // TUNABLE — balanced back
        }
    }

    private fun buildTitle(
        type: QuestType,
        rarity: QuestRarity,
        targetClass: FitnessClass,
        target: Int,
        muscleGroup: MuscleGroup?
    ): String {
        return when (type) {
            QuestType.COMPLETE_WORKOUTS     -> "Complete $target Workouts"
            QuestType.RUN_DISTANCE          -> "Cover ${target}km"
            QuestType.HIT_STREAK            -> "Reach a ${target}-Day Streak"
            QuestType.LOG_MUSCLE_GROUP_SETS -> "Log $target ${muscleGroup?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "muscle"} Sets"
        }
    }

    private fun buildDescription(
        type: QuestType,
        target: Int,
        targetClass: FitnessClass,
        muscleGroup: MuscleGroup?
    ): String {
        return when (type) {
            QuestType.COMPLETE_WORKOUTS     -> "Complete $target workout sessions this week as ${targetClass.name.lowercase().replaceFirstChar { it.uppercase() }}."
            QuestType.RUN_DISTANCE          -> "Log ${target}km of cardio this week."
            QuestType.HIT_STREAK            -> "Reach a total streak of $target days."
            QuestType.LOG_MUSCLE_GROUP_SETS -> "Log $target sets targeting ${muscleGroup?.name?.replace("_", " ")?.lowercase() ?: "your primary muscle group"} this week."
        }
    }
}

package systems

import models.Achievement
import models.AchievementCategory
import models.AchievementCondition
import models.CardioExerciseType
import models.CardioSession
import models.FitnessClass
import models.MAX_STAT_VALUE
import models.PersonalRecord
import models.StatType
import models.TimeOperator
import models.User
import models.UserAchievement
import models.WorkoutSession

// ============================================================
// AchievementSystem.kt
// Layer: Systems
// Communicates with: Models only (Achievement, UserAchievement,
//                    User, WorkoutSession, CardioSession,
//                    PersonalRecord)
// Never communicates with: UI, ViewModel, Repository, Database
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================
//
// HOW ACHIEVEMENTS WORK:
//
// After every workout session is saved, the caller passes all
// relevant context to checkAchievements(). The system evaluates
// every locked achievement and returns a list of newly unlocked ones.
//
// Progress-based achievements (progressTarget != null) update their
// currentProgress on every call even before unlock.
//
// Binary achievements (progressTarget == null) either unlock or stay locked.
//
// Secret achievements behave identically to normal ones in logic —
// the UI is responsible for hiding them until unlocked.
//
// PR achievements require a minimum 20% improvement over the previous
// best to count — prevents micro-increment farming.
//
// ============================================================

object AchievementSystem {

    // --------------------------------------------------------
    // TUNABLE CONSTANTS
    // --------------------------------------------------------

    // Minimum percentage improvement over previous PR to count as a new PR
    // for achievement purposes. Prevents adding 1kg every session to farm PRs.
    private const val MIN_PR_IMPROVEMENT_PERCENT = 20.0    // TUNABLE


    // --------------------------------------------------------
    // ACHIEVEMENT TABLE
    // Full list of all achievements in the game.
    // Add new achievements here — zero structural changes needed.
    // --------------------------------------------------------

    val allAchievements: List<Achievement> = listOf(

        // ── STREAK ────────────────────────────────────────────
        Achievement(
            id = "streak_warming_up",
            name = "Warming Up",
            description = "Reach a 3-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(3),
            goldReward = 50
        ),
        Achievement(
            id = "streak_on_a_roll",
            name = "On a Roll",
            description = "Reach a 7-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(7),
            goldReward = 100,
            progressTarget = 7
        ),
        Achievement(
            id = "streak_two_weeks",
            name = "Two Weeks Strong",
            description = "Reach a 14-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(14),
            goldReward = 200,
            progressTarget = 14
        ),
        Achievement(
            id = "streak_iron_will",
            name = "Iron Will",
            description = "Reach a 30-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(30),
            goldReward = 500,
            progressTarget = 30
        ),
        Achievement(
            id = "streak_century",
            name = "Century Streak",
            description = "Reach a 100-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(100),
            goldReward = 1000,
            progressTarget = 100
        ),
        Achievement(
            id = "streak_unbreakable",
            name = "Unbreakable",
            description = "Reach a 365-day streak.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.StreakDays(365),
            goldReward = 5000,
            progressTarget = 365
        ),
        Achievement(
            id = "streak_comeback_kid",
            name = "Comeback Kid",
            description = "Log a workout after 14+ days of inactivity.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.ComebackAfterInactivity(14),
            goldReward = 150
        ),
        Achievement(
            id = "streak_never_miss_monday",
            name = "Never Miss a Monday",
            description = "Log your first workout of the week on Monday, 4 weeks in a row.",
            category = AchievementCategory.STREAK,
            condition = AchievementCondition.NeverMissMonday(4),
            goldReward = 300,
            progressTarget = 4
        ),

        // ── CLASS ─────────────────────────────────────────────
        Achievement(
            id = "class_iron_god",
            name = "Iron God",
            description = "Reach Level 50 as Champion.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.ClassLevel(FitnessClass.CHAMPION, 50),
            goldReward = 1000,
            progressTarget = 50
        ),
        Achievement(
            id = "class_flash_step",
            name = "Flash Step",
            description = "Log 20 sessions with Dexterity as the primary stat gain.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.SessionsAsClass(FitnessClass.ROGUE, 20),
            goldReward = 400,
            progressTarget = 20
        ),
        Achievement(
            id = "class_unstoppable_force",
            name = "Unstoppable Force",
            description = "Log 15 explosive or HIIT sessions.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.ExplosiveSessions(15),
            goldReward = 400,
            progressTarget = 15
        ),
        Achievement(
            id = "class_inner_peace",
            name = "Inner Peace",
            description = "Complete all 20 flexibility exercises at least once.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.AllFlexibilityExercisesCompleted,
            goldReward = 500,
            progressTarget = 20
        ),
        Achievement(
            id = "class_distance_devotee",
            name = "Distance Devotee",
            description = "Log 10 cardio sessions as Adventurer.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.CardioSessionCount(10),
            goldReward = 300,
            progressTarget = 10
        ),
        Achievement(
            id = "class_paragon_path",
            name = "The Paragon Path",
            description = "Have all 6 classes at Level 5 or above simultaneously.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.AllClassesMinLevel(5),
            goldReward = 1500,
            progressTarget = 6
        ),
        Achievement(
            id = "class_tourist",
            name = "Class Tourist",
            description = "Try all 6 classes at least once.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.AllClassesTried,
            goldReward = 300,
            progressTarget = 6
        ),
        Achievement(
            id = "class_jack_of_all",
            name = "Jack of All Trades",
            description = "Reach Level 10 in 3 different classes.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.XClassesAtMinLevel(minLevel = 10, count = 3),
            goldReward = 800,
            progressTarget = 3
        ),
        Achievement(
            id = "class_true_master",
            name = "True Master",
            description = "Reach Level 100 in any class.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.AnyClassLevel(100),
            goldReward = 10000,
            progressTarget = 100
        ),
        Achievement(
            id = "class_legend",
            name = "Legend",
            description = "Reach Overall Level 600 — max all classes.",
            category = AchievementCategory.CLASS,
            condition = AchievementCondition.AllClassesMinLevel(100),
            goldReward = 50000,
            progressTarget = 6  // 0–6 classes at max level — matches evaluator output
        ),

        // ── STAT ──────────────────────────────────────────────
        Achievement(
            id = "stat_ironclad",
            name = "Ironclad",
            description = "Reach 500 Strength.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.STRENGTH, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_titan",
            name = "Titan",
            description = "Reach 1000 Strength (MAX).",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.STRENGTH, 1000),
            goldReward = 2000,
            progressTarget = 1000
        ),
        Achievement(
            id = "stat_lightning_reflexes",
            name = "Lightning Reflexes",
            description = "Reach 500 Dexterity.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.DEXTERITY, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_marathon_soul",
            name = "Marathon Soul",
            description = "Reach 500 Endurance.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.ENDURANCE, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_rubber_spine",
            name = "Rubber Spine",
            description = "Reach 500 Flexibility.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.FLEXIBILITY, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_dynamite",
            name = "Dynamite",
            description = "Reach 500 Power.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.POWER, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_monk_mind",
            name = "Monk Mind",
            description = "Reach 500 Focus.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.StatThreshold(StatType.FOCUS, 500),
            goldReward = 500,
            progressTarget = 500
        ),
        Achievement(
            id = "stat_perfect_form",
            name = "Perfect Form",
            description = "All 6 stats reach 100.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.AllStatsThreshold(100),
            goldReward = 1000,
            progressTarget = 6
        ),
        Achievement(
            id = "stat_balanced_soul",
            name = "Balanced Soul",
            description = "All 6 stats above 200.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.AllStatsThreshold(200),
            goldReward = 2000,
            progressTarget = 6
        ),
        Achievement(
            id = "stat_demigod",
            name = "Demigod",
            description = "Any single stat reaches the maximum of 1000.",
            category = AchievementCategory.STAT,
            condition = AchievementCondition.AnyStatMax,
            goldReward = 3000
        ),

        // ── CARDIO ────────────────────────────────────────────
        Achievement(
            id = "cardio_first_mile",
            name = "First Mile",
            description = "Log your first cardio session.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.CardioSessionCount(1),
            goldReward = 50
        ),
        Achievement(
            id = "cardio_marathon_man",
            name = "Marathon Man",
            description = "Log a single run of 42km or more.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.SingleCardioDistance(42.0),
            goldReward = 2000
        ),
        Achievement(
            id = "cardio_century_rider",
            name = "Century Rider",
            description = "Log 100km total on cycling.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.TotalCardioDistance(100.0),
            goldReward = 800,
            progressTarget = 100
        ),
        Achievement(
            id = "cardio_deep_water",
            name = "Deep Water",
            description = "Log 10 swimming sessions.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.CardioSessionCount(10, CardioExerciseType.DISTANCE_BASED),
            goldReward = 400,
            progressTarget = 10
        ),
        Achievement(
            id = "cardio_elite_pacer",
            name = "Elite Pacer",
            description = "Achieve ELITE pace tier in any cardio session.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.ElitePaceAchieved,
            goldReward = 500
        ),
        Achievement(
            id = "cardio_interval_beast",
            name = "Interval Beast",
            description = "Log 20 interval-based cardio sessions.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.IntervalCardioSessions(20),
            goldReward = 600,
            progressTarget = 20
        ),
        Achievement(
            id = "cardio_ironman",
            name = "Ironman",
            description = "Log 500km total across all cardio sessions.",
            category = AchievementCategory.CARDIO,
            condition = AchievementCondition.TotalCardioDistance(500.0),
            goldReward = 5000,
            progressTarget = 500
        ),

        // ── PERSONAL RECORDS ──────────────────────────────────
        Achievement(
            id = "pr_new_heights",
            name = "New Heights",
            description = "Set your first personal record.",
            category = AchievementCategory.PERSONAL_RECORD,
            condition = AchievementCondition.PRCount(1),
            goldReward = 100
        ),
        Achievement(
            id = "pr_record_breaker",
            name = "Record Breaker",
            description = "Set PRs in 5 different exercises.",
            category = AchievementCategory.PERSONAL_RECORD,
            condition = AchievementCondition.PRCount(5),
            goldReward = 300,
            progressTarget = 5
        ),
        Achievement(
            id = "pr_hall_of_fame",
            name = "Hall of Fame",
            description = "Set PRs in 25 different exercises.",
            category = AchievementCategory.PERSONAL_RECORD,
            condition = AchievementCondition.PRCount(25),
            goldReward = 1500,
            progressTarget = 25
        ),
        Achievement(
            id = "pr_pr_machine",
            name = "PR Machine",
            description = "Set PRs in 10 different exercises.",
            category = AchievementCategory.PERSONAL_RECORD,
            condition = AchievementCondition.PRCount(10),
            goldReward = 700,
            progressTarget = 10
        ),
        Achievement(
            id = "pr_big_four",
            name = "The Big Four",
            description = "Set a PR on Squat, Deadlift, Bench Press, and Overhead Press.",
            category = AchievementCategory.PERSONAL_RECORD,
            // IDs: Barbell Squat, Deadlift, Barbell Bench Press, Overhead Press
            // Exact IDs to be confirmed against ExerciseLibrary — placeholders used
            condition = AchievementCondition.SpecificPRs(listOf(121, 141, 1, 41)), // TUNABLE — verify IDs
            goldReward = 1000,
            progressTarget = 4
        ),

        // ── QUESTS ────────────────────────────────────────────
        Achievement(
            id = "quest_accepted",
            name = "Quest Accepted",
            description = "Complete your first quest.",
            category = AchievementCategory.QUEST,
            condition = AchievementCondition.QuestsCompleted(1),
            goldReward = 100
        ),
        Achievement(
            id = "quest_questaholic",
            name = "Questaholic",
            description = "Complete 25 quests.",
            category = AchievementCategory.QUEST,
            condition = AchievementCondition.QuestsCompleted(25),
            goldReward = 500,
            progressTarget = 25
        ),
        Achievement(
            id = "quest_completionist",
            name = "The Completionist",
            description = "Complete 100 quests.",
            category = AchievementCategory.QUEST,
            condition = AchievementCondition.QuestsCompleted(100),
            goldReward = 2000,
            progressTarget = 100
        ),
        Achievement(
            id = "quest_course_corrected",
            name = "Course Corrected",
            description = "Complete a balance quest.",
            category = AchievementCategory.QUEST,
            condition = AchievementCondition.BalanceQuestCompleted,
            goldReward = 300
        ),

        // ── VOLUME / MILESTONE ────────────────────────────────
        Achievement(
            id = "volume_first_blood",
            name = "First Blood",
            description = "Log your first workout.",
            category = AchievementCategory.VOLUME,
            condition = AchievementCondition.TotalSessions(1),
            goldReward = 50
        ),
        Achievement(
            id = "volume_level_up",
            name = "Level Up!",
            description = "Reach Level 10 in any class.",
            category = AchievementCategory.VOLUME,
            condition = AchievementCondition.AnyClassLevel(10),
            goldReward = 200,
            progressTarget = 10
        ),
        Achievement(
            id = "volume_century_club",
            name = "Century Club",
            description = "Log 100 total workout sessions.",
            category = AchievementCategory.VOLUME,
            condition = AchievementCondition.TotalSessions(100),
            goldReward = 1000,
            progressTarget = 100
        ),
        Achievement(
            id = "volume_one_thousand_sets",
            name = "One Thousand Sets",
            description = "Log 1,000 total sets.",
            category = AchievementCategory.VOLUME,
            condition = AchievementCondition.TotalSets(1000),
            goldReward = 1000,
            progressTarget = 1000
        ),
        Achievement(
            id = "volume_dedicated",
            name = "Dedicated",
            description = "Log a workout every day for a full calendar week (all 7 days).",
            category = AchievementCategory.VOLUME,
            condition = AchievementCondition.StreakDays(7),
            goldReward = 500,
            progressTarget = 7
        ),

        // ── BEHAVIOURAL / SECRET ──────────────────────────────
        Achievement(
            id = "secret_early_bird",
            name = "Early Bird",
            description = "Log a workout before 6:00 AM.",
            category = AchievementCategory.BEHAVIOURAL,
            condition = AchievementCondition.WorkoutTimeOfDay(6, TimeOperator.BEFORE),
            goldReward = 200,
            isSecret = true
        ),
        Achievement(
            id = "secret_night_owl",
            name = "Night Owl",
            description = "Log a workout after 11:00 PM.",
            category = AchievementCategory.BEHAVIOURAL,
            condition = AchievementCondition.WorkoutTimeOfDay(23, TimeOperator.AFTER),
            goldReward = 200,
            isSecret = true
        ),
        Achievement(
            id = "secret_weekend_warrior",
            name = "Weekend Warrior",
            description = "Log 10 workouts on Saturdays or Sundays.",
            category = AchievementCategory.BEHAVIOURAL,
            condition = AchievementCondition.WeekendWorkouts(10),
            goldReward = 300,
            isSecret = true,
            progressTarget = 10
        ),
        Achievement(
            id = "secret_all_nighter",
            name = "All-Nighter",
            description = "Log a workout on Saturday after 23:00 and Sunday before 07:00 in the same weekend.",
            category = AchievementCategory.BEHAVIOURAL,
            condition = AchievementCondition.AllNighter,
            goldReward = 500,
            isSecret = true
        ),
        Achievement(
            id = "secret_minimalist",
            name = "The Minimalist",
            description = "Complete a full session using bodyweight only with 20 or more sets.",
            category = AchievementCategory.BEHAVIOURAL,
            condition = AchievementCondition.BodyweightOnlySession(20),
            goldReward = 300,
            isSecret = true
        )
    )


    // --------------------------------------------------------
    // CONTEXT — everything needed to evaluate all achievements
    // --------------------------------------------------------

    /**
     * All data needed to evaluate achievements after a workout.
     * Passed as a single object so checkAchievements() has one clean parameter.
     *
     * Caller contract (important):
     * previousLastWorkoutDateMs must be captured BEFORE StreakSystem.onWorkoutLogged
     * mutates the user's lastWorkoutDate. Without this, ComebackAfterInactivity can
     * never fire — the gap from "last workout" to "this workout" is always 0 because
     * the field was just overwritten by the current timestamp.
     *
     * Fix: removed redundant classesSwitchedTo field. User already carries
     * classesSwitchedTo: MutableSet<FitnessClass> — passing it separately forced the
     * caller to keep two copies in sync, and one could silently drift from the other.
     * Evaluator now reads ctx.user.classesSwitchedTo directly.
     */
    data class AchievementContext(
        val user: User,
        val allWorkoutSessions: List<WorkoutSession>,
        val allCardioSessions: List<CardioSession>,
        val personalRecords: List<PersonalRecord>,
        val previousPersonalRecords: List<PersonalRecord>, // PRs before this session — for 20% check
        val totalQuestsCompleted: Int,
        val balanceQuestsCompleted: Int,
        val latestSessionTimestampMs: Long,                // timestamp of the session just logged
        val previousLastWorkoutDateMs: Long                // user.lastWorkoutDate BEFORE the streak update — for ComebackAfterInactivity
    )


    // --------------------------------------------------------
    // RESULT TYPE
    // --------------------------------------------------------

    data class AchievementCheckResult(
        val newlyUnlocked: List<Achievement>,              // achievements unlocked this session
        val updatedProgress: List<UserAchievement>,        // all achievements with updated progress
        val goldEarned: Int                                // total gold from this session's unlocks
    )


    // --------------------------------------------------------
    // PUBLIC FUNCTIONS
    // --------------------------------------------------------

    /**
     * Main entry point. Called after every workout session is saved.
     * Evaluates every achievement and returns newly unlocked ones
     * plus updated progress for all progress-based achievements.
     *
     * @param context           All data needed to evaluate conditions
     * @param currentProgress   The user's existing UserAchievement list
     */
    fun checkAchievements(
        context: AchievementContext,
        currentProgress: List<UserAchievement>
    ): AchievementCheckResult {
        val progressMap = currentProgress.associateBy { it.achievementId }.toMutableMap()
        val newlyUnlocked = mutableListOf<Achievement>()

        for (achievement in allAchievements) {
            val existing = progressMap[achievement.id]
                ?: UserAchievement(achievementId = achievement.id)

            // Skip already unlocked non-repeatable achievements
            if (existing.isUnlocked && !achievement.isRepeatable) continue

            val (isNowMet, newProgress) = evaluateCondition(achievement.condition, context)

            val updated = existing.copy(
                currentProgress = newProgress ?: existing.currentProgress,
                isUnlocked = if (isNowMet) true else existing.isUnlocked,
                unlockedAt = if (isNowMet && !existing.isUnlocked) context.latestSessionTimestampMs else existing.unlockedAt,
                timesCompleted = if (isNowMet && achievement.isRepeatable) existing.timesCompleted + 1 else existing.timesCompleted
            )

            progressMap[achievement.id] = updated

            if (isNowMet && !existing.isUnlocked) {
                newlyUnlocked.add(achievement)
            }
        }

        val totalGold = newlyUnlocked.sumOf { it.goldReward }

        return AchievementCheckResult(
            newlyUnlocked = newlyUnlocked,
            updatedProgress = progressMap.values.toList(),
            goldEarned = totalGold
        )
    }


    // --------------------------------------------------------
    // PRIVATE — CONDITION EVALUATOR
    // Returns (isMet: Boolean, currentProgress: Int?)
    // currentProgress is null for binary achievements (no progress bar)
    // --------------------------------------------------------

    private fun evaluateCondition(
        condition: AchievementCondition,
        ctx: AchievementContext
    ): Pair<Boolean, Int?> {

        return when (condition) {

            // ── STREAK ────────────────────────────────────────
            is AchievementCondition.StreakDays ->
                Pair(ctx.user.currentStreak >= condition.days, ctx.user.currentStreak)

            is AchievementCondition.ComebackAfterInactivity -> {
                // Fix: was reading ctx.user.lastWorkoutDate, which has already been
                // overwritten to latestSessionTimestampMs by StreakSystem.onWorkoutLogged
                // before AchievementSystem runs. That made the gap always 0 and the
                // achievement impossible to unlock. Now reads previousLastWorkoutDateMs,
                // which the caller captures BEFORE the streak update.
                //
                // Edge case: a brand-new user has previousLastWorkoutDateMs = 0L, which
                // would compute a ~50-year gap and instantly unlock the achievement on
                // first workout. Guard against that: if previous == 0L, the user has
                // never worked out before — there's no "comeback" to have.
                if (ctx.previousLastWorkoutDateMs == 0L) {
                    Pair(false, null)
                } else {
                    val gapDays = daysBetween(ctx.previousLastWorkoutDateMs, ctx.latestSessionTimestampMs)
                    Pair(gapDays >= condition.inactiveDays, null)
                }
            }

            is AchievementCondition.NeverMissMonday -> {
                // Evaluated externally — requires week history not available here
                // Returns false until a dedicated Monday-tracking field is added
                // TODO: implement when week history is available
                Pair(false, null)
            }

            // ── CLASS ─────────────────────────────────────────
            is AchievementCondition.ClassLevel -> {
                // Check the specific class level
                val classLevel = ctx.user.classMap[condition.fitnessClass]?.level ?: 0
                Pair(classLevel >= condition.level, classLevel)
            }

            is AchievementCondition.AnyClassLevel -> {
                // Check if any class has reached the target level
                val maxLevel = ctx.user.classMap.values.maxOfOrNull { it.level } ?: 0
                Pair(maxLevel >= condition.level, maxLevel)
            }

            is AchievementCondition.AllClassesMinLevel -> {
                val classesAtMin = ctx.user.classMap.values.count { it.level >= condition.minLevel }
                Pair(classesAtMin >= 6, classesAtMin)
            }

            is AchievementCondition.XClassesAtMinLevel -> {
                // Counts how many classes have reached minLevel — unlocks at count
                val classesAtMin = ctx.user.classMap.values.count { it.level >= condition.minLevel }
                Pair(classesAtMin >= condition.count, classesAtMin)
            }

            is AchievementCondition.AllClassesTried -> {
                // Fix: now reads from ctx.user.classesSwitchedTo directly. Previously read from
                // ctx.classesSwitchedTo, which was a redundant field that duplicated the user's.
                val count = ctx.user.classesSwitchedTo.size
                Pair(count >= 6, count)
            }

            is AchievementCondition.SessionsAsClass -> {
                val count = ctx.allWorkoutSessions.count { it.fitnessClass == condition.fitnessClass }
                Pair(count >= condition.sessions, count)
            }

            is AchievementCondition.ExplosiveSessions -> {
                // TODO: cross-reference with ExerciseLibrary.getByCategory(EXPLOSIVE)
                // Returns false until ExerciseLibrary is injectable at Repository layer.
                // A never-unlocking achievement is better than one that unlocks incorrectly.
                Pair(false, 0)
            }

            is AchievementCondition.AllFlexibilityExercisesCompleted -> {
                // Flexibility exercises are IDs 301–320 (20 total)
                val flexIds = (301..320).toSet()
                val loggedFlexIds = ctx.allWorkoutSessions
                    .flatMap { it.sets }
                    .map { it.exerciseId }
                    .filter { it in flexIds }
                    .toSet()
                Pair(loggedFlexIds.size >= 20, loggedFlexIds.size)
            }

            // ── STAT ──────────────────────────────────────────
            is AchievementCondition.StatThreshold -> {
                val value = getStatValue(ctx.user, condition.stat)
                Pair(value >= condition.value, value)
            }

            is AchievementCondition.AllStatsThreshold -> {
                val stats = ctx.user.stats
                val above = listOf(
                    stats.strength, stats.dexterity, stats.endurance,
                    stats.flexibility, stats.power, stats.focus
                ).count { it >= condition.value }
                Pair(above >= 6, above)
            }

            is AchievementCondition.AnyStatMax -> {
                // Fix: was hard-coded to 1000. Now reads MAX_STAT_VALUE from Models.kt — if
                // Stamatis tunes MAX_STAT_VALUE, the Demigod achievement follows automatically.
                val stats = ctx.user.stats
                val isMax = listOf(
                    stats.strength, stats.dexterity, stats.endurance,
                    stats.flexibility, stats.power, stats.focus
                ).any { it >= MAX_STAT_VALUE }
                Pair(isMax, null)
            }

            // ── CARDIO ────────────────────────────────────────
            is AchievementCondition.CardioSessionCount -> {
                val count = if (condition.type != null) {
                    // TODO: inject CardioExerciseLibrary to filter by type
                    // Returns false until library is injectable at Repository layer.
                    return Pair(false, 0)
                } else {
                    ctx.allCardioSessions.size
                }
                Pair(count >= condition.count, count)
            }

            is AchievementCondition.SingleCardioDistance -> {
                val met = ctx.allCardioSessions.any { (it.distanceKm ?: 0.0) >= condition.km }
                Pair(met, null)
            }

            is AchievementCondition.TotalCardioDistance -> {
                // Fix: was summing distances to Double, then calling .toInt() BEFORE comparing
                // to condition.km (also Double). Result: a player with 49.9 km total would fail
                // a target of 50.0 because total got truncated to 49. Now we keep the sum as
                // Double for the comparison and only truncate when reporting progress for
                // the UI progress bar (which is Int).
                val totalDouble = ctx.allCardioSessions.sumOf { it.distanceKm ?: 0.0 }
                val met = totalDouble >= condition.km
                Pair(met, totalDouble.toInt())
            }

            is AchievementCondition.ElitePaceAchieved -> {
                // Elite pace is pace <= 4.0 min/km for running — actual threshold
                // depends on exercise. Simplified check: paceMinPerKm <= 4.0
                val met = ctx.allCardioSessions.any { session ->
                    val pace = session.paceMinPerKm ?: Double.MAX_VALUE
                    pace <= 4.0 // TUNABLE — use exercise-specific threshold when available
                }
                Pair(met, null)
            }

            is AchievementCondition.IntervalCardioSessions -> {
                // TODO: filter by INTERVAL_BASED type using CardioExerciseLibrary
                // Returns false until library is injectable at Repository layer.
                Pair(false, 0)
            }

            // ── PERSONAL RECORDS ──────────────────────────────
            is AchievementCondition.PRCount -> {
                // Only count PRs that are at least MIN_PR_IMPROVEMENT_PERCENT better
                // than the previous best for that exercise.
                // Fix: isSignificantPR now handles REPS_WEIGHT, REPS_ONLY, and TIME.
                // Previously only REPS_WEIGHT PRs could count — bodyweight athletes
                // were silently excluded from every PRCount achievement.
                val qualifyingPrCount = ctx.personalRecords.count { current ->
                    val previous = ctx.previousPersonalRecords.find { it.exerciseId == current.exerciseId }
                    isSignificantPR(current, previous)
                }
                Pair(qualifyingPrCount >= condition.count, qualifyingPrCount)
            }

            is AchievementCondition.SpecificPRs -> {
                val loggedExerciseIds = ctx.personalRecords.map { it.exerciseId }.toSet()
                val completed = condition.exerciseIds.count { it in loggedExerciseIds }
                Pair(completed >= condition.exerciseIds.size, completed)
            }

            // ── QUESTS ────────────────────────────────────────
            is AchievementCondition.QuestsCompleted ->
                Pair(ctx.totalQuestsCompleted >= condition.count, ctx.totalQuestsCompleted)

            is AchievementCondition.BalanceQuestCompleted ->
                Pair(ctx.balanceQuestsCompleted > 0, null)

            // ── VOLUME / MILESTONE ────────────────────────────
            is AchievementCondition.TotalSessions -> {
                val count = ctx.allWorkoutSessions.size
                Pair(count >= condition.count, count)
            }

            is AchievementCondition.TotalSets -> {
                val count = ctx.allWorkoutSessions.sumOf { it.sets.size }
                Pair(count >= condition.count, count)
            }

            is AchievementCondition.BodyweightOnlySession -> {
                // TODO: cross-reference ExerciseLibrary to check equipment type per exerciseId
                // Returns false until library is injectable at Repository layer.
                Pair(false, null)
            }

            // ── BEHAVIOURAL / SECRET ──────────────────────────
            is AchievementCondition.WorkoutTimeOfDay -> {
                val hour = getHourFromTimestamp(ctx.latestSessionTimestampMs)
                val met = when (condition.operator) {
                    TimeOperator.BEFORE -> hour < condition.hour
                    TimeOperator.AFTER  -> hour >= condition.hour
                }
                Pair(met, null)
            }

            is AchievementCondition.WeekendWorkouts -> {
                val count = ctx.allWorkoutSessions.count { isWeekend(it.date) }
                Pair(count >= condition.count, count)
            }

            is AchievementCondition.AllNighter -> {
                // Requires Saturday workout at 23:00+ and Sunday workout before 07:00
                // in the same weekend — checked across session history
                val met = checkAllNighter(ctx.allWorkoutSessions)
                Pair(met, null)
            }
        }
    }


    // --------------------------------------------------------
    // PRIVATE HELPERS
    // --------------------------------------------------------

    private fun getStatValue(user: User, stat: StatType): Int {
        return when (stat) {
            StatType.STRENGTH    -> user.stats.strength
            StatType.DEXTERITY   -> user.stats.dexterity
            StatType.ENDURANCE   -> user.stats.endurance
            StatType.FLEXIBILITY -> user.stats.flexibility
            StatType.POWER       -> user.stats.power
            StatType.FOCUS       -> user.stats.focus
        }
    }

    /**
     * Returns true if the new PR is at least MIN_PR_IMPROVEMENT_PERCENT better
     * than the previous PR for the same exercise. Handles all three tracking types.
     *
     * Fix: old signature only took bestWeightKg, so every REPS_ONLY exercise (pull-ups,
     * dips, push-ups…) and every TIME exercise (plank, L-sit, flex holds…) returned false
     * because bestWeightKg was null. A pure bodyweight athlete could never unlock any
     * PRCount achievement. Now handles all three PR shapes:
     *   REPS_WEIGHT: compare bestWeightKg
     *   REPS_ONLY:   compare bestReps
     *   TIME:        compare bestDurationSeconds
     *
     * Convention (from WorkoutModels.kt): a PR has exactly one primary metric field set.
     * We detect which one the new PR uses and compare the same field on the previous PR.
     *
     * No previous PR of the same type → any logged value qualifies (first PR).
     * Current PR has no populated metric field → returns false (malformed PR).
     */
    private fun isSignificantPR(current: PersonalRecord, previous: PersonalRecord?): Boolean {
        // REPS_WEIGHT: bestWeightKg is the primary metric
        if (current.bestWeightKg != null) {
            val prev = previous?.bestWeightKg ?: return true // first PR — always counts
            val improvement = ((current.bestWeightKg - prev) / prev) * 100.0
            return improvement >= MIN_PR_IMPROVEMENT_PERCENT
        }
        // REPS_ONLY: bestReps is the primary metric (and bestWeightKg is null)
        if (current.bestReps != null) {
            val prev = previous?.bestReps ?: return true // first PR — always counts
            if (prev <= 0) return true // degenerate previous value — treat as first PR
            val improvement = ((current.bestReps - prev).toDouble() / prev) * 100.0
            return improvement >= MIN_PR_IMPROVEMENT_PERCENT
        }
        // TIME: bestDurationSeconds is the primary metric
        if (current.bestDurationSeconds != null) {
            val prev = previous?.bestDurationSeconds ?: return true // first PR — always counts
            if (prev <= 0) return true
            val improvement = ((current.bestDurationSeconds - prev).toDouble() / prev) * 100.0
            return improvement >= MIN_PR_IMPROVEMENT_PERCENT
        }
        // No populated metric field — PR is malformed, does not count
        return false
    }

    private fun daysBetween(fromMs: Long, toMs: Long): Int {
        val dayInMs = 24L * 60L * 60L * 1000L
        return ((toMs - fromMs) / dayInMs).toInt()
    }

    /**
     * Returns the local hour (0–23) from a Unix ms timestamp.
     * Applies the device timezone offset so Early Bird / Night Owl
     * trigger on local time, not UTC.
     *
     * Phase 2: replace with Android Calendar API for full accuracy.
     */
    private fun getHourFromTimestamp(timestampMs: Long): Int {
        val tz = java.util.TimeZone.getDefault()
        val localMs = timestampMs + tz.getOffset(timestampMs)
        val dayInMs = 24L * 60L * 60L * 1000L
        val hourInMs = 60L * 60L * 1000L
        return ((localMs % dayInMs) / hourInMs).toInt()
    }

    private fun isWeekend(timestampMs: Long): Boolean {
        return getDayOfWeek(timestampMs) >= 5 // 5=Sat, 6=Sun
    }

    /**
     * Returns day of week (0=Mon, 1=Tue ... 5=Sat, 6=Sun) in local timezone.
     * Applies timezone offset before calculating — without this, users near midnight
     * in non-UTC timezones would get the wrong day.
     */
    private fun getDayOfWeek(timestampMs: Long): Int {
        val tz = java.util.TimeZone.getDefault()
        val localMs = timestampMs + tz.getOffset(timestampMs)
        val dayInMs = 24L * 60L * 60L * 1000L
        return ((localMs / dayInMs) + 3).toInt() % 7 // 0=Mon, 5=Sat, 6=Sun
    }

    /**
     * Checks if the user ever logged a workout on Saturday 23:00+
     * AND the following Sunday before 07:00 in the same weekend.
     * All checks use local timezone via getDayOfWeek and getHourFromTimestamp.
     */
    private fun checkAllNighter(sessions: List<WorkoutSession>): Boolean {
        val saturdayLate = sessions.filter { session ->
            getDayOfWeek(session.date) == 5 && getHourFromTimestamp(session.date) >= 23
        }

        val sundayEarly = sessions.filter { session ->
            getDayOfWeek(session.date) == 6 && getHourFromTimestamp(session.date) < 7
        }

        val dayInMs = 24L * 60L * 60L * 1000L

        return saturdayLate.any { sat ->
            sundayEarly.any { sun ->
                sun.date > sat.date && (sun.date - sat.date) <= dayInMs
            }
        }
    }
}
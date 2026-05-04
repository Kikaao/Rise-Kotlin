package com.rise.fitrpg.data.models

// Used for grouping achievements


enum class AchievementCategory {
    STREAK,
    CLASS,
    STAT,
    CARDIO,
    PERSONAL_RECORD,
    QUEST,
    VOLUME,
    BEHAVIOURAL
}

// stat-based achievement conditions

enum class StatType {
    STRENGTH, DEXTERITY, ENDURANCE, FLEXIBILITY, POWER, FOCUS
}

// Used by WorkoutTimeOfDay condition — whether the workout was before or after a given hour

enum class TimeOperator { BEFORE, AFTER }

// 90% AI Genarated Code
// One subclass per distinct condition shape

sealed class AchievementCondition {

    //Streak
    data class StreakDays(val days: Int) : AchievementCondition()
    data class ComebackAfterInactivity(val inactiveDays: Int) : AchievementCondition()
    data class NeverMissMonday(val consecutiveWeeks: Int) : AchievementCondition()

    //  Classs
    data class ClassLevel(val fitnessClass: FitnessClass, val level: Int) : AchievementCondition()
    data class AnyClassLevel(val level: Int) : AchievementCondition()
    data class AllClassesMinLevel(val minLevel: Int) : AchievementCondition()

    // X out of 6 classes at level minLevel or above (e.g. Jack of All Trades: 3 classes at 10)
    data class XClassesAtMinLevel(val minLevel: Int, val count: Int) : AchievementCondition()

    // Player has switched to all 6 classes at least once
    object AllClassesTried : AchievementCondition()

    // X sessions where a specific class was active
    data class SessionsAsClass(val fitnessClass: FitnessClass, val sessions: Int) : AchievementCondition()

    // X sessions with explosive/HIIT exercises (category = EXPLOSIVE)
    data class ExplosiveSessions(val sessions: Int) : AchievementCondition()

    // All 20 flexibility exercises logged at least once
    object AllFlexibilityExercisesCompleted : AchievementCondition()

    // Stat
    data class StatThreshold(val stat: StatType, val value: Int) : AchievementCondition()

    // All 6 stats reach value
    data class AllStatsThreshold(val value: Int) : AchievementCondition()

    // Any single stat reaches MAX (1000)
    object AnyStatMax : AchievementCondition()

    // Cadio
    data class CardioSessionCount(
        val count: Int,
        val type: CardioExerciseType? = null  // null = any cardio type
    ) : AchievementCondition()

    // Single cardio session with distance >= km
    data class SingleCardioDistance(val km: Double) : AchievementCondition()

    // Cumulative total distance >= km across all sessions
    data class TotalCardioDistance(val km: Double) : AchievementCondition()

    // Achieved ELITE pace tier in any single session
    object ElitePaceAchieved : AchievementCondition()

    // X interval-based cardio sessions logged
    data class IntervalCardioSessions(val sessions: Int) : AchievementCondition()

    // PR
    data class PRCount(val count: Int) : AchievementCondition()

    // PR set on ALL exercises in a specific list (e.g. The Big Four)
    data class SpecificPRs(val exerciseIds: List<Int>) : AchievementCondition()

    // ── QUESTS ────────────────────────────────────────────
    // Total quests completed >= count
    data class QuestsCompleted(val count: Int) : AchievementCondition()

    // At least one balance quest completed
    object BalanceQuestCompleted : AchievementCondition()

    // milestones
    data class TotalSessions(val count: Int) : AchievementCondition()

    // Total sets logged across all sessions >= count
    data class TotalSets(val count: Int) : AchievementCondition()

    // Full session: bodyweight only, minimum X sets
    data class BodyweightOnlySession(val minSets: Int) : AchievementCondition()

    // secret
    data class WorkoutTimeOfDay(val hour: Int, val operator: TimeOperator) : AchievementCondition()

    // X workouts logged on Saturdays or Sundays
    data class WeekendWorkouts(val count: Int) : AchievementCondition()

    // Workout on Saturday 23:00+ AND Sunday before 07:00 in the same weekend
    object AllNighter : AchievementCondition()
}


data class Achievement(
    val id: String,                         // unique key e.g. "streak_7"
    val name: String,                       // display name
    val description: String,                // shown in achievement gallery
    val category: AchievementCategory,
    val condition: AchievementCondition,
    val goldReward: Int,                    // GOLD awarded on unlock
    val isSecret: Boolean = false,          // hidden until unlocked
    val isRepeatable: Boolean = false,      // can be unlocked more than once
    val progressTarget: Int? = null         // non-null = show progress bar (target value)
                                            // null = binary achievement, no progress bar
)

data class UserAchievement(
    val achievementId: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,           // Unix ms timestamp of unlock
    val currentProgress: Int = 0,          // used when progressTarget != null
    val timesCompleted: Int = 0            // for repeatable achievements
)

package models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()

// Maximum value any stat can reach — used by the class head start formula and stat cap logic
const val MAX_STAT_VALUE = 1000

// ─── STATS ──────────────────────────────────────────────
data class Stats(
    var strength: Int = 0,
    var dexterity: Int = 0,
    var endurance: Int = 0,
    var flexibility: Int = 0,
    var power: Int = 0,
    var focus: Int = 0
)

// ─── CLASS ENUM ─────────────────────────────────────────
enum class FitnessClass {
    CHAMPION, STRIKER, ROGUE, MONK, ADVENTURER, PARAGON
}

// ─── STREAK TIER ────────────────────────────────────────
// Fix: renamed CHAMPION → ELITE to avoid clash with FitnessClass.CHAMPION.
// Fix: clean non-overlapping ranges (was 1-3, 3-5, 5-7 — 3 and 5 existed in two tiers).
enum class StreakTier(val minDaysPerWeek: Int, val maxDaysPerWeek: Int) {
    PEASANT(1, 2),
    KNIGHT(3, 4),
    ELITE(5, 7)
}

// ─── CLASS PROGRESS ─────────────────────────────────────
// Fix: added totalXp — xp resets on every level up, totalXp never resets.
// totalXp is needed for achievements, leaderboards, and lifetime stats screens.
data class ClassProgress(
    val fitnessClass: FitnessClass,
    var xp: Int = 0,         // XP within current level — resets on level up
    var totalXp: Int = 0,    // cumulative XP ever earned as this class — never resets
    var level: Int = 0
)

// ─── USER ────────────────────────────────────────────────
// Fix: added email (required for JWT auth), createdAt (registration timestamp).
// Fix: added weeklyWorkoutCount and weekStartDate (required for streak calculation).
// Fix: classMap pre-populated with all 6 classes at level 0 — prevents null access
//      in systems code before a class has ever been played.
// Fix: added hasUsedFreeSwitch — every new account gets one free class switch
//      that does not respect the 2-month cooldown. Does not stack — once used, gone.
//      Explicit boolean is cleaner than relying on lastClassSwitch == 0L as a sentinel.
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val createdAt: Long = 0L,
    var currentClass: FitnessClass = FitnessClass.ADVENTURER,
    var streakTier: StreakTier = StreakTier.PEASANT,
    var currentStreak: Int = 0,
    var weeklyWorkoutCount: Int = 0,    // resets at the start of every week
    var weekStartDate: Long = 0L,       // timestamp of when the current week started
    var lastWorkoutDate: Long = 0L,
    var lastClassSwitch: Long = 0L,     // Unix ms — enforces 2-month cooldown between switches
    var hasUsedFreeSwitch: Boolean = false, // true once the one-time free switch is consumed
    // Fix: gold changed from val to var. AchievementSystem.checkAchievements() and
    // QuestSystem.updateQuestProgress() both return a goldEarned amount that the caller
    // must apply to the user. With val, the only way to update was user.copy(gold = ...),
    // which was undocumented and silently error-prone — a caller who forgot the copy
    // would lose all gold on the floor. var matches the pattern used for every other
    // mutable User field (currentStreak, weeklyWorkoutCount, hasUsedFreeSwitch, etc.).
    var gold: Int = 0,
    val classesSwitchedTo: MutableSet<FitnessClass> = mutableSetOf(),
    val stats: Stats = Stats(),
    val inventory: MutableMap<ItemType, InventoryItem> = mutableMapOf(),
    val classMap: MutableMap<FitnessClass, ClassProgress> = FitnessClass.entries
        .associateWith { ClassProgress(it) }
        .toMutableMap()
) {
    val overallLevel: Int
        get() = classMap.values.sumOf { it.level }
}

package com.rise.fitrpg.data.models

// All timestamps are Unix time in milliseconds — System.currentTimeMillis()

// Maximum value any stat can reach — used by the class head start formula and stat cap logic
const val MAX_STAT_VALUE = 1000

// Statssssssssssss
data class Stats(
    var strength: Int = 0,
    var dexterity: Int = 0,
    var endurance: Int = 0,
    var flexibility: Int = 0,
    var power: Int = 0,
    var focus: Int = 0
)
// Classsssssssssssss
// switching has a cooldown (see ClassSwitchSystem)
enum class FitnessClass {
    CHAMPION, STRIKER, ROGUE, MONK, ADVENTURER, PARAGON
}

// Streak by class
// renamed CHAMPION to ELITE to avoid clash with FitnessClass.CHAMPION
// Fix: clean non-overlapping ranges (was 1-3, 3-5, 5-7 — 3 and 5 existed in two tiers).
enum class StreakTier(val minDaysPerWeek: Int, val maxDaysPerWeek: Int) {
    PEASANT(1, 2),
    KNIGHT(3, 4),
    ELITE(5, 7)
}

// Class progresss
// xp resets on level up totalXp never does
// totalXp is needed for achievements, leaderboards, and lifetime stats screens and and we will see in de future
data class ClassProgress(
    val fitnessClass: FitnessClass,
    var xp: Int = 0,         //current level xp
    var totalXp: Int = 0,
    var level: Int = 0
)

// Userrr
// added weeklyWorkoutCount and weekStartDate (required for streak calculation) v2.1
// added hasUsedFreeSwitch every new account gets one free class switch then cooldowwn 2 months do not stack
// added lastClassSwitch == 0L didnt understand why long and why 0L, v2.0
// REMINDER: study it, but it works so leave it for now as it is

data class User(
    val id: Int,
    val name: String,
    val email: String, // Stealing there data
    val createdAt: Long = 0L,
    var currentClass: FitnessClass = FitnessClass.ADVENTURER,
    var streakTier: StreakTier = StreakTier.PEASANT,
    var currentStreak: Int = 0,
    var weeklyWorkoutCount: Int = 0,    // resets every week
    var weekStartDate: Long = 0L,
    var lastWorkoutDate: Long = 0L,
    var lastClassSwitch: Long = 0L,
    var hasUsedFreeSwitch: Boolean = false, // true once the one-time free switch is consumed
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

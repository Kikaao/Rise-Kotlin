import models.*
import systems.*

// ============================================================
// Main.kt — FitRPG Systems Test Suite
// Run this to verify all systems behave correctly.
// All tests print results to console for manual inspection.
//
// Systems covered:
//   1. XpSystem        — strength (all 3 tracking types), cardio
//                        (distance + interval), all pace tiers,
//                        streak multipliers
//   2. LevelSystem     — XP curve, addXp, progress bar, milestone
//                        blocking, max level cap
//   3. StreakSystem    — log workout, double-count, weekly eval
//                        (goal met / freeze / broken), multiplier parity
//   4. StatSystem      — strength gains, cardio gains, focus from
//                        streak, focus from quest, stat cap, focus
//                        exclusion from exercises
//   5. ClassSwitchSystem — canSwitch (free / cooldown / bypass / same
//                        class), calculateHeadStart (new vs returning,
//                        cap), applySwitch (classesSwitchedTo, free
//                        switch consumed)
//   6. MuscleSystem    — recalculateAll (weight-based, reps-based,
//                        cardio-based), rank thresholds, HIP_FLEXORS
//                        exclusion, getRankedMusclesSorted,
//                        getUnrankedMuscles, progressToNextRank
//   7. AchievementSystem — first session, streak achievements, PR
//                        20% threshold, previousLastWorkoutDateMs fix
//                        (ComebackAfterInactivity)
//   8. QuestSystem     — counts per tier, HIT_STREAK target, expiry,
//                        first quest always COMMON
// ============================================================

fun main() {
    printHeader("FITRPG SYSTEMS TEST SUITE")

    testXpSystem()
    testLevelCurve()
    testStreakSystem()
    testStatSystem()
    testClassSwitchSystem()
    testMuscleSystem()
    testAchievements()
    testQuestGeneration()

    printHeader("ALL TESTS COMPLETE")
}


// ============================================================
// 1. XP SYSTEM TESTS
// ============================================================

fun testXpSystem() {
    printHeader("1. XP SYSTEM")

    val benchPress = ExerciseLibrary.barbellBenchPress  // id=1, REPS_WEIGHT, strength=0.8
    val pushUp     = ExerciseLibrary.standardPushUp     // id=11, REPS_ONLY, strength=0.5
    val deadHang   = ExerciseLibrary.deadHang           // id=36, TIME, strength=0.4/endurance=0.4
    val squat      = ExerciseLibrary.barbellBackSquat   // id=121, REPS_WEIGHT, strength=0.9

    // ── Scenario A: Light bench press, no streak ──────────
    val lightSet = WorkoutSet(id = 1, exerciseId = 1, setNumber = 1, reps = 8, weightKg = 60.0)
    val lightResult = XpSystem.calculateStrengthXP(
        exercise = benchPress, set = lightSet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 0
    )
    printResult("Bench Press 60kg×8 (Champion, no streak)", lightResult)

    // ── Scenario B: Heavy bench press — should beat light ─
    val heavySet = WorkoutSet(id = 2, exerciseId = 1, setNumber = 1, reps = 5, weightKg = 120.0)
    val heavyResult = XpSystem.calculateStrengthXP(
        exercise = benchPress, set = heavySet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 0
    )
    printResult("Bench Press 120kg×5 (Champion, no streak)", heavyResult)
    check("Heavy bench gives MORE XP than light bench") { heavyResult.finalXp > lightResult.finalXp }

    // ── Scenario C: Wrong class (Adventurer) — fewer XP ──
    val wrongClassResult = XpSystem.calculateStrengthXP(
        exercise = benchPress, set = heavySet,
        currentClass = FitnessClass.ADVENTURER, currentStreakDays = 0
    )
    printResult("Bench Press 120kg×5 (Adventurer, no streak)", wrongClassResult)
    check("Champion gets MORE XP than Adventurer on bench press") {
        heavyResult.finalXp > wrongClassResult.finalXp
    }

    // ── Scenario D: Paragon flat multiplier ──────────────
    val paragonResult = XpSystem.calculateStrengthXP(
        exercise = benchPress, set = heavySet,
        currentClass = FitnessClass.PARAGON, currentStreakDays = 0
    )
    printResult("Bench Press 120kg×5 (Paragon, no streak)", paragonResult)
    check("Paragon multiplier is exactly 1.15") { paragonResult.classMultiplier == 1.15 }

    // ── Scenario E: REPS_ONLY tracking (push-up) ─────────
    // bodyweight — no weight, so difficulty comes from rep count alone
    val pushUpSet = WorkoutSet(id = 3, exerciseId = 11, setNumber = 1, reps = 30)
    val pushUpResult = XpSystem.calculateStrengthXP(
        exercise = pushUp, set = pushUpSet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 0
    )
    printResult("Push-Up ×30 (Champion, no streak)", pushUpResult)
    // 30 reps / 20.0 scale = 1.5 raw → +1.0 = 2.0 → capped at DIFFICULTY_MAX = 2.0
    check("REPS_ONLY difficulty caps at 2.0 for 30 reps") { pushUpResult.difficultyMultiplier == 2.0 }
    check("REPS_ONLY base XP is correct (25)") { pushUpResult.baseXp == 25 }

    // ── Scenario F: TIME tracking (dead hang) ────────────
    // 120 seconds / 120.0 scale = 1.0 raw → +1.0 = 2.0 → exactly at cap
    val deadHangSet = WorkoutSet(id = 4, exerciseId = 36, setNumber = 1, durationSeconds = 120)
    val deadHangResult = XpSystem.calculateStrengthXP(
        exercise = deadHang, set = deadHangSet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 0
    )
    printResult("Dead Hang 120s (Champion, no streak)", deadHangResult)
    check("TIME 120s difficulty = 2.0 (exactly at cap)") { deadHangResult.difficultyMultiplier == 2.0 }

    // Short dead hang — below scale threshold
    val shortHangSet = WorkoutSet(id = 5, exerciseId = 36, setNumber = 1, durationSeconds = 30)
    val shortHangResult = XpSystem.calculateStrengthXP(
        exercise = deadHang, set = shortHangSet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 0
    )
    // 30s / 120.0 = 0.25 → +1.0 = 1.25
    check("TIME 30s difficulty = 1.25") {
        shortHangResult.difficultyMultiplier == 1.25
    }

    // ── Scenario G: Streak multipliers ───────────────────
    val streak4  = XpSystem.applyStreakMultiplier(4)
    val streak5  = XpSystem.applyStreakMultiplier(5)
    val streak10 = XpSystem.applyStreakMultiplier(10)
    val streak30 = XpSystem.applyStreakMultiplier(30)
    println("\n  Streak multipliers:")
    println("    4 days  → ×$streak4  (should be 1.0)")
    println("    5 days  → ×$streak5  (should be 1.05)")
    println("    10 days → ×$streak10 (should be 1.1)")
    println("    30 days → ×$streak30 (should be 1.3)")
    check("No bonus below 5 days")   { streak4  == 1.0  }
    check("5-day streak = ×1.05")    { streak5  == 1.05 }
    check("10-day streak = ×1.10")   { streak10 == 1.10 }
    check("30-day streak = ×1.30")   { streak30 == 1.30 }

    // ── Scenario H: Squat, Champion, 30-day streak ───────
    val squatSet = WorkoutSet(id = 6, exerciseId = 121, setNumber = 1, reps = 5, weightKg = 140.0)
    val squatResult = XpSystem.calculateStrengthXP(
        exercise = squat, set = squatSet,
        currentClass = FitnessClass.CHAMPION, currentStreakDays = 30
    )
    printResult("Squat 140kg×5 (Champion, 30-day streak)", squatResult)
    check("Streak multiplier applied to squat") { squatResult.streakMultiplier == 1.3 }

    // ── Scenario I: Cardio — DISTANCE_BASED, all pace tiers
    val running = CardioExerciseLibrary.outdoorRunning

    // FAST pace — 10km in 50 min = 5.0 min/km (4–6 min/km bracket)
    val fastSession = CardioSession(
        id = 1, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = 50.0
    )
    val fastResult = XpSystem.calculateCardioXP(running, fastSession, currentStreakDays = 10)
    println("\n  10km run in 50min (5.0 min/km → FAST, 10-day streak):")
    println("    Base XP: ${fastResult.baseXp}  Pace: ×${fastResult.paceTierMultiplier}  Streak: ×${fastResult.streakMultiplier}  Final: ${fastResult.finalXp}")
    check("5.0 min/km → FAST tier (×1.2)")  { fastResult.paceTierMultiplier == 1.2 }
    check("10-day streak multiplier on cardio") { fastResult.streakMultiplier == 1.1 }

    // ELITE pace — 10km in 35 min = 3.5 min/km (< 4.0 min/km)
    val eliteSession = CardioSession(
        id = 2, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = 35.0
    )
    val eliteResult = XpSystem.calculateCardioXP(running, eliteSession, currentStreakDays = 0)
    println("  10km run in 35min (3.5 min/km → ELITE, no streak):")
    println("    Pace: ×${eliteResult.paceTierMultiplier}  Final: ${eliteResult.finalXp}")
    check("3.5 min/km → ELITE tier (×1.5)") { eliteResult.paceTierMultiplier == 1.5 }

    // INTERMEDIATE pace — 10km in 70 min = 7.0 min/km (6–8 min/km bracket)
    val intermediateSession = CardioSession(
        id = 3, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = 70.0
    )
    val intermediateResult = XpSystem.calculateCardioXP(running, intermediateSession, currentStreakDays = 0)
    println("  10km run in 70min (7.0 min/km → INTERMEDIATE, no streak):")
    println("    Pace: ×${intermediateResult.paceTierMultiplier}  Final: ${intermediateResult.finalXp}")
    check("7.0 min/km → INTERMEDIATE tier (×1.0)") { intermediateResult.paceTierMultiplier == 1.0 }

    // SLOW pace — 10km in 100 min = 10.0 min/km (> 8 min/km)
    val slowSession = CardioSession(
        id = 4, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = 100.0
    )
    val slowResult = XpSystem.calculateCardioXP(running, slowSession, currentStreakDays = 0)
    println("  10km run in 100min (10.0 min/km → SLOW, no streak):")
    println("    Pace: ×${slowResult.paceTierMultiplier}  Final: ${slowResult.finalXp}")
    check("10.0 min/km → SLOW tier (×0.8)") { slowResult.paceTierMultiplier == 0.8 }

    // No time logged → no pace bonus, base rate only
    val noTimeSession = CardioSession(
        id = 5, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = null
    )
    val noTimeResult = XpSystem.calculateCardioXP(running, noTimeSession, currentStreakDays = 0)
    println("  10km run, no time logged (no pace bonus):")
    println("    Pace: ×${noTimeResult.paceTierMultiplier}  (should be 1.0)")
    check("No time logged → pace multiplier = 1.0") { noTimeResult.paceTierMultiplier == 1.0 }

    // Anti-gaming: unrealistically fast pace (< 2.5 min/km cap) → ignored
    val cheatSession = CardioSession(
        id = 6, userId = 1, cardioExerciseId = 221, date = System.currentTimeMillis(),
        distanceKm = 10.0, durationMinutes = 10.0 // 1.0 min/km — physically impossible
    )
    val cheatResult = XpSystem.calculateCardioXP(running, cheatSession, currentStreakDays = 0)
    println("  10km run in 10min (1.0 min/km → anti-gaming guard):")
    println("    Pace: ×${cheatResult.paceTierMultiplier}  (should be 1.0 — ignored)")
    check("Unrealistic pace → multiplier ignored (1.0)") { cheatResult.paceTierMultiplier == 1.0 }

    // ── Scenario J: Cardio — INTERVAL_BASED (jump rope) ──
    val jumpRope = CardioExerciseLibrary.jumpRopeCardio // baseXpPer10Meters = 3.0
    // 10 sets × 100 meters = 1000 total meters → (1000/10) × 3.0 = 300 base XP
    val jumpRopeSession = CardioSession(
        id = 7, userId = 1, cardioExerciseId = 231, date = System.currentTimeMillis(),
        sets = 10, metersPerSet = 100
    )
    val jumpRopeResult = XpSystem.calculateCardioXP(jumpRope, jumpRopeSession, currentStreakDays = 0)
    println("\n  Jump Rope: 10 sets × 100m (1000m total, no streak):")
    println("    Base XP: ${jumpRopeResult.baseXp}  (expected 300)")
    println("    Pace mult: ×${jumpRopeResult.paceTierMultiplier}  (should be 1.0 — intervals have no pace tier)")
    println("    Final XP: ${jumpRopeResult.finalXp}")
    check("Jump rope base XP = 300 (1000m / 10 × 3.0)") { jumpRopeResult.baseXp == 300 }
    check("Interval exercises always have paceTierMultiplier = 1.0") {
        jumpRopeResult.paceTierMultiplier == 1.0
    }

    // Sprinting — higher base rate than jump rope
    val sprinting = CardioExerciseLibrary.sprintingCardio // baseXpPer10Meters = 6.0
    // 8 sets × 50 meters = 400 total meters → (400/10) × 6.0 = 240 base XP
    val sprintSession = CardioSession(
        id = 8, userId = 1, cardioExerciseId = 232, date = System.currentTimeMillis(),
        sets = 8, metersPerSet = 50
    )
    val sprintResult = XpSystem.calculateCardioXP(sprinting, sprintSession, currentStreakDays = 5)
    println("  Sprinting: 8 sets × 50m (400m total, 5-day streak):")
    println("    Base XP: ${sprintResult.baseXp}  (expected 240)")
    println("    Streak: ×${sprintResult.streakMultiplier}  Final: ${sprintResult.finalXp}")
    check("Sprint base XP = 240 (400m / 10 × 6.0)") { sprintResult.baseXp == 240 }
    check("5-day streak applies to interval cardio") { sprintResult.streakMultiplier == 1.05 }
}


// ============================================================
// 2. LEVEL CURVE TEST
// ============================================================

fun testLevelCurve() {
    printHeader("2. LEVEL CURVE")

    val keyLevels = listOf(1, 2, 5, 10, 20, 21, 25, 40, 50, 60, 61, 75, 100)

    println("  XP cost per level step:")
    keyLevels.forEach { level ->
        val cost = LevelSystem.getXpForLevel(level)
        println("    Level ${level.toString().padStart(3)}: ${cost.toString().padStart(7)} XP")
    }

    println("\n  Cumulative XP to reach level:")
    keyLevels.forEach { level ->
        val total = LevelSystem.getTotalXpForLevel(level)
        println("    Level ${level.toString().padStart(3)}: ${total.toString().padStart(10)} XP total")
    }

    // Band boundary check — level 20 cost should equal level 21 cost
    // (both BAND_1 and BAND_2 are 150 — flat until band 3)
    val cost20 = LevelSystem.getXpForLevel(20)
    val cost21 = LevelSystem.getXpForLevel(21)
    val cost61 = LevelSystem.getXpForLevel(61)
    println("\n  Band boundary checks:")
    println("    Level 20 cost: $cost20  Level 21 cost: $cost21  (currently equal — two-band)")
    println("    Level 60 cost: ${LevelSystem.getXpForLevel(60)}  Level 61 cost: $cost61  (band 3 jump)")
    check("Band 3 (level 61) costs more than band 2 (level 60)") {
        LevelSystem.getXpForLevel(61) > LevelSystem.getXpForLevel(60)
    }

    // Sessions estimate
    val avgXpPerSession = 1400
    println("\n  Estimated sessions to reach level (~$avgXpPerSession XP/session):")
    listOf(10, 25, 50, 100).forEach { level ->
        val sessions = LevelSystem.getTotalXpForLevel(level) / avgXpPerSession
        println("    Level $level: ~$sessions sessions")
    }

    // addXp — multi-level-up on fresh character
    val freshProgress = ClassProgress(fitnessClass = FitnessClass.CHAMPION)
    val bigXpResult = LevelSystem.addXp(
        progress = freshProgress, fitnessClass = FitnessClass.CHAMPION,
        xpToAdd = 5000, personalRecords = emptyList()
    )
    println("\n  Adding 5000 XP to fresh Level 0 Champion:")
    println("    Levels gained: ${bigXpResult.levelsGained}")
    println("    Final level:   ${bigXpResult.updatedProgress.level}")
    println("    Remaining XP:  ${bigXpResult.updatedProgress.xp}")
    println("    Total XP:      ${bigXpResult.updatedProgress.totalXp}")
    check("totalXp equals xpToAdd — never drops") { bigXpResult.updatedProgress.totalXp == 5000 }
    check("At least one level gained from 5000 XP") { bigXpResult.levelsGained.isNotEmpty() }

    // Progress bar
    val progressBar = LevelSystem.getProgressToNextLevel(bigXpResult.updatedProgress)
    println("    Progress to next level: ${(progressBar * 100).toInt()}%")
    check("Progress bar is between 0 and 1") { progressBar in 0.0..1.0 }

    // Milestone blocking — hard gate at level 50 with a null exerciseId passes through
    // (null = not yet defined → always passes). Verify that a real requirement blocks.
    // We can't trigger a hard block through addXp with null exerciseIds (they pass through),
    // so we call attemptLevelUp by getting a character to level 49 and verifying it
    // levels up past 50 fine (milestone exerciseId is null → gate passes).
    val level49Progress = ClassProgress(
        fitnessClass = FitnessClass.CHAMPION,
        level = 49,
        xp = LevelSystem.getXpForLevel(50) + 100, // more than enough for level 50
        totalXp = 100_000
    )
    val milestone50Result = LevelSystem.addXp(
        progress = level49Progress, fitnessClass = FitnessClass.CHAMPION,
        xpToAdd = 0, personalRecords = emptyList()
    )
    println("\n  Hard gate at level 50 (exerciseId = null → passes through):")
    println("    Blocked by milestone: ${milestone50Result.blockedByMilestone != null}")
    check("Null milestone passes through (not yet defined → always allow)") {
        milestone50Result.blockedByMilestone == null
    }

    // Max level cap — adding XP at level 100 should not increase level
    val maxProgress = ClassProgress(
        fitnessClass = FitnessClass.CHAMPION, level = 100, xp = 0, totalXp = 999_999
    )
    val maxResult = LevelSystem.addXp(
        progress = maxProgress, fitnessClass = FitnessClass.CHAMPION,
        xpToAdd = 100_000, personalRecords = emptyList()
    )
    println("\n  Adding 100,000 XP to Level 100 character:")
    println("    Final level: ${maxResult.updatedProgress.level}  (should stay 100)")
    println("    Total XP:    ${maxResult.updatedProgress.totalXp}  (should increase)")
    check("Level stays at 100 after adding XP at max level") {
        maxResult.updatedProgress.level == 100
    }
    check("totalXp still increases at max level") {
        maxResult.updatedProgress.totalXp == maxProgress.totalXp + 100_000
    }
    check("No levels gained list at max level") { maxResult.levelsGained.isEmpty() }
}


// ============================================================
// 3. STREAK SYSTEM TESTS
// ============================================================

fun testStreakSystem() {
    printHeader("3. STREAK SYSTEM")

    val now    = System.currentTimeMillis()
    val dayMs  = 24L * 60 * 60 * 1000
    val weekMs = 7 * dayMs

    val baseUser = User(
        id = 1, name = "Stamatis", email = "s@test.com",
        streakTier = StreakTier.KNIGHT,
        weekStartDate = now - weekMs - 1000,
        lastWorkoutDate = now - dayMs * 2
    )

    // ── Log workout today ──────────────────────────────────
    val logResult = StreakSystem.onWorkoutLogged(baseUser, now)
    println("  Logging workout today:")
    when (logResult) {
        is StreakSystem.WorkoutLoggedResult.StreakIncremented ->
            println("    Streak incremented → ${logResult.newStreakDays} days ✓")
        is StreakSystem.WorkoutLoggedResult.AlreadyLoggedToday ->
            println("    Already logged today — no double count ✓")
    }

    // ── Same day double-count guard ────────────────────────
    val updatedUser = (logResult as? StreakSystem.WorkoutLoggedResult.StreakIncremented)?.updatedUser ?: baseUser
    val secondLog   = StreakSystem.onWorkoutLogged(updatedUser, now)
    check("Second log same day does not increment streak") {
        secondLog is StreakSystem.WorkoutLoggedResult.AlreadyLoggedToday
    }
    println("  Logging again same day: no double-count ✓")

    // ── Weekly eval — goal met (KNIGHT = 3 min) ────────────
    val userGoalMet  = baseUser.copy(weeklyWorkoutCount = 4)
    val weekResult   = StreakSystem.evaluateWeek(userGoalMet, now)
    println("\n  Weekly evaluation (KNIGHT, logged 4 — goal is 3):")
    when (weekResult) {
        is StreakSystem.WeeklyEvaluationResult.GoalMet ->
            println("    Goal met ✓ (logged ${weekResult.workoutsLogged}, required ${weekResult.minimumRequired})")
        else -> println("    Unexpected result: $weekResult")
    }
    check("Goal met when workouts >= minimum") { weekResult is StreakSystem.WeeklyEvaluationResult.GoalMet }

    // ── Weekly eval — goal missed, freeze consumed ─────────
    val freezeItem    = InventoryItem(ItemType.STREAK_FREEZE, quantity = 2)
    val userWithFreeze = baseUser.copy(
        weeklyWorkoutCount = 1,
        inventory = mutableMapOf(ItemType.STREAK_FREEZE to freezeItem)
    )
    val freezeResult = StreakSystem.evaluateWeek(userWithFreeze, now)
    println("\n  Weekly evaluation (KNIGHT, logged 1, has 2 freezes):")
    when (freezeResult) {
        is StreakSystem.WeeklyEvaluationResult.FreezeConsumed -> {
            println("    Freeze consumed ✓ (${freezeResult.freezesRemaining} remaining)")
            check("One freeze consumed, one remaining") { freezeResult.freezesRemaining == 1 }
        }
        else -> println("    Unexpected result: $freezeResult")
    }

    // ── Weekly eval — goal missed, no freeze ───────────────
    val userNoFreeze = baseUser.copy(weeklyWorkoutCount = 0)
    val brokenResult = StreakSystem.evaluateWeek(userNoFreeze, now)
    println("\n  Weekly evaluation (KNIGHT, logged 0, no freeze):")
    when (brokenResult) {
        is StreakSystem.WeeklyEvaluationResult.StreakBroken -> {
            println("    Streak broken ✓")
            check("Streak resets to 0") { brokenResult.updatedUser.currentStreak == 0 }
        }
        else -> println("    Unexpected result: $brokenResult")
    }

    // ── Multiplier parity with XpSystem ───────────────────
    println("\n  Streak multipliers from StreakSystem:")
    println("    4 days:  ×${StreakSystem.getCurrentMultiplier(4)}  (should be 1.0)")
    println("    5 days:  ×${StreakSystem.getCurrentMultiplier(5)}  (should be 1.05)")
    println("    30 days: ×${StreakSystem.getCurrentMultiplier(30)} (should be 1.3)")
    check("StreakSystem and XpSystem return identical multipliers") {
        StreakSystem.getCurrentMultiplier(30) == XpSystem.applyStreakMultiplier(30)
    }
}


// ============================================================
// 4. STAT SYSTEM TESTS
// ============================================================

fun testStatSystem() {
    printHeader("4. STAT SYSTEM")

    val emptyStats = Stats()
    val benchPress = ExerciseLibrary.barbellBenchPress // strength=0.8, power=0.2
    val running    = CardioExerciseLibrary.outdoorRunning // endurance=0.7, dexterity=0.2, power=0.1

    // ── Strength stat gains from bench press ──────────────
    // 100 XP × 0.8 strength = 80, × 0.2 power = 20, others = 0
    val strengthResult = StatSystem.applyStrengthStatGains(
        currentStats = emptyStats, exercise = benchPress, finalXp = 100
    )
    println("  Bench press 100 XP stat gains:")
    println("    Strength: +${strengthResult.strengthGained}  (expected 80)")
    println("    Power:    +${strengthResult.powerGained}     (expected 20)")
    println("    Others:    ${strengthResult.dexterityGained}/${strengthResult.enduranceGained}/${strengthResult.flexibilityGained} (expected 0)")
    println("    Focus:     ${strengthResult.focusGained}     (expected 0 — never from exercises)")
    check("Bench press gives 80 strength from 100 XP") { strengthResult.strengthGained == 80 }
    check("Bench press gives 20 power from 100 XP")    { strengthResult.powerGained == 20 }
    check("Bench press gives 0 dexterity")             { strengthResult.dexterityGained == 0 }
    check("Focus never gained from exercises")         { strengthResult.focusGained == 0 }

    // ── Cardio stat gains from running ────────────────────
    // 200 XP × 0.7 endurance = 140, × 0.2 dex = 40, × 0.1 power = 20
    val cardioResult = StatSystem.applyCardioStatGains(
        currentStats = emptyStats, cardioExercise = running, finalXp = 200
    )
    println("\n  Running 200 XP stat gains:")
    println("    Endurance:  +${cardioResult.enduranceGained}  (expected 140)")
    println("    Dexterity:  +${cardioResult.dexterityGained}  (expected 40)")
    println("    Power:      +${cardioResult.powerGained}      (expected 20)")
    println("    Focus:       ${cardioResult.focusGained}      (expected 0)")
    check("Running gives 140 endurance from 200 XP") { cardioResult.enduranceGained == 140 }
    check("Running gives 40 dexterity from 200 XP")  { cardioResult.dexterityGained == 40 }
    check("Running gives 20 power from 200 XP")      { cardioResult.powerGained == 20 }
    check("Cardio gives 0 focus from exercises")     { cardioResult.focusGained == 0 }

    // ── Stat cap — strength starts at 990, 100 XP × 0.8 = 80 → capped at 10
    val nearCapStats = Stats(strength = 990, power = 999)
    val capResult = StatSystem.applyStrengthStatGains(
        currentStats = nearCapStats, exercise = benchPress, finalXp = 100
    )
    println("\n  Bench press 100 XP with strength=990, power=999:")
    println("    Strength gained: ${capResult.strengthGained}  (expected 10 — capped at 1000)")
    println("    Power gained:    ${capResult.powerGained}     (expected 1 — capped at 1000)")
    check("Strength gain capped at MAX_STAT_VALUE (1000)") { capResult.statsAfter.strength == 1000 }
    check("Power gain capped at MAX_STAT_VALUE (1000)")    { capResult.statsAfter.power == 1000 }

    // ── Focus from streak ─────────────────────────────────
    // 10 streak days × 10 focus/day = 100 focus
    val focusFromStreakResult = StatSystem.addFocusFromStreak(
        currentStats = emptyStats, streakDays = 10
    )
    println("\n  Focus from 10-day streak:")
    println("    Focus gained: ${focusFromStreakResult.focusGained}  (expected 100)")
    check("Focus from streak = streakDays × 10") { focusFromStreakResult.focusGained == 100 }
    check("Only focus changes from streak")       { focusFromStreakResult.strengthGained == 0 }

    // ── Focus from quest completion ───────────────────────
    val focusFromQuestResult = StatSystem.addFocusFromQuestCompletion(currentStats = emptyStats)
    println("\n  Focus from quest completion:")
    println("    Focus gained: ${focusFromQuestResult.focusGained}  (expected 50)")
    check("Focus from quest = 50") { focusFromQuestResult.focusGained == 50 }
    check("Only focus changes from quest") { focusFromQuestResult.strengthGained == 0 }

    // ── Focus cap via streak ──────────────────────────────
    val nearFocusCap = Stats(focus = 995)
    val focusCapResult = StatSystem.addFocusFromStreak(
        currentStats = nearFocusCap, streakDays = 10
    )
    println("\n  Focus from streak with focus=995:")
    println("    Focus gained: ${focusCapResult.focusGained}  (expected 5 — capped at 1000)")
    check("Focus capped at 1000 via streak") { focusCapResult.statsAfter.focus == 1000 }

    // ── statsAfter correctly reflects updated values ───────
    check("statsAfter.strength equals statsBefore.strength + gained") {
        strengthResult.statsAfter.strength == strengthResult.statsBefore.strength + strengthResult.strengthGained
    }
}


// ============================================================
// 5. CLASS SWITCH SYSTEM TESTS
// ============================================================

fun testClassSwitchSystem() {
    printHeader("5. CLASS SWITCH SYSTEM")

    val now    = System.currentTimeMillis()
    val dayMs  = 24L * 60 * 60 * 1000

    // ── Fresh user — free switch available ────────────────
    val freshUser = User(
        id = 1, name = "Stamatis", email = "s@test.com",
        currentClass = FitnessClass.ADVENTURER,
        hasUsedFreeSwitch = false
    )
    val freeSwitchResult = ClassSwitchSystem.canSwitch(freshUser, FitnessClass.CHAMPION, now)
    println("  Fresh user switching to CHAMPION (free switch available):")
    when (freeSwitchResult) {
        is ClassSwitchSystem.CanSwitchResult.Allowed ->
            println("    Allowed (${freeSwitchResult.reason}) ✓")
        else -> println("    Unexpected: $freeSwitchResult")
    }
    check("Fresh user gets FREE_SWITCH") {
        freeSwitchResult is ClassSwitchSystem.CanSwitchResult.Allowed &&
                (freeSwitchResult as ClassSwitchSystem.CanSwitchResult.Allowed).reason == ClassSwitchSystem.SwitchAllowedReason.FREE_SWITCH
    }

    // ── Already on this class ──────────────────────────────
    val sameClassResult = ClassSwitchSystem.canSwitch(freshUser, FitnessClass.ADVENTURER, now)
    check("Switching to current class returns AlreadyOnThisClass") {
        sameClassResult is ClassSwitchSystem.CanSwitchResult.AlreadyOnThisClass
    }
    println("  Switching to ADVENTURER while on ADVENTURER: AlreadyOnThisClass ✓")

    // ── Cooldown still active — no item ───────────────────
    val usedSwitchUser = freshUser.copy(
        hasUsedFreeSwitch = true,
        lastClassSwitch = now - 10 * dayMs  // switched 10 days ago, cooldown is 60 days
    )
    val blockedResult = ClassSwitchSystem.canSwitch(usedSwitchUser, FitnessClass.CHAMPION, now)
    println("\n  User switched 10 days ago (60-day cooldown, no item):")
    when (blockedResult) {
        is ClassSwitchSystem.CanSwitchResult.Blocked -> {
            val daysRemaining = blockedResult.msRemaining / dayMs
            println("    Blocked ✓ (~$daysRemaining days remaining)")
            check("~50 days remaining on cooldown") { daysRemaining in 49..51 }
        }
        else -> println("    Unexpected: $blockedResult")
    }

    // ── Cooldown passed ────────────────────────────────────
    val cooldownPassedUser = freshUser.copy(
        hasUsedFreeSwitch = true,
        lastClassSwitch = now - 70 * dayMs  // 70 days ago — past 60-day cooldown
    )
    val cooldownPassedResult = ClassSwitchSystem.canSwitch(cooldownPassedUser, FitnessClass.CHAMPION, now)
    check("Cooldown passed → COOLDOWN_PASSED") {
        cooldownPassedResult is ClassSwitchSystem.CanSwitchResult.Allowed &&
                (cooldownPassedResult as ClassSwitchSystem.CanSwitchResult.Allowed).reason == ClassSwitchSystem.SwitchAllowedReason.COOLDOWN_PASSED
    }
    println("  User switched 70 days ago: COOLDOWN_PASSED ✓")

    // ── Bypass item — cooldown active but item present ────
    val bypassResult = ClassSwitchSystem.canSwitch(
        user = usedSwitchUser,
        targetClass = FitnessClass.CHAMPION,
        nowMs = now,
        bypassCooldown = true
    )
    check("Item bypass → BYPASS_ITEM when cooldown active") {
        bypassResult is ClassSwitchSystem.CanSwitchResult.Allowed &&
                (bypassResult as ClassSwitchSystem.CanSwitchResult.Allowed).reason == ClassSwitchSystem.SwitchAllowedReason.BYPASS_ITEM
    }
    println("  Item bypass during active cooldown: BYPASS_ITEM ✓")

    // Critical fix check: free switch must be consumed BEFORE bypass item.
    // A fresh user with bypassCooldown=true should still get FREE_SWITCH, not BYPASS_ITEM.
    val freeBeforeBypassResult = ClassSwitchSystem.canSwitch(
        user = freshUser, // has free switch
        targetClass = FitnessClass.CHAMPION,
        nowMs = now,
        bypassCooldown = true
    )
    check("Free switch consumed before bypass item (fix CS1)") {
        freeBeforeBypassResult is ClassSwitchSystem.CanSwitchResult.Allowed &&
                (freeBeforeBypassResult as ClassSwitchSystem.CanSwitchResult.Allowed).reason == ClassSwitchSystem.SwitchAllowedReason.FREE_SWITCH
    }
    println("  Free switch takes priority over bypass item (fix CS1) ✓")

    // ── Head start — new class ────────────────────────────
    // Champion: primary=strength(0.8), secondary=endurance+power(0.4 each)
    // stats: strength=500, endurance=300, power=200
    // points = 500×0.8 + 300×0.4 + 200×0.4 = 400 + 120 + 80 = 600
    // startingLevel = (600 / 70).toInt() = 8
    val statsForHeadStart = Stats(strength = 500, endurance = 300, power = 200)
    val userForHeadStart = freshUser.copy(
        stats = statsForHeadStart,
        classMap = FitnessClass.entries.associateWith { ClassProgress(it) }.toMutableMap()
    )
    val headStart = ClassSwitchSystem.calculateHeadStart(userForHeadStart, FitnessClass.CHAMPION)
    println("\n  Head start for CHAMPION (strength=500, endurance=300, power=200):")
    println("    Points: ${headStart.headStartPoints}  (expected 600.0)")
    println("    Starting level: ${headStart.startingLevel}  (expected 8)")
    println("    Is returning: ${headStart.isReturning}  (expected false)")
    println("    Cap applied: ${headStart.wasCapApplied}  (expected false)")
    check("Head start points = 600.0") { headStart.headStartPoints == 600.0 }
    check("Head start level = 8")      { headStart.startingLevel == 8 }
    check("isReturning = false for new class") { !headStart.isReturning }

    // ── Head start cap at HEAD_START_MAX_LEVEL (30) ───────
    // Massively high stats to force rawLevel > 30
    val bigStats = Stats(strength = 1000, endurance = 1000, power = 1000)
    val userBigStats = freshUser.copy(
        stats = bigStats,
        classMap = FitnessClass.entries.associateWith { ClassProgress(it) }.toMutableMap()
    )
    val cappedHeadStart = ClassSwitchSystem.calculateHeadStart(userBigStats, FitnessClass.CHAMPION)
    println("\n  Head start cap with maxed strength stats:")
    println("    Raw level: would be huge  Capped level: ${cappedHeadStart.startingLevel}  (expected 30)")
    println("    Cap applied: ${cappedHeadStart.wasCapApplied}  (expected true)")
    check("Head start capped at 30") { cappedHeadStart.startingLevel == 30 }
    check("wasCapApplied = true when raw > 30") { cappedHeadStart.wasCapApplied }

    // ── Returning player — keeps existing progress ─────────
    val existingChampionProgress = ClassProgress(
        fitnessClass = FitnessClass.CHAMPION, level = 15, xp = 500, totalXp = 10_000
    )
    val returningUser = freshUser.copy(
        classMap = mutableMapOf(FitnessClass.CHAMPION to existingChampionProgress)
    )
    val returningHeadStart = ClassSwitchSystem.calculateHeadStart(returningUser, FitnessClass.CHAMPION)
    println("\n  Returning to CHAMPION (was level 15, totalXp=10000):")
    println("    isReturning: ${returningHeadStart.isReturning}  (expected true)")
    println("    Starting level: ${returningHeadStart.startingLevel}  (expected 15)")
    check("isReturning = true when totalXp > 0") { returningHeadStart.isReturning }
    check("Returning player resumes at their last level (15)") {
        returningHeadStart.startingLevel == 15
    }

    // ── applySwitch — classesSwitchedTo is updated ────────
    val switchResult = ClassSwitchSystem.applySwitch(
        user = freshUser, targetClass = FitnessClass.CHAMPION,
        allowedReason = ClassSwitchSystem.SwitchAllowedReason.FREE_SWITCH,
        nowMs = now
    )
    println("\n  Applying class switch (free switch) ADVENTURER → CHAMPION:")
    println("    New class: ${switchResult.updatedUser.currentClass}  (expected CHAMPION)")
    println("    hasUsedFreeSwitch: ${switchResult.updatedUser.hasUsedFreeSwitch}  (expected true)")
    println("    classesSwitchedTo: ${switchResult.updatedUser.classesSwitchedTo}")
    check("currentClass updated to CHAMPION")  { switchResult.updatedUser.currentClass == FitnessClass.CHAMPION }
    check("hasUsedFreeSwitch = true after free switch") { switchResult.updatedUser.hasUsedFreeSwitch }
    check("CHAMPION added to classesSwitchedTo") {
        FitnessClass.CHAMPION in switchResult.updatedUser.classesSwitchedTo
    }

    // ── Paragon head start formula ────────────────────────
    // All 6 stats at 400: (400×6) × 0.27 = 2400 × 0.27 = 648
    // startingLevel = (648 / 70).toInt() = 9
    val balancedStats = Stats(
        strength = 400, dexterity = 400, endurance = 400,
        flexibility = 400, power = 400, focus = 400
    )
    val paragonUser = freshUser.copy(
        stats = balancedStats,
        classMap = FitnessClass.entries.associateWith { ClassProgress(it) }.toMutableMap()
    )
    val paragonHeadStart = ClassSwitchSystem.calculateHeadStart(paragonUser, FitnessClass.PARAGON)
    println("\n  Paragon head start (all stats=400):")
    println("    Points: ${paragonHeadStart.headStartPoints}  (expected 648.0)")
    println("    Starting level: ${paragonHeadStart.startingLevel}  (expected 9)")
    check("Paragon head start points = 648.0") { paragonHeadStart.headStartPoints == 648.0 }
    check("Paragon head start level = 9")       { paragonHeadStart.startingLevel == 9 }
}


// ============================================================
// 6. MUSCLE SYSTEM TESTS
// ============================================================

fun testMuscleSystem() {
    printHeader("6. MUSCLE SYSTEM")

    val now = System.currentTimeMillis()

    // Profile: male, 25 years old, 80kg
    // Combined coefficient = 1.0 (MALE) × 1.0 (TWENTIES) = 1.0
    // This makes expected scores exactly: liftedKg / (80 × 1.0) = liftedKg / 80
    val profile = PlayerProfile(
        userId = 1, gender = Gender.MALE, ageYears = 25,
        heightCm = 180.0, weightKg = 80.0
    )

    val benchPress = ExerciseLibrary.barbellBenchPress // id=1, CHEST, REPS_WEIGHT
    val pushUp     = ExerciseLibrary.standardPushUp    // id=11, CHEST, REPS_ONLY

    // PR: bench press 120kg → chest score = 120 / (80 × 1.0) = 1.5 → rank A
    val benchPR = PersonalRecord(
        id = 1, userId = 1, exerciseId = 1,
        date = now, bestWeightKg = 120.0, bestReps = 5
    )
    // PR: push-up 50 reps → chest score (reps-only) = 50 / (80 × 1.0) = 0.625 → rank C
    // Bench press (1.5) > push-up (0.625) → bench should win for chest
    val pushUpPR = PersonalRecord(
        id = 2, userId = 1, exerciseId = 11,
        date = now, bestReps = 50
    )

    val allExercises = ExerciseLibrary.all
    val result = MuscleSystem.recalculateAll(
        profile = profile,
        allPRs = listOf(benchPR, pushUpPR),
        allExercises = allExercises,
        bestCardioSessions = emptyMap(),
        allCardioExercises = CardioExerciseLibrary.all,
        existingScores = emptyList(),
        nowMs = now
    )

    val chestScore = result.updatedScores.find { it.muscleGroup == MuscleGroup.CHEST }
    println("  Bench PR 120kg (80kg bodyweight, male, 25):")
    println("    Chest raw score: ${chestScore?.rawScore}  (expected 1.5)")
    println("    Chest rank:      ${chestScore?.rank}      (expected A)")
    println("    Scoring method:  ${chestScore?.scoringMethod}  (expected WEIGHT_BASED — bench wins over push-up)")
    check("Chest raw score = 1.5 (120/80)") { chestScore?.rawScore == 1.5 }
    check("Chest rank = A (threshold 1.5)") { chestScore?.rank == MuscleRank.A }
    check("Weight-based PR beats reps-only PR for same muscle") {
        chestScore?.scoringMethod == MuscleScoringMethod.WEIGHT_BASED
    }

    // ── Rank thresholds — explicit boundary checks ─────────
    // score 0.0 = F, 0.5 = D, 0.75 = C, 1.0 = B, 1.5 = A, 2.0 = S
    println("\n  Rank boundary checks (80kg bodyweight, male, 25):")
    data class RankCase(val liftKg: Double, val expectedRank: MuscleRank, val label: String)
    val rankCases = listOf(
        RankCase(0.0,   MuscleRank.F, "0kg   → F (no data)"),
        RankCase(39.0,  MuscleRank.F, "39kg  → F (score 0.4875, below D threshold 0.5)"),
        RankCase(40.0,  MuscleRank.D, "40kg  → D (score 0.5, exactly D threshold)"),
        RankCase(60.0,  MuscleRank.C, "60kg  → C (score 0.75, exactly C threshold)"),
        RankCase(80.0,  MuscleRank.B, "80kg  → B (score 1.0, bodyweight = B)"),
        RankCase(120.0, MuscleRank.A, "120kg → A (score 1.5, 1.5× bodyweight)"),
        RankCase(160.0, MuscleRank.S, "160kg → S (score 2.0, 2× bodyweight)")
    )
    for (case in rankCases) {
        val pr = PersonalRecord(id = 99, userId = 1, exerciseId = 1, date = now, bestWeightKg = case.liftKg, bestReps = 1)
        val prs = if (case.liftKg > 0.0) listOf(pr) else emptyList()
        val r = MuscleSystem.recalculateAll(profile, prs, allExercises, emptyMap(), CardioExerciseLibrary.all, emptyList(), now)
        val chest = r.updatedScores.find { it.muscleGroup == MuscleGroup.CHEST }
        val passed = chest?.rank == case.expectedRank
        println("    [${if (passed) "✓" else "✗ FAIL"}] ${case.label}")
    }

    // ── HIP_FLEXORS excluded from output ──────────────────
    val hipFlexorsInOutput = result.updatedScores.any { it.muscleGroup == MuscleGroup.HIP_FLEXORS }
    check("HIP_FLEXORS excluded from muscle scores") { !hipFlexorsInOutput }
    println("\n  HIP_FLEXORS excluded from output ✓")

    // ── Cardio-based scoring for QUADS via running ─────────
    // 15km run for a male 25yo (coeff 1.0): 15 / (12.0 × 1.0) = 1.25 → rank B
    val bestRunSession = CardioSession(
        id = 1, userId = 1, cardioExerciseId = 221, date = now, distanceKm = 15.0
    )
    val cardioResult = MuscleSystem.recalculateAll(
        profile = profile,
        allPRs = emptyList(),
        allExercises = allExercises,
        bestCardioSessions = mapOf(221 to bestRunSession),
        allCardioExercises = CardioExerciseLibrary.all,
        existingScores = emptyList(),
        nowMs = now
    )
    val quadsScore = cardioResult.updatedScores.find { it.muscleGroup == MuscleGroup.QUADS }
    println("\n  Cardio scoring — 15km run (male, 25, 80kg):")
    println("    Quads raw score: ${quadsScore?.rawScore}  (expected 1.25)")
    println("    Quads rank:      ${quadsScore?.rank}      (expected B)")
    println("    Scoring method:  ${quadsScore?.scoringMethod}  (expected CARDIO_BASED)")
    check("Quads raw score = 1.25 (15km / (12 × 1.0))") { quadsScore?.rawScore == 1.25 }
    check("Quads rank = B at score 1.25")    { quadsScore?.rank == MuscleRank.B }
    check("Scoring method = CARDIO_BASED")   { quadsScore?.scoringMethod == MuscleScoringMethod.CARDIO_BASED }

    // ── Gender coefficient — female gets higher score same lift ─
    val femaleProfile = PlayerProfile(
        userId = 2, gender = Gender.FEMALE, ageYears = 25,
        heightCm = 165.0, weightKg = 60.0
    )
    // Female coeff = 0.75, so denominator = 60 × 0.75 = 45
    // 60kg bench → score = 60/45 = 1.333 → rank B (1.0–1.5)
    val femaleBenchPR = PersonalRecord(
        id = 10, userId = 2, exerciseId = 1, date = now, bestWeightKg = 60.0, bestReps = 5
    )
    val femaleResult = MuscleSystem.recalculateAll(
        profile = femaleProfile, allPRs = listOf(femaleBenchPR), allExercises = allExercises,
        bestCardioSessions = emptyMap(), allCardioExercises = CardioExerciseLibrary.all,
        existingScores = emptyList(), nowMs = now
    )
    val femaleChest = femaleResult.updatedScores.find { it.muscleGroup == MuscleGroup.CHEST }
    val expectedFemaleScore = 60.0 / (60.0 * 0.75)
    println("\n  Female coefficient check (60kg bench, 60kg bodyweight, female, 25):")
    println("    Expected score: $expectedFemaleScore  Actual: ${femaleChest?.rawScore}")
    println("    Rank: ${femaleChest?.rank}  (expected B — 1.33 is between 1.0 and 1.5)")
    check("Female 60kg bench scores higher than male would") {
        femaleChest != null && femaleChest.rawScore > 1.0
    }
    check("Female chest rank = B") { femaleChest?.rank == MuscleRank.B }

    // ── getRankedMusclesSorted and getUnrankedMuscles ──────
    val mixedScores = result.updatedScores // from the first benchmark test
    val ranked   = MuscleSystem.getRankedMusclesSorted(mixedScores)
    val unranked = MuscleSystem.getUnrankedMuscles(mixedScores)
    println("\n  getRankedMusclesSorted: ${ranked.size} ranked muscles")
    println("  getUnrankedMuscles:     ${unranked.size} unranked muscles")
    check("Ranked + unranked covers all muscles except HIP_FLEXORS") {
        ranked.size + unranked.size == MuscleGroup.entries.size - 1
    }
    check("Ranked muscles sorted strongest first") {
        ranked.zipWithNext().all { (a, b) -> a.rawScore >= b.rawScore }
    }

    // ── progressToNextRank computed property ──────────────
    // chestScore is rank A (1.5). Next rank = S (2.0). Progress = (1.5-1.5)/(2.0-1.5) = 0.0
    println("\n  progressToNextRank for chest (rank A, score exactly 1.5):")
    println("    Progress: ${chestScore?.progressToNextRank}  (expected 0.0 — at the floor of A)")
    check("progressToNextRank = 0.0 at exact rank threshold") {
        chestScore?.progressToNextRank == 0.0
    }
    // S rank is max — progressToNextRank should return 1.0
    val sRankPR = PersonalRecord(id = 98, userId = 1, exerciseId = 1, date = now, bestWeightKg = 200.0, bestReps = 1)
    val sResult = MuscleSystem.recalculateAll(profile, listOf(sRankPR), allExercises, emptyMap(), CardioExerciseLibrary.all, emptyList(), now)
    val sChest  = sResult.updatedScores.find { it.muscleGroup == MuscleGroup.CHEST }
    check("progressToNextRank = 1.0 at S rank (no higher rank)") {
        sChest?.progressToNextRank == 1.0
    }
    println("  S rank progressToNextRank = 1.0 (no higher rank) ✓")
}


// ============================================================
// 7. ACHIEVEMENT TESTS
// ============================================================

fun testAchievements() {
    printHeader("7. ACHIEVEMENTS")

    val now = System.currentTimeMillis()
    val dayMs = 24L * 60 * 60 * 1000

    val baseUser = User(
        id = 1, name = "Stamatis", email = "s@test.com",
        currentStreak = 1,
        classMap = FitnessClass.entries
            .associateWith { ClassProgress(it) }
            .toMutableMap()
    )

    val session = WorkoutSession(
        id = 1, userId = 1, date = now,
        fitnessClass = FitnessClass.CHAMPION,
        sets = listOf(
            WorkoutSet(id = 1, exerciseId = 1, setNumber = 1, reps = 5, weightKg = 100.0)
        )
    )

    fun makeCtx(
        user: User = baseUser,
        sessions: List<WorkoutSession> = listOf(session),
        prs: List<PersonalRecord> = emptyList(),
        prevPrs: List<PersonalRecord> = emptyList(),
        quests: Int = 0,
        balanceQuests: Int = 0,
        previousLastWorkoutMs: Long = 0L
    ) = AchievementSystem.AchievementContext(
        user = user,
        allWorkoutSessions = sessions,
        allCardioSessions = emptyList(),
        personalRecords = prs,
        previousPersonalRecords = prevPrs,
        totalQuestsCompleted = quests,
        balanceQuestsCompleted = balanceQuests,
        latestSessionTimestampMs = now,
        previousLastWorkoutDateMs = previousLastWorkoutMs
    )

    // ── First session ─────────────────────────────────────
    val firstResult = AchievementSystem.checkAchievements(makeCtx(), emptyList())
    println("  First session achievements:")
    firstResult.newlyUnlocked.forEach { println("    ✓ ${it.name} (+${it.goldReward} gold)") }
    check("First Blood unlocks on first session") {
        firstResult.newlyUnlocked.any { it.id == "volume_first_blood" }
    }
    println("  Total gold: ${firstResult.goldEarned}")

    // ── Streak achievements ───────────────────────────────
    val sevenDayResult = AchievementSystem.checkAchievements(
        makeCtx(user = baseUser.copy(currentStreak = 7)), emptyList()
    )
    check("On a Roll unlocks at 7-day streak") {
        sevenDayResult.newlyUnlocked.any { it.id == "streak_on_a_roll" }
    }
    println("\n  7-day streak: On a Roll ✓")

    val thirtyDayResult = AchievementSystem.checkAchievements(
        makeCtx(user = baseUser.copy(currentStreak = 30)), emptyList()
    )
    check("Iron Will unlocks at 30-day streak") {
        thirtyDayResult.newlyUnlocked.any { it.id == "streak_iron_will" }
    }
    println("  30-day streak: Iron Will ✓")

    // ── PR 20% improvement threshold ──────────────────────
    println("\n  PR achievement — 20% improvement check:")
    val prevPR = PersonalRecord(id = 1, userId = 1, exerciseId = 1, date = now - dayMs,
        bestWeightKg = 100.0, bestReps = 5)

    // 21% improvement — should unlock
    val newPR21 = PersonalRecord(id = 2, userId = 1, exerciseId = 1, date = now,
        bestWeightKg = 121.0, bestReps = 5)
    val pr21Result = AchievementSystem.checkAchievements(
        makeCtx(prs = listOf(newPR21), prevPrs = listOf(prevPR)), emptyList()
    )
    val newHeights21 = pr21Result.newlyUnlocked.any { it.id == "pr_new_heights" }
    println("    100kg → 121kg (21%): New Heights = $newHeights21  (expected true)")
    check("21% improvement unlocks New Heights") { newHeights21 }

    // Exactly 20% — should also unlock (>= not >)
    val newPR20 = PersonalRecord(id = 3, userId = 1, exerciseId = 1, date = now,
        bestWeightKg = 120.0, bestReps = 5)
    val pr20Result = AchievementSystem.checkAchievements(
        makeCtx(prs = listOf(newPR20), prevPrs = listOf(prevPR)), emptyList()
    )
    val newHeights20 = pr20Result.newlyUnlocked.any { it.id == "pr_new_heights" }
    println("    100kg → 120kg (20%): New Heights = $newHeights20  (expected true — boundary)")
    check("Exactly 20% improvement unlocks New Heights (boundary)") { newHeights20 }

    // 5% improvement — should NOT unlock
    val newPR5 = PersonalRecord(id = 4, userId = 1, exerciseId = 1, date = now,
        bestWeightKg = 105.0, bestReps = 5)
    val pr5Result = AchievementSystem.checkAchievements(
        makeCtx(prs = listOf(newPR5), prevPrs = listOf(prevPR)), emptyList()
    )
    val newHeights5 = pr5Result.newlyUnlocked.any { it.id == "pr_new_heights" }
    println("    100kg → 105kg (5%): New Heights = $newHeights5  (expected false)")
    check("5% improvement does NOT unlock New Heights") { !newHeights5 }

    // ── ComebackAfterInactivity — previousLastWorkoutDateMs fix ──
    // This is the bug that was fixed in the audit: if we pass previousLastWorkoutDateMs = 0L
    // (brand new user), the comeback achievement must NOT fire even though the gap is huge.
    println("\n  ComebackAfterInactivity — audit fix check:")
    val comebackCtxNewUser = makeCtx(previousLastWorkoutMs = 0L)
    val newUserResult = AchievementSystem.checkAchievements(comebackCtxNewUser, emptyList())
    val comebackOnFirstWorkout = newUserResult.newlyUnlocked.any { it.id == "streak_comeback" }
    println("    Brand new user (previousLastWorkout=0L): comeback fires = $comebackOnFirstWorkout  (expected false)")
    check("Comeback does NOT fire on first ever workout (previousLastWorkout=0L guard)") {
        !comebackOnFirstWorkout
    }

    // A real comeback: user last worked out 30 days ago, then logged today
    // previousLastWorkoutDateMs = 30 days ago, latestSessionTimestampMs = now
    val comebackCtxRealReturn = AchievementSystem.AchievementContext(
        user = baseUser,
        allWorkoutSessions = listOf(session),
        allCardioSessions = emptyList(),
        personalRecords = emptyList(),
        previousPersonalRecords = emptyList(),
        totalQuestsCompleted = 0,
        balanceQuestsCompleted = 0,
        latestSessionTimestampMs = now,
        previousLastWorkoutDateMs = now - 30 * dayMs  // truly returning after 30 days
    )
    val realComebackResult = AchievementSystem.checkAchievements(comebackCtxRealReturn, emptyList())
    val comebackFired = realComebackResult.newlyUnlocked.any { it.id == "streak_comeback" }
    println("    Real comeback (previous workout 30 days ago): comeback fires = $comebackFired  (expected true)")
    check("Comeback fires when genuinely returning after 14+ days") { comebackFired }
}


// ============================================================
// 8. QUEST GENERATION TEST
// ============================================================

fun testQuestGeneration() {
    printHeader("8. QUEST GENERATION")

    val now = System.currentTimeMillis()

    val baseUser = User(
        id = 1, name = "Stamatis", email = "s@test.com",
        currentClass = FitnessClass.CHAMPION,
        classMap = FitnessClass.entries.associateWith { ClassProgress(it) }.toMutableMap()
    )

    // ── Quest counts per tier ─────────────────────────────
    val peasantUser = baseUser.copy(streakTier = StreakTier.PEASANT)
    val peasantQuests = QuestSystem.generateQuests(peasantUser, emptyList(), emptyList(), now)
    println("  PEASANT quest generation:")
    println("    Class quests:   ${peasantQuests.classQuests.size}  (expected 1)")
    println("    Balance quests: ${peasantQuests.balanceQuests.size}  (expected 1)")
    check("PEASANT gets 1 class quest")   { peasantQuests.classQuests.size == 1 }
    check("PEASANT gets 1 balance quest") { peasantQuests.balanceQuests.size == 1 }

    val knightUser = baseUser.copy(streakTier = StreakTier.KNIGHT)
    val knightQuests = QuestSystem.generateQuests(knightUser, emptyList(), emptyList(), now)
    println("\n  KNIGHT quest generation:")
    println("    Class quests:   ${knightQuests.classQuests.size}  (expected 3)")
    println("    Balance quests: ${knightQuests.balanceQuests.size}  (expected 2)")
    knightQuests.allQuests.forEach { q ->
        println("    [${if (q.isBalanceQuest) "BALANCE" else "CLASS  "}] ${q.rarity.name.padEnd(8)} ${q.title}")
    }
    check("KNIGHT gets 3 class quests")   { knightQuests.classQuests.size == 3 }
    check("KNIGHT gets 2 balance quests") { knightQuests.balanceQuests.size == 2 }

    val eliteUser = baseUser.copy(streakTier = StreakTier.ELITE)
    val eliteQuests = QuestSystem.generateQuests(eliteUser, emptyList(), emptyList(), now)
    println("\n  ELITE quest generation:")
    println("    Class quests:   ${eliteQuests.classQuests.size}  (expected 5)")
    println("    Balance quests: ${eliteQuests.balanceQuests.size}  (expected 3)")
    check("ELITE gets 5 class quests")   { eliteQuests.classQuests.size == 5 }
    check("ELITE gets 3 balance quests") { eliteQuests.balanceQuests.size == 3 }

    // ── First quest always COMMON ──────────────────────────
    check("PEASANT first class quest is COMMON")  { peasantQuests.classQuests.first().rarity == models.QuestRarity.COMMON }
    check("KNIGHT first class quest is COMMON")   { knightQuests.classQuests.first().rarity == models.QuestRarity.COMMON }
    check("ELITE first class quest is COMMON")    { eliteQuests.classQuests.first().rarity == models.QuestRarity.COMMON }
    println("\n  First quest is always COMMON ✓")

    // ── HIT_STREAK target values per tier ─────────────────
    val knightStreakQuest = knightQuests.allQuests.firstOrNull { it.type == models.QuestType.HIT_STREAK }
    if (knightStreakQuest != null) {
        println("  KNIGHT HIT_STREAK target: ${knightStreakQuest.targetValue}  (expected 4 — upper bound of tier)")
        check("KNIGHT HIT_STREAK target = 4") { knightStreakQuest.targetValue == 4 }
    }

    // ── Quest expiry is exactly 7 days ────────────────────
    val quest      = knightQuests.classQuests.first()
    val expiryDays = (quest.expiryMs - quest.weekStartMs) / (24 * 60 * 60 * 1000)
    println("  Quest expiry: $expiryDays days  (expected 7)")
    check("Quest expires in exactly 7 days") { expiryDays == 7L }
}


// ============================================================
// HELPERS
// ============================================================

fun printHeader(title: String) {
    println("\n" + "═".repeat(60))
    println("  $title")
    println("═".repeat(60))
}

fun printResult(label: String, result: XpSystem.XpResult) {
    println("\n  $label:")
    println("    Base XP:        ${result.baseXp}")
    println("    Class mult:     ×${result.classMultiplier}")
    println("    Difficulty mult:×${String.format("%.2f", result.difficultyMultiplier)}")
    println("    Streak mult:    ×${result.streakMultiplier}")
    println("    Final XP:       ${result.finalXp}")
}

fun check(label: String, condition: () -> Boolean) {
    val passed = runCatching { condition() }.getOrDefault(false)
    val icon   = if (passed) "✓" else "✗ FAIL"
    println("  [$icon] $label")
}
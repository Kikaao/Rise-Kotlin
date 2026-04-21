package systems

import models.ClassProgress
import models.FitnessClass
import models.PersonalRecord

// ============================================================
// LevelSystem.kt
// Layer: Systems
// Communicates with: Models only (ClassProgress, FitnessClass,
//                    PersonalRecord, LevelMilestone)
// Never communicates with: UI, ViewModel, Repository, Database
// ============================================================
// All timestamps are Unix time in milliseconds (System.currentTimeMillis())
// ============================================================

object LevelSystem {

    // --------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------

    const val MAX_LEVEL = 100

    // --------------------------------------------------------
    // XP CURVE — STEPPED (Option C)
    // Three bands defined for STRUCTURE; current VALUES are two-band.
    //
    // Fix: the comment previously claimed "Band 1: fast, Band 2: moderate, Band 3: hard"
    // but BASE_XP_BAND_1 and BASE_XP_BAND_2 were both 150 — mathematically the curve
    // is two-band (flat 150 × level through level 60, then 200 × level). This didn't
    // match the documented intent.
    //
    // Decision needed from Stamatis: either
    //   (a) tune BAND_1 down (e.g. 100) for a genuine fast-early ramp, or
    //   (b) accept that early game is "flat" and update the doc to say two bands.
    // Left as-is pending that tuning pass (v2.1 locks the structure but values are TUNABLE).
    //
    // Band 1: Levels 1–20   → current rate: 150 XP × level (same as Band 2 — see above)
    // Band 2: Levels 21–60  → current rate: 150 XP × level
    // Band 3: Levels 61–100 → current rate: 200 XP × level
    //
    // Formula per level:
    //   Band 1: BASE_XP_BAND_1 × level
    //   Band 2: BASE_XP_BAND_2 × level
    //   Band 3: BASE_XP_BAND_3 × level
    // --------------------------------------------------------

    private const val BASE_XP_BAND_1 = 150      // TUNABLE — levels 1–20 (currently equal to BAND_2)
    private const val BASE_XP_BAND_2 = 150      // TUNABLE — levels 21–60
    private const val BASE_XP_BAND_3 = 200      // TUNABLE — levels 61–100


    // --------------------------------------------------------
    // MILESTONE TABLE
    // Soft gates (isRequired = false): levels 10, 25, 40
    //   → shown to the player as a goal, but XP alone is enough to level up
    // Hard gates (isRequired = true): every 5 levels from 50–100
    //   → player cannot level up without meeting the milestone
    //
    // exerciseId and minimumWeightKg are null until requirements are decided.
    // When null, the gate is treated as not yet implemented and passes through.
    // To add a requirement later: set exerciseId and minimumWeightKg — zero
    // structural changes needed.
    // --------------------------------------------------------

    data class LevelMilestone(
        val fitnessClass: FitnessClass,
        val level: Int,
        val isRequired: Boolean,
        val description: String,            // shown to the player in the UI
        val exerciseId: Int? = null,        // null = not implemented yet → passes through
        val minimumWeightKg: Double? = null // null = not implemented yet → passes through
    )

    // Milestone table — one entry per class per gate level
    // Requirements (exerciseId, minimumWeightKg) filled in when decided
    private val milestones: List<LevelMilestone> = buildList {

        // ── CHAMPION ──────────────────────────────────────────
        add(LevelMilestone(FitnessClass.CHAMPION, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.CHAMPION, 100, true,  "Required milestone — to be defined"))

        // ── STRIKER ───────────────────────────────────────────
        add(LevelMilestone(FitnessClass.STRIKER, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.STRIKER, 100, true,  "Required milestone — to be defined"))

        // ── ROGUE ─────────────────────────────────────────────
        add(LevelMilestone(FitnessClass.ROGUE, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ROGUE, 100, true,  "Required milestone — to be defined"))

        // ── MONK ──────────────────────────────────────────────
        add(LevelMilestone(FitnessClass.MONK, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.MONK, 100, true,  "Required milestone — to be defined"))

        // ── ADVENTURER ────────────────────────────────────────
        add(LevelMilestone(FitnessClass.ADVENTURER, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.ADVENTURER, 100, true,  "Required milestone — to be defined"))

        // ── PARAGON ───────────────────────────────────────────
        add(LevelMilestone(FitnessClass.PARAGON, 10,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 25,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 40,  false, "Soft gate — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 50,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 55,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 60,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 65,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 70,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 75,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 80,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 85,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 90,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 95,  true,  "Required milestone — to be defined"))
        add(LevelMilestone(FitnessClass.PARAGON, 100, true,  "Required milestone — to be defined"))
    }


    // --------------------------------------------------------
    // PUBLIC FUNCTIONS
    // --------------------------------------------------------

    /**
     * Returns the total XP required to reach a given level from level 0.
     * This is cumulative — the sum of all XP costs from level 1 up to [level].
     */
    fun getTotalXpForLevel(level: Int): Int {
        if (level <= 0) return 0
        return (1..level.coerceAtMost(MAX_LEVEL)).sumOf { getXpForLevel(it) }
    }

    /**
     * Returns the XP cost of a single level step (level-1 → level).
     * This is what resets to 0 on level up inside ClassProgress.
     */
    fun getXpForLevel(level: Int): Int {
        return when {
            level <= 20  -> BASE_XP_BAND_1 * level
            level <= 60  -> BASE_XP_BAND_2 * level
            else         -> BASE_XP_BAND_3 * level
        }
    }

    /**
     * Returns 0.0–1.0 progress through the current level.
     * Used by the UI to fill the XP bar on the profile screen.
     *
     * Example: level 5, xp = 1500, xpForLevel6 = 3000 → 0.5 (50%)
     */
    fun getProgressToNextLevel(progress: ClassProgress): Double {
        if (progress.level >= MAX_LEVEL) return 1.0
        val xpRequired = getXpForLevel(progress.level + 1)
        if (xpRequired <= 0) return 1.0
        return (progress.xp.toDouble() / xpRequired).coerceIn(0.0, 1.0)
    }

    /**
     * Attempts to level up a class. Returns a LevelUpResult describing
     * what happened — success, blocked by milestone, or already max level.
     *
     * Flow:
     * 1. Check if already at max level
     * 2. Check if enough XP for next level
     * 3. Check milestone gate — if required and not met, block
     * 4. Level up: xp resets (subtract cost), totalXp never resets, level++
     *
     * @param progress       Current class progress (level, xp, totalXp)
     * @param fitnessClass   The class being leveled
     * @param personalRecords All PRs for this user — used for milestone checks
     */
    fun attemptLevelUp(
        progress: ClassProgress,
        fitnessClass: FitnessClass,
        personalRecords: List<PersonalRecord>
    ): LevelUpResult {
        val currentLevel = progress.level
        val targetLevel = currentLevel + 1

        if (currentLevel >= MAX_LEVEL) {
            return LevelUpResult.AlreadyMaxLevel
        }

        val xpRequired = getXpForLevel(targetLevel)
        if (progress.xp < xpRequired) {
            return LevelUpResult.NotEnoughXp(
                currentXp = progress.xp,
                requiredXp = xpRequired
            )
        }

        // Check milestone gate for target level
        val milestone = getMilestone(fitnessClass, targetLevel)
        if (milestone != null && milestone.isRequired) {
            if (!isMilestoneMet(milestone, personalRecords)) {
                return LevelUpResult.MilestoneNotMet(
                    milestone = milestone,
                    currentLevel = currentLevel,
                    targetLevel = targetLevel
                )
            }
        }

        // All checks passed — apply level up
        // xp resets by subtracting the cost (overflow carries to next level)
        // totalXp is already updated in addXp() — do not touch it here
        val updatedProgress = progress.copy(
            level = targetLevel,
            xp = progress.xp - xpRequired
        )

        return LevelUpResult.Success(
            updatedProgress = updatedProgress,
            newLevel = targetLevel,
            milestone = milestone // null if no milestone at this level
        )
    }

    /**
     * Adds XP to a class and triggers level up attempts in a loop.
     * A single workout could theoretically trigger multiple level ups
     * (especially at low levels) so we keep checking until XP is spent.
     *
     * Returns the final ClassProgress and a list of all levels gained.
     */
    fun addXp(
        progress: ClassProgress,
        fitnessClass: FitnessClass,
        xpToAdd: Int,
        personalRecords: List<PersonalRecord>
    ): AddXpResult {
        // totalXp is cumulative and never resets — update it immediately with the
        // full amount earned, regardless of whether a level up happens.
        // xp (current level progress) is separate and resets on level up.
        var current = progress.copy(
            xp = progress.xp + xpToAdd,
            totalXp = progress.totalXp + xpToAdd
        )
        val levelsGained = mutableListOf<Int>()
        var blockedByMilestone: LevelUpResult.MilestoneNotMet? = null

        // Keep attempting level ups until XP runs out or something blocks
        while (current.level < MAX_LEVEL) {
            when (val result = attemptLevelUp(current, fitnessClass, personalRecords)) {
                is LevelUpResult.Success -> {
                    current = result.updatedProgress
                    levelsGained.add(result.newLevel)
                }
                is LevelUpResult.MilestoneNotMet -> {
                    // Blocked — stop trying, surface the milestone to the player
                    blockedByMilestone = result
                    break
                }
                is LevelUpResult.NotEnoughXp -> break  // Not enough XP — done
                is LevelUpResult.AlreadyMaxLevel -> break
            }
        }

        return AddXpResult(
            updatedProgress = current,
            levelsGained = levelsGained,
            blockedByMilestone = blockedByMilestone
        )
    }


    // --------------------------------------------------------
    // RESULT TYPES
    // --------------------------------------------------------

    sealed class LevelUpResult {
        data class Success(
            val updatedProgress: ClassProgress,
            val newLevel: Int,
            val milestone: LevelMilestone?   // shown in UI as "milestone reached!"
        ) : LevelUpResult()

        data class MilestoneNotMet(
            val milestone: LevelMilestone,
            val currentLevel: Int,
            val targetLevel: Int
        ) : LevelUpResult()

        data class NotEnoughXp(
            val currentXp: Int,
            val requiredXp: Int
        ) : LevelUpResult()

        object AlreadyMaxLevel : LevelUpResult()
    }

    data class AddXpResult(
        val updatedProgress: ClassProgress,
        val levelsGained: List<Int>,                         // empty if no level up
        val blockedByMilestone: LevelUpResult.MilestoneNotMet?  // null if not blocked
    )


    // --------------------------------------------------------
    // PRIVATE HELPERS
    // --------------------------------------------------------

    /**
     * Finds the milestone for a specific class + level combination.
     * Returns null if no milestone exists at that level.
     */
    private fun getMilestone(fitnessClass: FitnessClass, level: Int): LevelMilestone? {
        return milestones.find { it.fitnessClass == fitnessClass && it.level == level }
    }

    /**
     * Checks whether a milestone's PR requirement is met.
     *
     * If exerciseId is null → requirement not yet defined → passes through.
     * If minimumWeightKg is null → requirement not yet defined → passes through.
     * Otherwise → look up the user's PR for that exercise and compare.
     */
    private fun isMilestoneMet(
        milestone: LevelMilestone,
        personalRecords: List<PersonalRecord>
    ): Boolean {
        val exerciseId = milestone.exerciseId ?: return true       // not defined yet → pass
        val minimumWeight = milestone.minimumWeightKg ?: return true // not defined yet → pass

        val pr = personalRecords.find { it.exerciseId == exerciseId }
            ?: return false  // no PR logged for this exercise → not met

        return (pr.bestWeightKg ?: 0.0) >= minimumWeight
    }
}

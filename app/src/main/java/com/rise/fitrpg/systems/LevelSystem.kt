package com.rise.fitrpg.systems

import com.rise.fitrpg.data.models.ClassProgress
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.PersonalRecord


object LevelSystem {

    // --------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------

    const val MAX_LEVEL = 100

    // XP cost per level — three bands, multiplier × level

    private const val BASE_XP_BAND_1 = 150      //  levels 1–20 (
    private const val BASE_XP_BAND_2 = 150      //  levels 21–60
    private const val BASE_XP_BAND_3 = 200      //  levels 61–100


    // mileston
    // Soft gates (isRequired = false): levels 10, 25, 40
    // shown to the player as a goal, but XP alone is enough to level up

    // Hard gates (isRequired = true): every 5 levels from 50–100
    // player cannot level up without meeting the milestone
    //

    data class LevelMilestone(
        val fitnessClass: FitnessClass,
        val level: Int,
        val isRequired: Boolean,
        val description: String,
        val exerciseId: Int? = null,
        val minimumWeightKg: Double? = null
    )

    // AI STUFF
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


    //Total XP needed to reach a level from zero
    fun getTotalXpForLevel(level: Int): Int {
        if (level <= 0) return 0
        return (1..level.coerceAtMost(MAX_LEVEL)).sumOf { getXpForLevel(it) }
    }

    // XP cost of a single level up
    fun getXpForLevel(level: Int): Int {
        return when {
            level <= 20  -> BASE_XP_BAND_1 * level
            level <= 60  -> BASE_XP_BAND_2 * level
            else         -> BASE_XP_BAND_3 * level
        }
    }

    // Progress through the current level as 0.0–1.0 — fed to the XP bar on the profile screen - AI STUF
    fun getProgressToNextLevel(progress: ClassProgress): Double {
        if (progress.level >= MAX_LEVEL) return 1.0
        val xpRequired = getXpForLevel(progress.level + 1)
        if (xpRequired <= 0) return 1.0
        return (progress.xp.toDouble() / xpRequired).coerceIn(0.0, 1.0)
    }

    // Tries to level up once. returns what happened
    // Checks in order: max level, enough XP, milestone gate, then applies the level up
    // xp resets by subtracting the cost so overflow carries into the next level naturally
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

        // All checks passed THeN apply level up
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

    // Adds XP and keeps attempting level ups until XP runs out or a milestone blocks
    // A single session can trigger multiple level ups at low levels, the loop handles that
    fun addXp(
        progress: ClassProgress,
        fitnessClass: FitnessClass,
        xpToAdd: Int,
        personalRecords: List<PersonalRecord>
    ): AddXpResult {
        // full amount earned, regardless of whether a level up happens.
        // xp is separate and resets on level up.
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
                    // stop trying, surface the milestone to the player
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



    sealed class LevelUpResult {
        data class Success(
            val updatedProgress: ClassProgress,
            val newLevel: Int,
            val milestone: LevelMilestone?   // UI "milestone reached!"
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
        val levelsGained: List<Int>,
        val blockedByMilestone: LevelUpResult.MilestoneNotMet?  // null if not blocked
    )


    private fun getMilestone(fitnessClass: FitnessClass, level: Int): LevelMilestone? {
        return milestones.find { it.fitnessClass == fitnessClass && it.level == level }
    }


    // If exerciseId or minimumWeightKg is null the requirement isn't defined yet — pass through
    // No PR logged for the exercise at all = not met
    private fun isMilestoneMet(
        milestone: LevelMilestone,
        personalRecords: List<PersonalRecord>
    ): Boolean {
        val exerciseId = milestone.exerciseId ?: return true       // not defined   pass
        val minimumWeight = milestone.minimumWeightKg ?: return true // not defined   pass

        val pr = personalRecords.find { it.exerciseId == exerciseId }
            ?: return false  // no PR logged for this exercise   not met

        return (pr.bestWeightKg ?: 0.0) >= minimumWeight
    }
}

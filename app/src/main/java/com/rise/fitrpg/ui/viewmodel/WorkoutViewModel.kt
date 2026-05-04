package com.rise.fitrpg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rise.fitrpg.AppConstants
import com.rise.fitrpg.data.models.CardioExerciseLibrary
import com.rise.fitrpg.data.models.CardioSession
import com.rise.fitrpg.data.models.Exercise
import com.rise.fitrpg.data.models.ExerciseLibrary
import com.rise.fitrpg.data.models.PersonalRecord
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.data.models.WorkoutSession
import com.rise.fitrpg.data.models.WorkoutSet
import com.rise.fitrpg.data.repository.AchievementRepository
import com.rise.fitrpg.data.repository.CardioRepository
import com.rise.fitrpg.data.repository.QuestRepository
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.systems.AchievementSystem
import com.rise.fitrpg.systems.LevelSystem
import com.rise.fitrpg.systems.QuestSystem
import com.rise.fitrpg.systems.StatSystem
import com.rise.fitrpg.systems.StreakSystem
import com.rise.fitrpg.systems.XpSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============================================================
// WorkoutViewModel
// Layer: ViewModel
// ============================================================
// Handles the full workout lifecycle:
//
// DURING WORKOUT:
//   - User adds sets (exercise, reps, weight, duration)
//   - XP is calculated per set for immediate feedback
//   - Sets accumulate in a mutable list until session is saved
//
// ON SAVE (processCompletedWorkout):
//   The 6-system orchestration chain runs in order:
//   1. XP      — calculate final XP per set (already done during input)
//   2. Stats   — apply stat gains from XP earned
//   3. Level   — add XP to class progress, attempt level ups
//   4. Streak  — update streak (increment day count if new day)
//   5. Quests  — update quest progress, check for completions
//   6. Achieve — check all achievement conditions
//   7. Save    — persist everything in one pass
//
// Also handles cardio session logging (simpler — one session, no sets).
//
// The post-workout summary is exposed via WorkoutResult so the UI
// can show XP earned, levels gained, achievements unlocked, etc.
// ============================================================

class WorkoutViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository,
    private val questRepository: QuestRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val userId = AppConstants.CURRENT_USER_ID

    // ── IN-PROGRESS WORKOUT STATE ──────────────────────────

    // Sets accumulated during the current workout — the user adds these one by one.
    // Each set already has its XP calculated for immediate feedback.
    private val _currentSets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val currentSets: StateFlow<List<WorkoutSet>> = _currentSets.asStateFlow()

    // Running total XP for the session — shown live as the user logs sets.
    private val _sessionXpTotal = MutableStateFlow(0)
    val sessionXpTotal: StateFlow<Int> = _sessionXpTotal.asStateFlow()

    // Post-workout result — set after processCompletedWorkout finishes.
    // UI observes this to show the summary screen.
    private val _workoutResult = MutableStateFlow<WorkoutResult?>(null)
    val workoutResult: StateFlow<WorkoutResult?> = _workoutResult.asStateFlow()

    // Loading state — true while the save/processing chain is running.
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // ── SET MANAGEMENT ─────────────────────────────────────

    // Adds a set to the in-progress workout.
    // XP is calculated immediately so the user sees feedback per set.
    // The set ID is assigned as the next index (0-based).
    fun addSet(
        exerciseId: Int,
        reps: Int? = null,
        weightKg: Double? = null,
        durationSeconds: Int? = null
    ) {
        viewModelScope.launch {
            val exercise = ExerciseLibrary.getById(exerciseId) ?: return@launch
            val user = userRepository.getUser(userId) ?: return@launch
            val profile = userRepository.getProfile(userId)

            val set = WorkoutSet(
                id = 0, // DB assigns real ID — 0 is placeholder during input
                exerciseId = exerciseId,
                setNumber = _currentSets.value.size + 1,
                reps = reps,
                weightKg = weightKg,
                durationSeconds = durationSeconds
            )

            val xpResult = XpSystem.calculateStrengthXP(
                exercise = exercise,
                set = set,
                currentClass = user.currentClass,
                currentStreakDays = user.currentStreak,
                // PlayerProfile.weightKg is the user's bodyweight — used for
                // bodyweight exercise difficulty calculation (pull-ups, push-ups, etc.)
                playerBodyweightKg = profile?.weightKg
            )

            val setWithXp = set.copy(xpEarned = xpResult.finalXp)

            _currentSets.value = _currentSets.value + setWithXp
            _sessionXpTotal.value += xpResult.finalXp
        }
    }

    // Removes the last set — undo for mistakes during logging.
    fun removeLastSet() {
        val sets = _currentSets.value
        if (sets.isEmpty()) return
        val removed = sets.last()
        _currentSets.value = sets.dropLast(1)
        _sessionXpTotal.value -= removed.xpEarned
    }

    // Clears all sets — used when cancelling a workout.
    fun clearWorkout() {
        _currentSets.value = emptyList()
        _sessionXpTotal.value = 0
        _workoutResult.value = null
    }

    // ── SAVE STRENGTH WORKOUT ──────────────────────────────

    // Called when the user taps "Finish Workout".
    // Runs the full 6-system orchestration chain.
    fun saveWorkout(notes: String? = null) {
        val sets = _currentSets.value
        if (sets.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val user = userRepository.getUser(userId) ?: return@launch
                val nowMs = System.currentTimeMillis()

                val session = WorkoutSession(
                    id = 0, // DB assigns real ID via autoGenerate
                    userId = userId,
                    date = nowMs,
                    fitnessClass = user.currentClass,
                    sets = sets,
                    totalXpEarned = _sessionXpTotal.value,
                    notes = notes
                )

                val result = processCompletedWorkout(user, session, nowMs)
                _workoutResult.value = result

                // Clear the in-progress state now that everything is saved
                _currentSets.value = emptyList()
                _sessionXpTotal.value = 0
            } finally {
                _isSaving.value = false
            }
        }
    }

    // ── SAVE CARDIO SESSION ────────────────────────────────
    // Simpler flow — no sets to accumulate, just one session object.

    fun saveCardioSession(
        cardioExerciseId: Int,
        distanceKm: Double? = null,
        durationMinutes: Double? = null,
        sets: Int? = null,
        metersPerSet: Int? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val user = userRepository.getUser(userId) ?: return@launch
                val cardioExercise = CardioExerciseLibrary.getById(cardioExerciseId) ?: return@launch
                val nowMs = System.currentTimeMillis()

                // Calculate XP
                val rawSession = CardioSession(
                    id = 0,
                    userId = userId,
                    cardioExerciseId = cardioExerciseId,
                    date = nowMs,
                    distanceKm = distanceKm,
                    durationMinutes = durationMinutes,
                    sets = sets,
                    metersPerSet = metersPerSet,
                    notes = notes
                )

                val xpResult = XpSystem.calculateCardioXP(
                    cardioExercise = cardioExercise,
                    session = rawSession,
                    currentStreakDays = user.currentStreak
                )

                val session = rawSession.copy(xpEarned = xpResult.finalXp)

                val result = processCompletedCardioSession(user, session, nowMs)
                _workoutResult.value = result
            } finally {
                _isSaving.value = false
            }
        }
    }

    // ── ORCHESTRATION: STRENGTH WORKOUT ────────────────────
    // The full post-workout processing chain.
    // Each step feeds into the next. Order matters.

    private suspend fun processCompletedWorkout(
        user: User,
        session: WorkoutSession,
        nowMs: Long
    ): WorkoutResult {
        var currentUser = user
        val profile = userRepository.getProfile(userId)

        // ── 1. STAT GAINS ──────────────────────────────────
        // Apply stat gains from each set's XP.
        // Stats are cumulative — we update the user's stats in place.
        var updatedStats = currentUser.stats.copy()
        for (set in session.sets) {
            val exercise = ExerciseLibrary.getById(set.exerciseId) ?: continue
            val statResult = StatSystem.applyStrengthStatGains(
                currentStats = updatedStats,
                exercise = exercise,
                finalXp = set.xpEarned
            )
            updatedStats = statResult.statsAfter
        }
        currentUser = currentUser.copy(stats = updatedStats)

        // ── 2. LEVEL PROGRESS ──────────────────────────────
        // Add session XP to the current class and attempt level ups.
        val currentProgress = currentUser.classMap[currentUser.currentClass]
            ?: return WorkoutResult.empty(session.totalXpEarned)

        val prs = workoutRepository.getAllPRs(userId)

        val addXpResult = LevelSystem.addXp(
            progress = currentProgress,
            fitnessClass = currentUser.currentClass,
            xpToAdd = session.totalXpEarned,
            personalRecords = prs
        )

        val updatedClassMap = currentUser.classMap.toMutableMap()
        updatedClassMap[currentUser.currentClass] = addXpResult.updatedProgress
        currentUser = currentUser.copy(classMap = updatedClassMap)

        // ── 3. STREAK ──────────────────────────────────────
        val previousLastWorkoutDate = currentUser.lastWorkoutDate
        val streakResult = StreakSystem.onWorkoutLogged(currentUser, nowMs)
        val streakIncremented = streakResult is StreakSystem.WorkoutLoggedResult.StreakIncremented
        if (streakResult is StreakSystem.WorkoutLoggedResult.StreakIncremented) {
            currentUser = streakResult.updatedUser
        }

        // ── 4. PERSONAL RECORDS ────────────────────────────
        // Check if any set in this session is a new PR.
        // PRs are saved individually — the achievement system needs them.
        val previousPRs = prs.toList() // snapshot before updates
        val updatedPRs = mutableListOf<PersonalRecord>()
        for (set in session.sets) {
            val existingPR = workoutRepository.getPR(userId, set.exerciseId)
            val isNewPR = isNewPersonalRecord(set, existingPR)
            if (isNewPR) {
                val newPR = PersonalRecord(
                    id = 0,
                    userId = userId,
                    exerciseId = set.exerciseId,
                    date = nowMs,
                    bestWeightKg = set.weightKg,
                    bestReps = set.reps,
                    bestDurationSeconds = set.durationSeconds
                )
                workoutRepository.savePersonalRecord(newPR)
                updatedPRs.add(newPR)
            }
        }

        // ── 5. QUESTS ──────────────────────────────────────
        val activeQuests = questRepository.getActiveQuests(userId, nowMs)
        val questResult = QuestSystem.updateQuestProgress(
            activeQuests = activeQuests,
            workoutSession = session,
            cardioSession = null,
            user = currentUser,
            nowMs = nowMs
        )

        // Save updated quest progress
        for (quest in questResult.updatedQuests) {
            questRepository.updateQuest(quest, userId)
        }

        // Award quest XP and gold to user
        if (questResult.totalXpEarned > 0 || questResult.totalGoldEarned > 0) {
            currentUser = currentUser.copy(
                gold = currentUser.gold + questResult.totalGoldEarned
            )

            // Quest XP also goes through LevelSystem
            if (questResult.totalXpEarned > 0) {
                val questXpResult = LevelSystem.addXp(
                    progress = updatedClassMap[currentUser.currentClass]!!,
                    fitnessClass = currentUser.currentClass,
                    xpToAdd = questResult.totalXpEarned,
                    personalRecords = workoutRepository.getAllPRs(userId)
                )
                val questClassMap = currentUser.classMap.toMutableMap()
                questClassMap[currentUser.currentClass] = questXpResult.updatedProgress
                currentUser = currentUser.copy(classMap = questClassMap)
            }

            // Focus stat from completed quests
            for (completed in questResult.newlyCompleted) {
                val focusResult = StatSystem.addFocusFromQuestCompletion(currentUser.stats)
                currentUser = currentUser.copy(stats = focusResult.statsAfter)
            }
        }

        // Focus stat from streak (only if streak incremented today)
        if (streakIncremented) {
            val focusResult = StatSystem.addFocusFromStreak(
                currentStats = currentUser.stats,
                streakDays = currentUser.currentStreak
            )
            currentUser = currentUser.copy(stats = focusResult.statsAfter)
        }

        // ── 6. ACHIEVEMENTS ────────────────────────────────
        val allWorkouts = workoutRepository.getAllSessions(userId) + session
        val allCardio = cardioRepository.getAllSessions(userId)
        val completedQuestCount = questRepository.getCompletedQuestCount(userId)
        // Balance quests completed — count quests where isBalanceQuest == true
        val balanceQuestsCompleted = questRepository.getCompletedQuests(userId)
            .count { it.isBalanceQuest }

        val achievementContext = AchievementSystem.AchievementContext(
            user = currentUser,
            allWorkoutSessions = allWorkouts,
            allCardioSessions = allCardio,
            personalRecords = workoutRepository.getAllPRs(userId),
            previousPersonalRecords = previousPRs,
            totalQuestsCompleted = completedQuestCount,
            balanceQuestsCompleted = balanceQuestsCompleted,
            latestSessionTimestampMs = nowMs,
            previousLastWorkoutDateMs = previousLastWorkoutDate
        )

        val currentAchievementProgress = achievementRepository.getAll()
        val achievementResult = AchievementSystem.checkAchievements(
            context = achievementContext,
            currentProgress = currentAchievementProgress
        )

        // Save achievement progress
        achievementRepository.saveAll(achievementResult.updatedProgress)

        // Award achievement gold
        if (achievementResult.goldEarned > 0) {
            currentUser = currentUser.copy(
                gold = currentUser.gold + achievementResult.goldEarned
            )
        }

        // ── 7. SAVE ────────────────────────────────────────
        workoutRepository.saveWorkout(session)
        userRepository.saveUser(currentUser)

        return WorkoutResult(
            totalXpEarned = session.totalXpEarned + questResult.totalXpEarned,
            levelsGained = addXpResult.levelsGained,
            blockedByMilestone = addXpResult.blockedByMilestone,
            streakIncremented = streakIncremented,
            newStreakDays = currentUser.currentStreak,
            newPersonalRecords = updatedPRs,
            questsCompleted = questResult.newlyCompleted,
            questXpEarned = questResult.totalXpEarned,
            questGoldEarned = questResult.totalGoldEarned,
            achievementsUnlocked = achievementResult.newlyUnlocked.map { it.name },
            achievementGoldEarned = achievementResult.goldEarned
        )
    }

    // ── ORCHESTRATION: CARDIO SESSION ──────────────────────
    // Same chain but simpler — no sets, no PRs.

    private suspend fun processCompletedCardioSession(
        user: User,
        session: CardioSession,
        nowMs: Long
    ): WorkoutResult {
        var currentUser = user
        val cardioExercise = CardioExerciseLibrary.getById(session.cardioExerciseId)
            ?: return WorkoutResult.empty(session.xpEarned)

        // ── 1. STAT GAINS ──────────────────────────────────
        val statResult = StatSystem.applyCardioStatGains(
            currentStats = currentUser.stats,
            cardioExercise = cardioExercise,
            finalXp = session.xpEarned
        )
        currentUser = currentUser.copy(stats = statResult.statsAfter)

        // ── 2. LEVEL PROGRESS ──────────────────────────────
        val currentProgress = currentUser.classMap[currentUser.currentClass]
            ?: return WorkoutResult.empty(session.xpEarned)

        val prs = workoutRepository.getAllPRs(userId)
        val addXpResult = LevelSystem.addXp(
            progress = currentProgress,
            fitnessClass = currentUser.currentClass,
            xpToAdd = session.xpEarned,
            personalRecords = prs
        )

        val updatedClassMap = currentUser.classMap.toMutableMap()
        updatedClassMap[currentUser.currentClass] = addXpResult.updatedProgress
        currentUser = currentUser.copy(classMap = updatedClassMap)

        // ── 3. STREAK ──────────────────────────────────────
        val previousLastWorkoutDate = currentUser.lastWorkoutDate
        val streakResult = StreakSystem.onWorkoutLogged(currentUser, nowMs)
        val streakIncremented = streakResult is StreakSystem.WorkoutLoggedResult.StreakIncremented
        if (streakResult is StreakSystem.WorkoutLoggedResult.StreakIncremented) {
            currentUser = streakResult.updatedUser
        }

        // ── 4. QUESTS ──────────────────────────────────────
        val activeQuests = questRepository.getActiveQuests(userId, nowMs)
        val questResult = QuestSystem.updateQuestProgress(
            activeQuests = activeQuests,
            workoutSession = null,
            cardioSession = session,
            user = currentUser,
            nowMs = nowMs
        )

        for (quest in questResult.updatedQuests) {
            questRepository.updateQuest(quest, userId)
        }

        if (questResult.totalXpEarned > 0 || questResult.totalGoldEarned > 0) {
            currentUser = currentUser.copy(
                gold = currentUser.gold + questResult.totalGoldEarned
            )
            if (questResult.totalXpEarned > 0) {
                val questXpResult = LevelSystem.addXp(
                    progress = updatedClassMap[currentUser.currentClass]!!,
                    fitnessClass = currentUser.currentClass,
                    xpToAdd = questResult.totalXpEarned,
                    personalRecords = workoutRepository.getAllPRs(userId)
                )
                val questClassMap = currentUser.classMap.toMutableMap()
                questClassMap[currentUser.currentClass] = questXpResult.updatedProgress
                currentUser = currentUser.copy(classMap = questClassMap)
            }
            for (completed in questResult.newlyCompleted) {
                val focusResult = StatSystem.addFocusFromQuestCompletion(currentUser.stats)
                currentUser = currentUser.copy(stats = focusResult.statsAfter)
            }
        }

        if (streakIncremented) {
            val focusResult = StatSystem.addFocusFromStreak(
                currentStats = currentUser.stats,
                streakDays = currentUser.currentStreak
            )
            currentUser = currentUser.copy(stats = focusResult.statsAfter)
        }

        // ── 5. ACHIEVEMENTS ────────────────────────────────
        val allWorkouts = workoutRepository.getAllSessions(userId)
        val allCardio = cardioRepository.getAllSessions(userId) + session
        val completedQuestCount = questRepository.getCompletedQuestCount(userId)
        val balanceQuestsCompleted = questRepository.getCompletedQuests(userId)
            .count { it.isBalanceQuest }

        val achievementContext = AchievementSystem.AchievementContext(
            user = currentUser,
            allWorkoutSessions = allWorkouts,
            allCardioSessions = allCardio,
            personalRecords = workoutRepository.getAllPRs(userId),
            previousPersonalRecords = prs,
            totalQuestsCompleted = completedQuestCount,
            balanceQuestsCompleted = balanceQuestsCompleted,
            latestSessionTimestampMs = nowMs,
            previousLastWorkoutDateMs = previousLastWorkoutDate
        )

        val currentAchievementProgress = achievementRepository.getAll()
        val achievementResult = AchievementSystem.checkAchievements(
            context = achievementContext,
            currentProgress = currentAchievementProgress
        )

        achievementRepository.saveAll(achievementResult.updatedProgress)

        if (achievementResult.goldEarned > 0) {
            currentUser = currentUser.copy(
                gold = currentUser.gold + achievementResult.goldEarned
            )
        }

        // ── 6. SAVE ────────────────────────────────────────
        cardioRepository.saveSession(session)
        userRepository.saveUser(currentUser)

        return WorkoutResult(
            totalXpEarned = session.xpEarned + questResult.totalXpEarned,
            levelsGained = addXpResult.levelsGained,
            blockedByMilestone = addXpResult.blockedByMilestone,
            streakIncremented = streakIncremented,
            newStreakDays = currentUser.currentStreak,
            newPersonalRecords = emptyList(), // Cardio has no PRs
            questsCompleted = questResult.newlyCompleted,
            questXpEarned = questResult.totalXpEarned,
            questGoldEarned = questResult.totalGoldEarned,
            achievementsUnlocked = achievementResult.newlyUnlocked.map { it.name },
            achievementGoldEarned = achievementResult.goldEarned
        )
    }

    // ── PERSONAL RECORD CHECK ──────────────────────────────
    // Minimum percentage improvement required for a new PR to count.
    // Applies to all three tracking types — prevents micro-increment farming.
    // e.g. adding 1kg per session to bench press, or 1 rep to pull-ups.
    private companion object {
        const val MIN_PR_IMPROVEMENT_PERCENT = 20.0
    }

    // A set is a new PR if:
    //   - No existing PR for this exercise, OR
    //   - Improvement >= 20% over the previous best (weight, reps, or duration)

    private fun isNewPersonalRecord(set: WorkoutSet, existing: PersonalRecord?): Boolean {
        if (existing == null) return true

        // Weight-based: 20% improvement required
        if (set.weightKg != null && existing.bestWeightKg != null && existing.bestWeightKg > 0) {
            val improvementPct = ((set.weightKg - existing.bestWeightKg) / existing.bestWeightKg) * 100
            return improvementPct >= MIN_PR_IMPROVEMENT_PERCENT
        }

        // Reps-based (bodyweight exercises): 20% improvement required
        if (set.reps != null && existing.bestReps != null && set.weightKg == null && existing.bestReps > 0) {
            val improvementPct = ((set.reps - existing.bestReps).toDouble() / existing.bestReps) * 100
            return improvementPct >= MIN_PR_IMPROVEMENT_PERCENT
        }

        // Duration-based (timed exercises like plank): 20% improvement required
        if (set.durationSeconds != null && existing.bestDurationSeconds != null && existing.bestDurationSeconds > 0) {
            val improvementPct = ((set.durationSeconds - existing.bestDurationSeconds).toDouble() / existing.bestDurationSeconds) * 100
            return improvementPct >= MIN_PR_IMPROVEMENT_PERCENT
        }

        return false
    }

    // Called by the UI after showing the post-workout summary.
    fun consumeWorkoutResult() {
        _workoutResult.value = null
    }

    // ── WORKOUT RESULT ─────────────────────────────────────
    // Everything the post-workout summary screen needs to show.

    data class WorkoutResult(
        val totalXpEarned: Int,
        val levelsGained: List<Int>,
        val blockedByMilestone: LevelSystem.LevelUpResult.MilestoneNotMet?,
        val streakIncremented: Boolean,
        val newStreakDays: Int,
        val newPersonalRecords: List<PersonalRecord>,
        val questsCompleted: List<Quest>,
        val questXpEarned: Int,
        val questGoldEarned: Int,
        val achievementsUnlocked: List<String>,
        val achievementGoldEarned: Int
    ) {
        val totalGoldEarned: Int get() = questGoldEarned + achievementGoldEarned

        companion object {
            // Fallback for error cases — shows XP only, nothing else.
            fun empty(xp: Int) = WorkoutResult(
                totalXpEarned = xp,
                levelsGained = emptyList(),
                blockedByMilestone = null,
                streakIncremented = false,
                newStreakDays = 0,
                newPersonalRecords = emptyList(),
                questsCompleted = emptyList(),
                questXpEarned = 0,
                questGoldEarned = 0,
                achievementsUnlocked = emptyList(),
                achievementGoldEarned = 0
            )
        }
    }
}

// ============================================================
// Factory
// ============================================================

class WorkoutViewModelFactory(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val cardioRepository: CardioRepository,
    private val questRepository: QuestRepository,
    private val achievementRepository: AchievementRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            return WorkoutViewModel(
                userRepository = userRepository,
                workoutRepository = workoutRepository,
                cardioRepository = cardioRepository,
                questRepository = questRepository,
                achievementRepository = achievementRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

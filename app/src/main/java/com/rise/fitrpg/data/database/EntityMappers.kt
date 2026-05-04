package com.rise.fitrpg.data.database

import com.rise.fitrpg.data.models.CardioSession
import com.rise.fitrpg.data.models.ClassProgress
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.Gender
import com.rise.fitrpg.data.models.InventoryItem
import com.rise.fitrpg.data.models.ItemType
import com.rise.fitrpg.data.models.MuscleGroup
import com.rise.fitrpg.data.models.PersonalRecord
import com.rise.fitrpg.data.models.PlayerProfile
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.QuestRarity
import com.rise.fitrpg.data.models.QuestType
import com.rise.fitrpg.data.models.Stats
import com.rise.fitrpg.data.models.StreakTier
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.data.models.UserAchievement
import com.rise.fitrpg.data.models.WorkoutSession
import com.rise.fitrpg.data.models.WorkoutSet
import com.rise.fitrpg.data.models.WorkoutType

// ============================================================
// EntityMappers.kt
// Layer: Database
// ============================================================
// Converts between Room entities (database layer) and domain
// models (models layer). The systems layer never sees entities.
// The UI layer never sees entities. Only the Repository calls these.
//
// Convention:
//   toDomain()  — entity → domain model
//   toEntity()  — domain model → entity
// ============================================================


// ── USER ──────────────────────────────────────────────────────

fun UserEntity.toDomain(
    classMap: Map<FitnessClass, ClassProgress>,
    inventory: Map<ItemType, InventoryItem>
): User = User(
    id = id,
    name = name,
    email = email,
    createdAt = createdAt,
    currentClass = FitnessClass.valueOf(currentClass),
    streakTier = StreakTier.valueOf(streakTier),
    currentStreak = currentStreak,
    weeklyWorkoutCount = weeklyWorkoutCount,
    weekStartDate = weekStartDate,
    lastWorkoutDate = lastWorkoutDate,
    lastClassSwitch = lastClassSwitch,
    hasUsedFreeSwitch = hasUsedFreeSwitch,
    gold = gold,
    classesSwitchedTo = if (classesSwitchedTo.isBlank()) mutableSetOf()
        else classesSwitchedTo.split(",").map { FitnessClass.valueOf(it) }.toMutableSet(),
    stats = Stats(
        strength = statStrength,
        dexterity = statDexterity,
        endurance = statEndurance,
        flexibility = statFlexibility,
        power = statPower,
        focus = statFocus
    ),
    inventory = inventory.toMutableMap(),
    classMap = classMap.toMutableMap()
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    createdAt = createdAt,
    currentClass = currentClass.name,
    streakTier = streakTier.name,
    currentStreak = currentStreak,
    weeklyWorkoutCount = weeklyWorkoutCount,
    weekStartDate = weekStartDate,
    lastWorkoutDate = lastWorkoutDate,
    lastClassSwitch = lastClassSwitch,
    hasUsedFreeSwitch = hasUsedFreeSwitch,
    gold = gold,
    classesSwitchedTo = classesSwitchedTo.joinToString(",") { it.name },
    statStrength = stats.strength,
    statDexterity = stats.dexterity,
    statEndurance = stats.endurance,
    statFlexibility = stats.flexibility,
    statPower = stats.power,
    statFocus = stats.focus
)


// ── CLASS PROGRESS ────────────────────────────────────────────

fun ClassProgressEntity.toDomain(): ClassProgress = ClassProgress(
    fitnessClass = FitnessClass.valueOf(fitnessClass),
    xp = xp,
    totalXp = totalXp,
    level = level
)

fun ClassProgress.toEntity(userId: Int, entityId: Int = 0): ClassProgressEntity =
    ClassProgressEntity(
        id = entityId,
        userId = userId,
        fitnessClass = fitnessClass.name,
        xp = xp,
        totalXp = totalXp,
        level = level
    )


// ── PLAYER PROFILE ────────────────────────────────────────────

fun PlayerProfileEntity.toDomain(): PlayerProfile = PlayerProfile(
    userId = userId,
    gender = Gender.valueOf(gender),
    ageYears = ageYears,
    heightCm = heightCm,
    weightKg = weightKg
)

fun PlayerProfile.toEntity(): PlayerProfileEntity = PlayerProfileEntity(
    userId = userId,
    gender = gender.name,
    ageYears = ageYears,
    heightCm = heightCm,
    weightKg = weightKg
)


// ── WORKOUT SESSION ───────────────────────────────────────────

fun WorkoutSessionEntity.toDomain(sets: List<WorkoutSet>): WorkoutSession = WorkoutSession(
    id = id,
    userId = userId,
    date = date,
    fitnessClass = FitnessClass.valueOf(fitnessClass),
    workoutType = WorkoutType.valueOf(workoutType),
    sets = sets,
    totalXpEarned = totalXpEarned,
    durationSeconds = durationSeconds,
    notes = notes
)

fun WorkoutSession.toEntity(): WorkoutSessionEntity = WorkoutSessionEntity(
    id = id,
    userId = userId,
    date = date,
    fitnessClass = fitnessClass.name,
    workoutType = workoutType.name,
    totalXpEarned = totalXpEarned,
    durationSeconds = durationSeconds,
    notes = notes
)


// ── WORKOUT SET ───────────────────────────────────────────────

fun WorkoutSetEntity.toDomain(): WorkoutSet = WorkoutSet(
    id = id,
    exerciseId = exerciseId,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    durationSeconds = durationSeconds,
    xpEarned = xpEarned
)

fun WorkoutSet.toEntity(sessionId: Int): WorkoutSetEntity = WorkoutSetEntity(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    durationSeconds = durationSeconds,
    xpEarned = xpEarned
)


// ── CARDIO SESSION ────────────────────────────────────────────

fun CardioSessionEntity.toDomain(): CardioSession = CardioSession(
    id = id,
    userId = userId,
    cardioExerciseId = cardioExerciseId,
    date = date,
    distanceKm = distanceKm,
    durationMinutes = durationMinutes,
    sets = sets,
    metersPerSet = metersPerSet,
    xpEarned = xpEarned,
    notes = notes
)

fun CardioSession.toEntity(): CardioSessionEntity = CardioSessionEntity(
    id = id,
    userId = userId,
    cardioExerciseId = cardioExerciseId,
    date = date,
    distanceKm = distanceKm,
    durationMinutes = durationMinutes,
    sets = sets,
    metersPerSet = metersPerSet,
    xpEarned = xpEarned,
    notes = notes
)


// ── PERSONAL RECORD ───────────────────────────────────────────

fun PersonalRecordEntity.toDomain(): PersonalRecord = PersonalRecord(
    id = id,
    userId = userId,
    exerciseId = exerciseId,
    date = date,
    bestWeightKg = bestWeightKg,
    bestReps = bestReps,
    bestDurationSeconds = bestDurationSeconds
)

fun PersonalRecord.toEntity(): PersonalRecordEntity = PersonalRecordEntity(
    id = id,
    userId = userId,
    exerciseId = exerciseId,
    date = date,
    bestWeightKg = bestWeightKg,
    bestReps = bestReps,
    bestDurationSeconds = bestDurationSeconds
)


// ── QUEST ─────────────────────────────────────────────────────

fun QuestEntity.toDomain(): Quest = Quest(
    id = id,
    type = QuestType.valueOf(type),
    rarity = QuestRarity.valueOf(rarity),
    title = title,
    description = description,
    targetClass = FitnessClass.valueOf(targetClass),
    isBalanceQuest = isBalanceQuest,
    targetValue = targetValue,
    targetMuscleGroup = targetMuscleGroup?.let { MuscleGroup.valueOf(it) },
    currentProgress = currentProgress,
    isCompleted = isCompleted,
    xpReward = xpReward,
    goldReward = goldReward,
    weekStartMs = weekStartMs,
    expiryMs = expiryMs
)

fun Quest.toEntity(userId: Int): QuestEntity = QuestEntity(
    id = id,
    userId = userId,
    type = type.name,
    rarity = rarity.name,
    title = title,
    description = description,
    targetClass = targetClass.name,
    isBalanceQuest = isBalanceQuest,
    targetValue = targetValue,
    targetMuscleGroup = targetMuscleGroup?.name,
    currentProgress = currentProgress,
    isCompleted = isCompleted,
    xpReward = xpReward,
    goldReward = goldReward,
    weekStartMs = weekStartMs,
    expiryMs = expiryMs
)


// ── USER ACHIEVEMENT ──────────────────────────────────────────

fun UserAchievementEntity.toDomain(): UserAchievement = UserAchievement(
    achievementId = achievementId,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt,
    currentProgress = currentProgress,
    timesCompleted = timesCompleted
)

fun UserAchievement.toEntity(): UserAchievementEntity = UserAchievementEntity(
    achievementId = achievementId,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt,
    currentProgress = currentProgress,
    timesCompleted = timesCompleted
)


// ── INVENTORY ─────────────────────────────────────────────────

fun InventoryEntity.toDomain(): InventoryItem = InventoryItem(
    itemType = ItemType.valueOf(itemType),
    quantity = quantity
)

fun InventoryItem.toEntity(userId: Int, entityId: Int = 0): InventoryEntity = InventoryEntity(
    id = entityId,
    userId = userId,
    itemType = itemType.name,
    quantity = quantity
)

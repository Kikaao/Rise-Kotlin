package com.rise.fitrpg.data.models

object CardioExerciseLibrary {

    // ─────────────────────────────────────────────────────
    // SHARED PACE THRESHOLDS (reused across similar exercises)
    // ─────────────────────────────────────────────────────

    private val runningPaceThresholds = listOf(
        PaceThreshold(4.0, PaceTier.ELITE),           // < 4 min/km  → 15+ km/h
        PaceThreshold(6.0, PaceTier.FAST),             // 4–6 min/km  → 10–15 km/h
        PaceThreshold(8.0, PaceTier.INTERMEDIATE),     // 6–8 min/km  → 7.5–10 km/h
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW) // > 8 min/km  → below 7.5 km/h
    )

    private val cyclingPaceThresholds = listOf(
        PaceThreshold(1.5, PaceTier.ELITE),            // < 1.5 min/km → 40+ km/h
        PaceThreshold(2.5, PaceTier.FAST),             // 1.5–2.5 min/km → 24–40 km/h
        PaceThreshold(4.0, PaceTier.INTERMEDIATE),     // 2.5–4 min/km → 15–24 km/h
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW) // > 4 min/km → below 15 km/h
    )

    private val rowingPaceThresholds = listOf(
        PaceThreshold(3.5, PaceTier.ELITE),            // < 3.5 min/km → 1:45/500m
        PaceThreshold(4.5, PaceTier.FAST),             // 3.5–4.5 min/km → 1:45–2:15/500m
        PaceThreshold(6.0, PaceTier.INTERMEDIATE),     // 4.5–6 min/km → 2:15–3:00/500m
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW) // > 6 min/km → below 3:00/500m
    )

    private val swimmingPaceThresholds = listOf(
        PaceThreshold(16.0, PaceTier.ELITE),           // < 16 min/km → sub 1:36/100m
        PaceThreshold(22.0, PaceTier.FAST),            // 16–22 min/km → 1:36–2:12/100m
        PaceThreshold(30.0, PaceTier.INTERMEDIATE),    // 22–30 min/km → 2:12–3:00/100m
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW) // > 30 min/km → above 3:00/100m
    )

    private val ellipticalPaceThresholds = listOf(
        PaceThreshold(4.5, PaceTier.ELITE),
        PaceThreshold(7.0, PaceTier.FAST),
        PaceThreshold(9.0, PaceTier.INTERMEDIATE),
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW)
    )

    private val walkingPaceThresholds = listOf(
        PaceThreshold(8.0, PaceTier.ELITE),            // < 8 min/km → power walking
        PaceThreshold(11.0, PaceTier.FAST),            // 8–11 min/km → brisk walk
        PaceThreshold(14.0, PaceTier.INTERMEDIATE),    // 11–14 min/km → normal walk
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW) // > 14 min/km → slow stroll
    )

    private val assaultBikePaceThresholds = listOf(
        PaceThreshold(1.2, PaceTier.ELITE),            // very high output
        PaceThreshold(2.0, PaceTier.FAST),
        PaceThreshold(3.0, PaceTier.INTERMEDIATE),
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW)
    )

    private val skiErgPaceThresholds = listOf(
        PaceThreshold(3.0, PaceTier.ELITE),
        PaceThreshold(4.0, PaceTier.FAST),
        PaceThreshold(5.5, PaceTier.INTERMEDIATE),
        PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW)
    )

    // ─────────────────────────────────────────────────────
    // DISTANCE BASED EXERCISES (id 221–230)
    // ─────────────────────────────────────────────────────

    val outdoorRunning = CardioExercise(
        id = 221,
        name = "Outdoor Running",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Running outdoors on roads, trails, or tracks. Distance and optional pace tracked for XP calculation.",
        equipmentType = EquipmentType.BODYWEIGHT,
        statContribution = StatContribution(endurance = 0.7, dexterity = 0.2, power = 0.1),
        baseXpPerKm = 30.0,
        paceThresholds = runningPaceThresholds,
        minDistanceForBonusKm = 0.5,
        maxRealisticPaceMinPerKm = 2.5   // sub-2:30/km is world record territory
    )

    val treadmillRunning = CardioExercise(
        id = 222,
        name = "Treadmill Running",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Running on a treadmill. Slightly easier than outdoor running due to the moving belt — same XP formula.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.7, dexterity = 0.1, power = 0.2),
        baseXpPerKm = 30.0,
        paceThresholds = runningPaceThresholds,
        minDistanceForBonusKm = 0.5,
        maxRealisticPaceMinPerKm = 2.5
    )

    val outdoorCycling = CardioExercise(
        id = 223,
        name = "Outdoor Cycling",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Cycling outdoors on roads or trails. Naturally higher speeds than running so pace thresholds are adjusted accordingly.",
        equipmentType = EquipmentType.OUTDOOR,
        statContribution = StatContribution(endurance = 0.7, dexterity = 0.2, power = 0.1),
        baseXpPerKm = 15.0,   // lower per-km XP since distances are much larger than running
        paceThresholds = cyclingPaceThresholds,
        minDistanceForBonusKm = 2.0,
        maxRealisticPaceMinPerKm = 1.0   // 60 km/h — realistic cap
    )

    val stationaryBike = CardioExercise(
        id = 224,
        name = "Stationary Bike",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Cycling on a stationary or spin bike. No wind resistance makes it slightly easier than outdoor cycling.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.8, power = 0.1, dexterity = 0.1),
        baseXpPerKm = 15.0,
        paceThresholds = listOf(
            PaceThreshold(1.2, PaceTier.ELITE),            // easier than outdoor → slightly lower threshold for elite
            PaceThreshold(2.0, PaceTier.FAST),
            PaceThreshold(3.5, PaceTier.INTERMEDIATE),
            PaceThreshold(Double.MAX_VALUE, PaceTier.SLOW)
        ),
        minDistanceForBonusKm = 2.0,
        maxRealisticPaceMinPerKm = 0.8
    )

    val rowingMachine = CardioExercise(
        id = 225,
        name = "Rowing Machine",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Indoor rowing ergometer. Full body cardio engaging legs, back, and arms. Log distance in km and optional time.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.6, strength = 0.2, power = 0.2),
        baseXpPerKm = 36.0,  // harder per km than running due to full body demand
        paceThresholds = rowingPaceThresholds,
        minDistanceForBonusKm = 0.5,
        maxRealisticPaceMinPerKm = 3.0   // 2:00/500m split is elite level
    )

    val swimming = CardioExercise(
        id = 226,
        name = "Swimming",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Pool or open water swimming. Log distance in km. Pace thresholds are based on min/km (1km = 10 × 100m).",
        equipmentType = EquipmentType.OUTDOOR,
        statContribution = StatContribution(endurance = 0.6, flexibility = 0.2, dexterity = 0.2),
        baseXpPerKm = 45.0,  // highest per-km XP — water resistance makes it hardest per km
        paceThresholds = swimmingPaceThresholds,
        minDistanceForBonusKm = 0.2,
        maxRealisticPaceMinPerKm = 12.0  // sub 1:12/100m is world record level
    )

    val elliptical = CardioExercise(
        id = 227,
        name = "Elliptical",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Low impact full-body cardio machine. Distance is the machine's displayed km readout.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.8, dexterity = 0.1, power = 0.1),
        baseXpPerKm = 21.0,
        paceThresholds = ellipticalPaceThresholds,
        minDistanceForBonusKm = 1.0,
        maxRealisticPaceMinPerKm = 3.0
    )

    val walking = CardioExercise(
        id = 228,
        name = "Walking",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Outdoor or treadmill walking. Lower intensity than running but still valuable for endurance and consistency.",
        equipmentType = EquipmentType.BODYWEIGHT,
        statContribution = StatContribution(endurance = 0.8, focus = 0.1, dexterity = 0.1),
        baseXpPerKm = 12.0,   // lower base since lower intensity
        paceThresholds = walkingPaceThresholds,
        minDistanceForBonusKm = 1.0,
        maxRealisticPaceMinPerKm = 6.0   // sub-6 min/km is jogging not walking
    )

    val assaultBike = CardioExercise(
        id = 229,
        name = "Assault Bike (Air Bike)",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Fan resistance bike using both arms and legs simultaneously. One of the most brutal cardio machines — resistance increases with effort.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.5, power = 0.4, strength = 0.1),
        baseXpPerKm = 24.0,
        paceThresholds = assaultBikePaceThresholds,
        minDistanceForBonusKm = 0.5,
        maxRealisticPaceMinPerKm = 0.8
    )

    val skiErg = CardioExercise(
        id = 230,
        name = "Ski Erg",
        type = CardioExerciseType.DISTANCE_BASED,
        description = "Standing cable pull machine simulating the motion of Nordic skiing. Engages the full upper body and core with each stroke.",
        equipmentType = EquipmentType.CARDIO_MACHINE,
        statContribution = StatContribution(endurance = 0.6, strength = 0.2, power = 0.2),
        baseXpPerKm = 33.0,
        paceThresholds = skiErgPaceThresholds,
        minDistanceForBonusKm = 0.5,
        maxRealisticPaceMinPerKm = 2.5
    )

    // ─────────────────────────────────────────────────────
    // INTERVAL BASED EXERCISES (id 231–232)
    // These also exist in ExerciseLibrary as dual-mode exercises.
    // These entries are used specifically when the user logs them
    // as a cardio session (not as a strength set).
    // ─────────────────────────────────────────────────────

    val jumpRopeCardio = CardioExercise(
        id = 231,
        name = "Jump Rope",
        type = CardioExerciseType.INTERVAL_BASED,
        description = "Jump rope logged as a cardio session — user logs sets × number of jumps or sets × meters. Higher volume = more XP.",
        equipmentType = EquipmentType.HOME,
        statContribution = StatContribution(endurance = 0.5, dexterity = 0.3, power = 0.2),
        baseXpPer10Meters = 3.0
    )

    val sprintingCardio = CardioExercise(
        id = 232,
        name = "Sprinting",
        type = CardioExerciseType.INTERVAL_BASED,
        description = "Sprinting logged as a cardio session — user logs sets × meters per sprint. Higher intensity per meter than jump rope.",
        equipmentType = EquipmentType.BODYWEIGHT,
        statContribution = StatContribution(power = 0.5, endurance = 0.3, dexterity = 0.2),
        baseXpPer10Meters = 6.0
    )

    // ─────────────────────────────────────────────────────
    // ALL CARDIO EXERCISES LIST
    // ─────────────────────────────────────────────────────

    val all: List<CardioExercise> = listOf(
        outdoorRunning, treadmillRunning, outdoorCycling, stationaryBike,
        rowingMachine, swimming, elliptical, walking, assaultBike, skiErg,
        jumpRopeCardio, sprintingCardio
    )

    // ─────────────────────────────────────────────────────
    // HELPER FUNCTIONS
    // ─────────────────────────────────────────────────────

    fun getById(id: Int) = all.find { it.id == id }
    fun getByType(type: CardioExerciseType) = all.filter { it.type == type }
    fun getDistanceBased() = all.filter { it.type == CardioExerciseType.DISTANCE_BASED }
    fun getIntervalBased() = all.filter { it.type == CardioExerciseType.INTERVAL_BASED }
}

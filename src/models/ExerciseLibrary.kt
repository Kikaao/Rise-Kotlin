package models

object ExerciseLibrary {

    // ─────────────────────────────────────────────────────
    // CHEST (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellBenchPress = Exercise(
        id = 1, name = "Barbell Bench Press",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 50,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "The king of chest exercises. Lie on a flat bench and press a barbell from chest to lockout, targeting the entire pec."
    )
    val inclineBarbellPress = Exercise(
        id = 2, name = "Incline Barbell Press",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "Like the bench press but at a 30–45° incline, shifting emphasis to the upper chest and anterior deltoid."
    )
    val declineBarbellPress = Exercise(
        id = 3, name = "Decline Barbell Press",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "Performed on a decline bench to target the lower chest fibers and sternal head of the pectoralis major."
    )
    val dumbbellBenchPress = Exercise(
        id = 4, name = "Dumbbell Bench Press",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.7, power = 0.2, dexterity = 0.1),
        description = "Greater range of motion than barbell. Helps correct muscle imbalances."
    )
    val inclineDumbbellPress = Exercise(
        id = 5, name = "Incline Dumbbell Press",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 44,
        statContribution = StatContribution(strength = 0.7, power = 0.2, dexterity = 0.1),
        description = "Pressing dumbbells from an incline bench allows a greater range of motion than a barbell for upper chest development."
    )
    val cableCrossover = Exercise(
        id = 6, name = "Cable Crossover",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, dexterity = 0.2),
        description = "Pull handles from high or low positions toward the center, providing constant tension through the movement."
    )
    val pecDeck = Exercise(
        id = 7, name = "Pec Deck (Machine Fly)",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "A machine-based isolation exercise that mimics the fly motion, excellent for beginners and pre-exhausting the chest."
    )
    val chestPressMachine = Exercise(
        id = 8, name = "Chest Press Machine",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, power = 0.1),
        description = "A guided machine press ideal for safely loading the chest, useful for warm-ups or high-rep finishers."
    )
    val lowCableFly = Exercise(
        id = 9, name = "Low Cable Fly",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, dexterity = 0.2),
        description = "Cables set at the bottom position emphasize the upper chest and give a unique stretch at the bottom."
    )
    val dumbbellPullover = Exercise(
        id = 10, name = "Dumbbell Pullover",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.3, dexterity = 0.1),
        description = "Lying on a bench, lower a dumbbell over your head in an arc. Engages the chest and lats through a long range of motion."
    )
    val standardPushUp = Exercise(
        id = 11, name = "Standard Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, power = 0.2),
        description = "The foundational chest bodyweight exercise. Hands shoulder-width apart, lower chest to floor and push back up."
    )
    val wideGripPushUp = Exercise(
        id = 12, name = "Wide-Grip Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 26,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, power = 0.1),
        description = "Hands placed wider than shoulder-width to increase pectoral activation and reduce tricep involvement."
    )
    val declinePushUp = Exercise(
        id = 13, name = "Decline Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, endurance = 0.2, power = 0.2),
        description = "Feet elevated on a surface, body angled downward to target the upper chest and front delts."
    )
    val inclinePushUp = Exercise(
        id = 14, name = "Incline Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, power = 0.2),
        description = "Hands on an elevated surface, targets the lower chest. Easier variation good for beginners."
    )
    val archerPushUp = Exercise(
        id = 15, name = "Archer Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, power = 0.1),
        description = "One arm extends to the side as you lower toward the other, shifting almost all load to a single pec at a time."
    )
    val plyometricPushUp = Exercise(
        id = 16, name = "Plyometric Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(power = 0.6, strength = 0.3, dexterity = 0.1),
        description = "Explosive push-up where hands leave the floor. Develops power and fast-twitch fibers in the chest."
    )
    val pikePushUp = Exercise(
        id = 17, name = "Pike Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Hips raised high in an inverted V position, more shoulder-focused but loads upper chest too."
    )
    val chestDipBodyweight = Exercise(
        id = 18, name = "Chest Dip",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, power = 0.2, endurance = 0.1),
        description = "On parallel bars, lean forward with a slight bend to load the lower chest deeply."
    )
    val pseudoPlanchePushUp = Exercise(
        id = 19, name = "Pseudo Planche Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 45,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, power = 0.1),
        description = "Hands rotated outward near hips, body leaning forward to increase chest and shoulder loading significantly."
    )
    val diamondPushUpChest = Exercise(
        id = 20, name = "Diamond Push-Up",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 32,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, power = 0.2),
        description = "Hands form a diamond under your chest, shifting focus toward the inner chest and triceps."
    )

    // ─────────────────────────────────────────────────────
    // BACK (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellDeadlift = Exercise(
        id = 21, name = "Barbell Deadlift",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 70,
        statContribution = StatContribution(strength = 0.9, power = 0.1),
        description = "A full posterior-chain compound lift. Pull a loaded barbell from the floor to hip height, engaging lats, traps, and erectors."
    )
    val bentOverBarbellRow = Exercise(
        id = 22, name = "Bent-Over Barbell Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.LOWER_BACK, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 55,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "Hinge at the hips and row a barbell toward your lower chest. Excellent mass builder for the entire back."
    )
    val latPulldown = Exercise(
        id = 23, name = "Lat Pulldown",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 42,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, flexibility = 0.1),
        description = "Seated at a cable machine, pull a bar down to your upper chest to develop lat width and improve pull-up strength."
    )
    val seatedCableRow = Exercise(
        id = 24, name = "Seated Cable Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.REAR_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 42,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, flexibility = 0.1),
        description = "Pull a cable handle toward your abdomen while seated, targeting the mid-back, rhomboids, and rear delts."
    )
    val tBarRow = Exercise(
        id = 25, name = "T-Bar Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 50,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "A landmine or T-bar machine row that allows heavy loading with a neutral grip, hitting the mid and lower lats."
    )
    val singleArmDumbbellRow = Exercise(
        id = 26, name = "Single-Arm Dumbbell Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.REAR_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, endurance = 0.1),
        description = "One knee and hand braced on a bench, row a dumbbell to your hip. Great for unilateral back development."
    )
    val facePull = Exercise(
        id = 27, name = "Face Pull",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.5, flexibility = 0.3, dexterity = 0.2),
        description = "Pull a rope attachment toward your face at eye level to strengthen the rear delts and external rotators."
    )
    val machineRow = Exercise(
        id = 28, name = "Machine Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS, MuscleGroup.BICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "A chest-supported or seated row machine that isolates the back with reduced spinal load."
    )
    val straightArmCablePulldown = Exercise(
        id = 29, name = "Straight-Arm Cable Pulldown",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 36,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, dexterity = 0.2),
        description = "Arms straight, pull a cable bar from overhead down to your thighs. Isolates the lats like a standing fly."
    )
    val rackPull = Exercise(
        id = 30, name = "Rack Pull",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 60,
        statContribution = StatContribution(strength = 0.9, power = 0.1),
        description = "A partial deadlift from knee height, allowing heavier loading of the traps, upper back, and erector spinae."
    )
    val pullUp = Exercise(
        id = 31, name = "Pull-Up",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 45,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, power = 0.1),
        description = "Hang from a bar with an overhand grip and pull your chin above the bar. The gold standard for lat development."
    )
    val invertedRow = Exercise(
        id = 32, name = "Inverted Row",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.REAR_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Lie under a bar or table and pull your chest up to it. Mimics a horizontal row with bodyweight."
    )
    val neutralGripPullUp = Exercise(
        id = 33, name = "Neutral-Grip Pull-Up",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 44,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "Hands facing each other on parallel handles, reduces shoulder strain while building lats."
    )
    val scapularPullUp = Exercise(
        id = 34, name = "Scapular Pull-Up",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, flexibility = 0.2),
        description = "Hang from a bar and raise your body only by depressing and retracting the scapulae. Builds scapular control."
    )
    val archerPullUp = Exercise(
        id = 35, name = "Archer Pull-Up",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 52,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "One arm pulls while the other extends out to the side, shifting load to one lat at a time for unilateral strength."
    )
    val deadHang = Exercise(
        id = 36, name = "Dead Hang",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, focus = 0.2),
        description = "Simply hanging from a bar builds grip strength, decompresses the spine, and strengthens the shoulder girdle."
    )
    val supermanHold = Exercise(
        id = 37, name = "Superman Hold",
        muscleGroup = MuscleGroup.LOWER_BACK,
        primaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 18,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, flexibility = 0.2),
        description = "Lying prone, raise arms and legs off the floor simultaneously to engage the erector spinae and lower back."
    )
    val negativePullUp = Exercise(
        id = 38, name = "Negative Pull-Up",
        muscleGroup = MuscleGroup.LATS,
        primaryMuscles = listOf(MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "Jump or step to the top position and lower yourself as slowly as possible. Builds eccentric back strength."
    )
    val towelRow = Exercise(
        id = 39, name = "Towel Row",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.BICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Loop a towel around a pole and sit back, then row your body toward it. Good at-home horizontal pull variation."
    )
    val towelPullUp = Exercise(
        id = 40, name = "Towel Pull-Up",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 48,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, endurance = 0.1),
        description = "Drape a towel over a bar and grip the ends to do pull-ups. The thicker grip dramatically increases forearm demand."
    )

    // ─────────────────────────────────────────────────────
    // SHOULDERS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellOverheadPress = Exercise(
        id = 41, name = "Barbell Overhead Press",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 52,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "Press a barbell from shoulder height to full arm extension overhead. The primary mass builder for all three delt heads."
    )
    val dumbbellShoulderPress = Exercise(
        id = 42, name = "Dumbbell Shoulder Press",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 46,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "Seated or standing, press dumbbells overhead. Independent arms allow a natural movement path and greater range."
    )
    val lateralRaise = Exercise(
        id = 43, name = "Lateral Raise",
        muscleGroup = MuscleGroup.SIDE_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Raise dumbbells to the side until arms are parallel to the floor to isolate the lateral deltoid head."
    )
    val frontRaise = Exercise(
        id = 44, name = "Front Raise",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Raise a dumbbell or plate in front of you to shoulder height, targeting the anterior deltoid."
    )
    val rearDeltFly = Exercise(
        id = 45, name = "Rear Delt Fly",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, flexibility = 0.3, dexterity = 0.2),
        description = "Bent over or on an incline bench, raise dumbbells outward to hit the posterior deltoid and upper back."
    )
    val arnoldPress = Exercise(
        id = 46, name = "Arnold Press",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.REAR_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 44,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, power = 0.1),
        description = "A dumbbell press starting with palms facing you, rotating outward as you press — hitting all three delt heads."
    )
    val cableLateralRaise = Exercise(
        id = 47, name = "Cable Lateral Raise",
        muscleGroup = MuscleGroup.SIDE_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "A cable version of the lateral raise providing constant tension throughout the range, superior for hypertrophy."
    )
    val machineShoulderPress = Exercise(
        id = 48, name = "Machine Shoulder Press",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, power = 0.1),
        description = "A guided press machine that supports the back and allows safe heavy pressing, great for volume work."
    )
    val uprightRow = Exercise(
        id = 49, name = "Upright Row",
        muscleGroup = MuscleGroup.SIDE_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, endurance = 0.2, dexterity = 0.2),
        description = "Pull a barbell or dumbbells up along your torso to chin height, engaging the lateral delts and upper traps."
    )
    val shoulderFacePull = Exercise(
        id = 50, name = "Shoulder Face Pull",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.SIDE_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, flexibility = 0.3, dexterity = 0.2),
        description = "Pull a rope toward your face at upper-chest height, directly targeting the rear delt and rotator cuff."
    )
    val handstandPushUp = Exercise(
        id = 51, name = "Handstand Push-Up",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 60,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "Inverted against a wall, bend the elbows and press back up. A demanding overhead press for advanced athletes."
    )
    val wallWalk = Exercise(
        id = 52, name = "Wall Walk",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, focus = 0.2),
        description = "Walk your hands toward a wall from a push-up position until vertical. Builds shoulder stability and control."
    )
    val shoulderTapPushUp = Exercise(
        id = 53, name = "Shoulder Tap Push-Up",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.CHEST),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Standard push-up where you tap the opposite shoulder at the top, engaging stabilizers and anti-rotation core."
    )
    val plankShoulderTap = Exercise(
        id = 54, name = "Plank Shoulder Tap",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.3, dexterity = 0.4, endurance = 0.3),
        description = "In a plank position, alternate tapping each shoulder while keeping hips still. Strengthens the shoulder girdle."
    )
    val crabWalk = Exercise(
        id = 55, name = "Crab Walk",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.GLUTES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 22,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "On hands and feet belly up, walk forward and backward. Activates rear delts and shoulder stabilizers."
    )
    val ytwOnFloor = Exercise(
        id = 56, name = "YTW on Floor",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(strength = 0.3, flexibility = 0.4, dexterity = 0.3),
        description = "Prone on the floor, form Y, T, and W shapes with your arms to activate all portions of the rear deltoid."
    )
    val bearCrawl = Exercise(
        id = 57, name = "Bear Crawl",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.QUADS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 25,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "On all fours with knees hovering, crawl forward and backward. Demands constant shoulder stability throughout."
    )
    val elevatedPikePushUp = Exercise(
        id = 58, name = "Elevated Pike Push-Up",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, power = 0.2),
        description = "Feet elevated on a surface for a pike push-up to increase overhead demand, bridging toward handstand push-ups."
    )
    val bodyweightTRaise = Exercise(
        id = 59, name = "Bodyweight T-Raise",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, flexibility = 0.2),
        description = "In a push-up position, rotate to one side and raise your top arm overhead, loading the rear and lateral delt."
    )
    val pikePushUpShoulder = Exercise(
        id = 60, name = "Pike Push-Up (Shoulder Focus)",
        muscleGroup = MuscleGroup.FRONT_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS, MuscleGroup.SIDE_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Hips raised in an inverted V, lower your head toward the floor and press up. The bodyweight overhead press."
    )

    // ─────────────────────────────────────────────────────
    // BICEPS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellCurl = Exercise(
        id = 61, name = "Barbell Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, power = 0.1),
        description = "The classic mass-building bicep exercise. Stand and curl a barbell from waist to shoulder height."
    )
    val dumbbellCurl = Exercise(
        id = 62, name = "Dumbbell Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 36,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Alternate or simultaneous dumbbell curls allow independent arm work and natural supination through the movement."
    )
    val hammerCurl = Exercise(
        id = 63, name = "Hammer Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 36,
        statContribution = StatContribution(strength = 0.6, endurance = 0.2, dexterity = 0.2),
        description = "Neutral grip targets the brachialis and brachioradialis alongside the bicep for arm thickness."
    )
    val preacherCurl = Exercise(
        id = 64, name = "Preacher Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Arms braced on the angled pad of a preacher bench, isolates the biceps and eliminates body swinging."
    )
    val inclineDumbbellCurl = Exercise(
        id = 65, name = "Incline Dumbbell Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "Lying back on an incline bench, the arms hang behind the body for a long-head stretch at the bottom of each rep."
    )
    val cableCurl = Exercise(
        id = 66, name = "Cable Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 34,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Curl with a cable machine for constant tension throughout the entire range of motion."
    )
    val concentrationCurl = Exercise(
        id = 67, name = "Concentration Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Seated, brace elbow against inner thigh and curl a dumbbell. Maximum isolation of the bicep peak."
    )
    val ezBarCurl = Exercise(
        id = 68, name = "EZ-Bar Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.EZ_BAR,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "A slightly angled bar reduces wrist strain while still heavily loading the biceps, great for volume sets."
    )
    val spiderCurl = Exercise(
        id = 69, name = "Spider Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Lie chest-down on an incline bench and curl dumbbells from a straight arm — constant tension on the bicep."
    )
    val highCableCurl = Exercise(
        id = 70, name = "High Cable Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Cables at shoulder height, curl toward your ear. Isolates the short head in a double bicep pose position."
    )
    val chinUp = Exercise(
        id = 71, name = "Chin-Up",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 44,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, power = 0.1),
        description = "The best bodyweight bicep exercise. Underhand grip pull-up where the biceps are primary movers along with the lats."
    )
    val invertedUnderhangRow = Exercise(
        id = 72, name = "Inverted Underhand Row",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Under a bar with a supinated grip, row your chest up to it. Places greater demand on the biceps than a standard row."
    )
    val towelCurl = Exercise(
        id = 73, name = "Towel Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Loop a towel around a door handle or post and curl your bodyweight using it — a makeshift home curl."
    )
    val australianChinUp = Exercise(
        id = 74, name = "Australian Chin-Up",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "A horizontal chin-up from a low bar with a supinated grip, loading the biceps with a simpler entry point."
    )
    val flexedArmHang = Exercise(
        id = 75, name = "Flexed Arm Hang",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, focus = 0.1),
        description = "Hold the top of a pull-up position with a supinated grip. Builds bicep isometric strength and grip endurance."
    )
    val negativeChinUp = Exercise(
        id = 76, name = "Negative Chin-Up",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "Jump to the top chin-up position and lower as slowly as possible. Heavy eccentric load on the biceps."
    )
    val archerChinUp = Exercise(
        id = 77, name = "Archer Chin-Up",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 50,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "One arm pulls while the other extends laterally, isolating one bicep at a time through the movement."
    )
    val closeGripChinUp = Exercise(
        id = 78, name = "Close-Grip Chin-Up",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 42,
        statContribution = StatContribution(strength = 0.7, endurance = 0.2, dexterity = 0.1),
        description = "Hands close together on the bar with supinated grip — increases bicep peak activation vs wider grip."
    )
    val ringCurl = Exercise(
        id = 79, name = "Ring Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.RINGS,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, endurance = 0.1),
        description = "Using gymnastic rings, perform a curl from an inverted position. The rings allow a natural forearm path."
    )
    val bodyweightFootAssistedCurl = Exercise(
        id = 80, name = "Bodyweight Foot-Assisted Curl",
        muscleGroup = MuscleGroup.BICEPS,
        primaryMuscles = listOf(MuscleGroup.BICEPS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Sitting, wrap a towel under your feet and curl your body forward, pulling with the biceps against leg resistance."
    )

    // ─────────────────────────────────────────────────────
    // TRICEPS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val closeGripBenchPress = Exercise(
        id = 81, name = "Close-Grip Bench Press",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "Barbell bench press with a narrow grip, shifting most load from chest to triceps."
    )
    val skullCrusher = Exercise(
        id = 82, name = "Skullcrusher",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.EZ_BAR,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, endurance = 0.1),
        description = "Lying on a bench, lower an EZ-bar toward your forehead and extend it back up. Direct isolation of all three tricep heads."
    )
    val tricepPushdown = Exercise(
        id = 83, name = "Tricep Pushdown",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Push a cable bar or rope downward against resistance, isolating the triceps through a full ROM."
    )
    val overheadTricepExtension = Exercise(
        id = 84, name = "Overhead Tricep Extension",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "Arms overhead, lower a dumbbell or cable behind your head to fully stretch the long head of the triceps."
    )
    val tricepKickback = Exercise(
        id = 85, name = "Tricep Kickback",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Hinged over, extend a dumbbell behind you at hip height. Excellent for squeezing and isolating the lateral head."
    )
    val machineTricepPress = Exercise(
        id = 86, name = "Machine Tricep Press",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "A guided press machine targeting the triceps in a similar pattern to a dip, useful for high-rep volume."
    )
    val ropePushdown = Exercise(
        id = 87, name = "Rope Pushdown",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 33,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "The rope attachment allows you to flare the hands at the bottom for a stronger tricep contraction."
    )
    val jmPress = Exercise(
        id = 88, name = "JM Press",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "A hybrid between a bench press and skullcrusher, using a barbell with a partial ROM for heavy tricep loading."
    )
    val seatedOverheadDumbbellExtension = Exercise(
        id = 89, name = "Seated Overhead Dumbbell Extension",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "One or two dumbbells held overhead, lower behind the head and press up — excellent for the long head."
    )
    val tatePress = Exercise(
        id = 90, name = "Tate Press",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Lying on a bench, flare elbows out and press dumbbells in a semicircle to target the medial tricep head."
    )
    val tricepDipBench = Exercise(
        id = 91, name = "Tricep Dip (Bench)",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, power = 0.1),
        description = "Hands on a bench behind you, lower your body by bending the elbows. A fundamental bodyweight tricep movement."
    )
    val parallelBarDip = Exercise(
        id = 92, name = "Parallel Bar Dip",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 42,
        statContribution = StatContribution(strength = 0.7, power = 0.2, endurance = 0.1),
        description = "Body upright on parallel bars, lower and press using primarily the triceps — one of the best mass builders."
    )
    val bodyweightSkullcrusher = Exercise(
        id = 93, name = "Bodyweight Skullcrusher",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Hands on a ledge at head height, lean in and lower your head toward your hands, then extend."
    )
    val diamondPushUpTricep = Exercise(
        id = 94, name = "Diamond Push-Up (Tricep Focus)",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, power = 0.2),
        description = "Hands in a diamond shape under the chest — the closer the hands, the more tricep dominant the movement."
    )
    val clappingPushUpBehind = Exercise(
        id = 95, name = "Clapping Push-Up (Behind Back)",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 55,
        statContribution = StatContribution(power = 0.6, strength = 0.3, dexterity = 0.1),
        description = "An explosive push-up where hands briefly meet behind the back at the top — extreme tricep strength required."
    )
    val narrowGripWallPushUp = Exercise(
        id = 96, name = "Narrow-Grip Wall Push-Up",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 16,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, dexterity = 0.2),
        description = "Leaning against a wall with a diamond/narrow hand position for an accessible tricep variation."
    )
    val handstandPushUpElbowsTucked = Exercise(
        id = 97, name = "Handstand Push-Up (Elbows Tucked)",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 58,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "Keeping elbows tracking back during a handstand push-up increases tricep involvement over shoulders."
    )
    val ringDip = Exercise(
        id = 98, name = "Ring Dip",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.RINGS,
        trackingType = TrackingType.REPS_ONLY, baseXp = 50,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, power = 0.1),
        description = "Dips on gymnastic rings are significantly harder than bar dips due to instability, demanding greater tricep activation."
    )
    val pikePushUpTricep = Exercise(
        id = 99, name = "Pike Push-Up (Tricep Focus)",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "A pike push-up with elbows flaring back (not out) places more emphasis on the triceps than the shoulders."
    )
    val closeKnucklePushUp = Exercise(
        id = 100, name = "Close Knuckle Push-Up",
        muscleGroup = MuscleGroup.TRICEPS,
        primaryMuscles = listOf(MuscleGroup.TRICEPS),
        secondaryMuscles = listOf(MuscleGroup.CHEST),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Performing a push-up on knuckles with narrow hand placement for additional tricep recruitment."
    )

    // ─────────────────────────────────────────────────────
    // FOREARMS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val wristCurl = Exercise(
        id = 101, name = "Wrist Curl",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 20,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Rest forearms on a bench and curl a dumbbell or barbell upward using only wrist flexion. Builds the forearm flexors."
    )
    val reverseWristCurl = Exercise(
        id = 102, name = "Reverse Wrist Curl",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 20,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Same position but with overhand grip, extending the wrist upward. Trains the forearm extensors."
    )
    val reverseBarbellCurl = Exercise(
        id = 103, name = "Reverse Barbell Curl",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.BICEPS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "A standard bicep curl with a pronated grip, heavily taxing the brachioradialis and forearm extensors."
    )
    val farmersCarry = Exercise(
        id = 104, name = "Farmer's Carry",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.TIME, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Walk while holding heavy dumbbells at your sides. Excellent for grip and forearm endurance and strength."
    )
    val platePinch = Exercise(
        id = 105, name = "Plate Pinch",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.TIME, baseXp = 22,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Pinch two weight plates together with fingertips and hold for time. Develops crushing and pinch grip strength."
    )
    val barbellWristRoll = Exercise(
        id = 106, name = "Barbell Wrist Roll",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Hold a barbell in front of you and roll it toward and away by rotating the wrists, working both flexors and extensors."
    )
    val cableWristCurl = Exercise(
        id = 107, name = "Cable Wrist Curl",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 20,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Using a cable machine at ground level, perform wrist curls for constant tension through wrist flexion."
    )
    val deadHangVariations = Exercise(
        id = 108, name = "Dead Hang Variations",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 22,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, focus = 0.1),
        description = "Hanging from different grip positions (overhand, underhand, towel, one arm) all challenge the forearm and grip."
    )
    val sledgehammerRotation = Exercise(
        id = 109, name = "Sledgehammer Rotation",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.SIDE_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "Hold a hammer or mace at the base and rotate the wrist to lower and raise the head slowly — great for wrist stability."
    )
    val towelPullUpForearm = Exercise(
        id = 110, name = "Towel Pull-Up (Forearm Focus)",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.BICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 45,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Drape a towel over a bar and grip the ends to do pull-ups. The thicker grip dramatically increases forearm demand."
    )
    val fingerPushUp = Exercise(
        id = 111, name = "Finger Push-Up",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "Push-ups performed on fingertips, loading the finger flexors and developing grip and forearm endurance."
    )
    val oneArmDeadHang = Exercise(
        id = 112, name = "One-Arm Dead Hang",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, focus = 0.1),
        description = "Hanging from one hand at a time dramatically increases grip demand and wrist stabilization requirements."
    )
    val wristRotationPushUp = Exercise(
        id = 113, name = "Wrist Rotation Push-Up",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(dexterity = 0.5, flexibility = 0.3, strength = 0.2),
        description = "In a push-up position, perform small circles with the wrists to activate the stabilizer muscles around the wrist joint."
    )
    val riceBucketTraining = Exercise(
        id = 114, name = "Rice Bucket Training",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.TIME, baseXp = 22,
        statContribution = StatContribution(strength = 0.3, dexterity = 0.5, endurance = 0.2),
        description = "Plunge hands into a bucket of rice and perform gripping, twisting, and spreading motions for comprehensive forearm work."
    )
    val towelWringing = Exercise(
        id = 115, name = "Towel Wringing",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.TIME, baseXp = 15,
        statContribution = StatContribution(strength = 0.3, endurance = 0.5, dexterity = 0.2),
        description = "Wring a wet towel repeatedly in both directions — effective for wrist and forearm muscle endurance."
    )
    val knucklePushUp = Exercise(
        id = 116, name = "Knuckle Push-Up",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 26,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "Performing push-ups on your knuckles builds grip strength and wrist stability under load."
    )
    val plankOnFists = Exercise(
        id = 117, name = "Plank on Fists",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 20,
        statContribution = StatContribution(strength = 0.3, endurance = 0.5, focus = 0.2),
        description = "Hold a plank on closed fists rather than open palms — activates the forearm flexors isometrically."
    )
    val wallFingerPress = Exercise(
        id = 118, name = "Wall Finger Press",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(strength = 0.3, dexterity = 0.5, endurance = 0.2),
        description = "Press all fingers against a wall and push, creating isometric tension throughout the forearm flexors and extensors."
    )
    val deadHangForearm = Exercise(
        id = 119, name = "Dead Hang (Grip Focus)",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS),
        secondaryMuscles = listOf(MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, focus = 0.1),
        description = "Simply hang from a bar for as long as possible. One of the most effective grip and forearm builders available."
    )
    val towelPullUpBodyweight = Exercise(
        id = 120, name = "Towel Pull-Up (Bodyweight)",
        muscleGroup = MuscleGroup.FOREARMS,
        primaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.LATS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 44,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Drape a towel over a bar and grip the ends to do pull-ups — the thicker grip dramatically increases forearm demand."
    )

    // ─────────────────────────────────────────────────────
    // QUADRICEPS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellBackSquat = Exercise(
        id = 121, name = "Barbell Back Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 65,
        statContribution = StatContribution(strength = 0.9, power = 0.1),
        description = "The foundational quad exercise. Bar on upper back, squat to parallel or below for maximum quad and glute activation."
    )
    val frontSquat = Exercise(
        id = 122, name = "Front Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 60,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "Bar rests across front delts with an upright torso, placing greater emphasis on the quads versus the back squat."
    )
    val legPress = Exercise(
        id = 123, name = "Leg Press",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 55,
        statContribution = StatContribution(strength = 0.8, endurance = 0.2),
        description = "Seated on a machine, press a weighted platform away with your feet — heavy quad loading with minimal spinal stress."
    )
    val hackSquat = Exercise(
        id = 124, name = "Hack Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 55,
        statContribution = StatContribution(strength = 0.8, power = 0.2),
        description = "A machine squat variant with a fixed backward lean, isolating the quads and removing upper body from the equation."
    )
    val legExtension = Exercise(
        id = 125, name = "Leg Extension",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Seated machine that isolates the quadriceps through knee extension. Excellent for finishing and isolation work."
    )
    val bulgarianSplitSquat = Exercise(
        id = 126, name = "Bulgarian Split Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, flexibility = 0.1),
        description = "Rear foot elevated on a bench, front leg performs a single-leg squat — intense unilateral quad and glute builder."
    )
    val gobletSquat = Exercise(
        id = 127, name = "Goblet Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.KETTLEBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "Hold a kettlebell or dumbbell at chest height and squat. Reinforces upright torso and quad-dominant movement pattern."
    )
    val barbellLunge = Exercise(
        id = 128, name = "Barbell Lunge",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, endurance = 0.1),
        description = "Step forward or reverse with a barbell on your back to build quad and glute strength unilaterally."
    )
    val sissySquat = Exercise(
        id = 129, name = "Sissy Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 40,
        statContribution = StatContribution(strength = 0.7, flexibility = 0.2, dexterity = 0.1),
        description = "Knees travel far forward over the toes while the torso leans back — extreme quad isolation and stretch."
    )
    val spanishSquat = Exercise(
        id = 130, name = "Spanish Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.RESISTANCE_BAND,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "Band around a post and your waist, push knees out and squat deep. Targets the VMO (inner quad sweep)."
    )
    val bodyweightSquat = Exercise(
        id = 131, name = "Bodyweight Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, dexterity = 0.2),
        description = "Feet shoulder-width, squat to at least parallel. The fundamental lower body movement pattern."
    )
    val jumpSquat = Exercise(
        id = 132, name = "Jump Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(power = 0.6, strength = 0.2, dexterity = 0.2),
        description = "Squat down then explode upward into a jump, training power and fast-twitch quad fibers."
    )
    val lunge = Exercise(
        id = 133, name = "Lunge",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Step forward and lower the back knee toward the floor to train quads and glutes in a unilateral pattern."
    )
    val reverseLunge = Exercise(
        id = 134, name = "Reverse Lunge",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Step backward instead of forward — easier on the knee and emphasizes the quad more at the bottom."
    )
    val bulgarianSplitSquatBodyweight = Exercise(
        id = 135, name = "Bulgarian Split Squat (Bodyweight)",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, flexibility = 0.2),
        description = "Rear foot on a chair or step, perform a single-leg squat. One of the hardest bodyweight quad exercises."
    )
    val stepUp = Exercise(
        id = 136, name = "Step-Up",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Step onto a box or step with one foot and drive through the heel to stand up. Builds glute and quad strength."
    )
    val wallSit = Exercise(
        id = 137, name = "Wall Sit",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 25,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, focus = 0.1),
        description = "Hold a seated position against a wall at 90 degrees. An isometric quad burner for endurance and time-under-tension."
    )
    val pistolSquat = Exercise(
        id = 138, name = "Pistol Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 55,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, flexibility = 0.1),
        description = "A single-leg squat all the way to the floor with the other leg extended. The pinnacle of bodyweight quad strength."
    )
    val sissySquatBodyweight = Exercise(
        id = 139, name = "Sissy Squat (Bodyweight)",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, dexterity = 0.2),
        description = "Knees drive far forward while heels rise, leaning the torso back. A challenging quad isolation with no equipment."
    )
    val skaterSquat = Exercise(
        id = 140, name = "Skater Squat",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 40,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.3, flexibility = 0.1),
        description = "A single-leg squat with the non-working leg bent behind you. Progression toward the pistol squat."
    )

    // ─────────────────────────────────────────────────────
    // HAMSTRINGS (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val romanianDeadlift = Exercise(
        id = 141, name = "Romanian Deadlift",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 55,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "Hip hinge with soft knees, lowering a barbell along the legs until a hamstring stretch is felt. The premier hamstring builder."
    )
    val lyingLegCurl = Exercise(
        id = 142, name = "Lying Leg Curl",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 36,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Prone on a machine, curl the weight toward your glutes — directly isolates the hamstrings through knee flexion."
    )
    val seatedLegCurl = Exercise(
        id = 143, name = "Seated Leg Curl",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 36,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Seated variation of the leg curl offering a different hip angle, which can increase long head activation."
    )
    val stiffLegDeadlift = Exercise(
        id = 144, name = "Stiff-Leg Deadlift",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 52,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "Like the RDL but with straighter legs, maximizing the hamstring stretch at the bottom with a greater hip hinge."
    )
    val goodMorning = Exercise(
        id = 145, name = "Good Morning",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.7, flexibility = 0.2, endurance = 0.1),
        description = "Bar on the back, hinge forward with hips. Shifts load to the lower back and hamstrings."
    )
    val singleLegRdl = Exercise(
        id = 146, name = "Single-Leg Romanian Deadlift",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, flexibility = 0.2),
        description = "A unilateral RDL performed on one leg for balance, coordination, and isolating each hamstring independently."
    )
    val hamstringCableCurl = Exercise(
        id = 147, name = "Hamstring Cable Curl",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 34,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Attach a cuff to a cable at ankle height and perform standing leg curls for constant tension through the movement."
    )
    val gluteHamRaise = Exercise(
        id = 148, name = "Glute-Ham Raise",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_ONLY, baseXp = 50,
        statContribution = StatContribution(strength = 0.7, power = 0.2, endurance = 0.1),
        description = "On a GHR machine, lower your body from vertical to horizontal using only your hamstrings and then raise back up."
    )
    val sumoDeadlift = Exercise(
        id = 149, name = "Sumo Deadlift",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 62,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "Wide stance deadlift with toes pointed out. Shifts more load onto the inner hamstrings and adductors."
    )
    val dumbbellRdl = Exercise(
        id = 150, name = "Dumbbell RDL",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.7, flexibility = 0.2, endurance = 0.1),
        description = "The same hip-hinge pattern as the barbell RDL but with dumbbells — easier to learn and great for unilateral variation."
    )
    val nordicHamstringCurl = Exercise(
        id = 151, name = "Nordic Hamstring Curl",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 50,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, endurance = 0.1),
        description = "Kneel with feet anchored, lower your torso toward the floor by slowly giving in to gravity — brutal eccentric hamstring exercise."
    )
    val singleLegRdlBodyweight = Exercise(
        id = 152, name = "Single-Leg RDL (Bodyweight)",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, flexibility = 0.2),
        description = "Hip hinge on one leg with no added weight. Challenges balance while stretching and loading the hamstring."
    )
    val inchworm = Exercise(
        id = 153, name = "Inchworm",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(flexibility = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Fold forward, walk hands out to a plank, then walk feet to hands — dynamically loads the hamstrings in hip flexion."
    )
    val stabilityBallLegCurl = Exercise(
        id = 154, name = "Stability Ball Leg Curl",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Lie on your back, heels on a stability ball, and curl the ball toward your glutes. Requires no gym equipment."
    )
    val goodMorningBodyweight = Exercise(
        id = 155, name = "Good Morning (Bodyweight)",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, flexibility = 0.4, endurance = 0.2),
        description = "Hands behind head, perform a controlled hip hinge to parallel — a great introduction to the hinge pattern."
    )
    val reverseHyperextension = Exercise(
        id = 156, name = "Reverse Hyperextension",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, flexibility = 0.3, endurance = 0.2),
        description = "Lie face down on a table edge and raise your legs behind you to activate the hamstrings and glutes."
    )
    val standingHamstringCurlBand = Exercise(
        id = 157, name = "Standing Hamstring Curl (Band)",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.RESISTANCE_BAND,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "A resistance band around the ankle anchored to a low point; curl the foot toward the glute while standing."
    )
    val sprinting = Exercise(
        id = 158, name = "Sprinting",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES, MuscleGroup.CALVES),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 50,
        statContribution = StatContribution(power = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "High-speed running places enormous eccentric demand on the hamstrings during the late swing phase.",
        modes = listOf(
            ExerciseMode(ExerciseContext.STRENGTH, TrackingType.TIME, XpType.STRENGTH_XP),
            ExerciseMode(ExerciseContext.CARDIO, null, XpType.CARDIO_XP)
        )
    )
    // Fix: removed duplicate Glute Bridge and Single-Leg Glute Bridge here.
    // Both already exist under GLUTES (id=171, id=175) which is their correct primary section.
    // Replaced with two genuine hamstring exercises.
    val cablePullThrough = Exercise(
        id = 159, name = "Cable Pull-Through",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.2, endurance = 0.2),
        description = "Stand facing away from a low cable, hinge at hips and pull the handle through your legs. Directly loads the hamstrings and glutes through hip extension."
    )
    val hipExtensionMachine = Exercise(
        id = 160, name = "Hip Extension Machine",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "A prone machine-based hip extension that isolates the hamstrings and glutes with a fixed path and adjustable resistance."
    )

    // ─────────────────────────────────────────────────────
    // GLUTES (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val barbellHipThrust = Exercise(
        id = 161, name = "Barbell Hip Thrust",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.QUADS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 52,
        statContribution = StatContribution(strength = 0.7, power = 0.2, endurance = 0.1),
        description = "Upper back on a bench, barbell across hips — drive hips to full extension. The single best direct glute exercise."
    )
    val squatGlute = Exercise(
        id = 162, name = "Barbell Squat (Glute Focus)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 62,
        statContribution = StatContribution(strength = 0.9, power = 0.1),
        description = "A deep squat with a barbell stimulates the glutes heavily, especially below parallel where the hip extensors are most active."
    )
    val rdlGlute = Exercise(
        id = 163, name = "Romanian Deadlift (Glute Focus)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 52,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "The hip hinge pattern loads the glutes maximally at the top lockout — both a hamstring and glute staple."
    )
    val cableKickback = Exercise(
        id = 164, name = "Cable Kickback",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Attach a cuff at a low cable and extend the leg behind you to isolate the glute through hip extension."
    )
    val bulgarianSplitSquatGlute = Exercise(
        id = 165, name = "Bulgarian Split Squat (Glute Focus)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 48,
        statContribution = StatContribution(strength = 0.7, dexterity = 0.2, flexibility = 0.1),
        description = "Rear foot elevated, loading the front leg — hits the glutes hard when the stride is long and depth is full."
    )
    val abductorMachine = Exercise(
        id = 166, name = "Abductor Machine",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Push the pads outward against resistance to train the glute medius and minimus for shape and stability."
    )
    val hyperextension45 = Exercise(
        id = 167, name = "45-Degree Hyperextension",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_ONLY, baseXp = 32,
        statContribution = StatContribution(strength = 0.6, flexibility = 0.3, endurance = 0.1),
        description = "On a back extension machine, load a plate on the chest and hinge to work the glutes and lower back."
    )
    val stepUpDumbbell = Exercise(
        id = 168, name = "Step-Up with Dumbbells",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 38,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Holding dumbbells, step onto a box — drive through the heel to engage the glute maximally on the way up."
    )
    val sumoDeadliftGlute = Exercise(
        id = 169, name = "Sumo Deadlift (Glute Focus)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        secondaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 60,
        statContribution = StatContribution(strength = 0.8, flexibility = 0.1, power = 0.1),
        description = "Wide stance pulls the glutes into a position of greater hip abduction, recruiting more glute medius than conventional."
    )
    val dumbbellHipThrust = Exercise(
        id = 170, name = "Dumbbell Hip Thrust",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.QUADS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 45,
        statContribution = StatContribution(strength = 0.7, power = 0.2, endurance = 0.1),
        description = "A dumbbell-based hip thrust for those without a barbell — excellent for the same glute-dominant hip extension pattern."
    )
    val gluteBridgeBodyweight = Exercise(
        id = 171, name = "Glute Bridge (Bodyweight)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, flexibility = 0.2),
        description = "Lie on your back, feet flat, and drive hips to the ceiling. Simple and effective glute activation and builder."
    )
    val hipThrustBodyweight = Exercise(
        id = 172, name = "Hip Thrust (Bodyweight)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, power = 0.1),
        description = "Upper back on a couch or chair, drive hips up and squeeze hard at the top. Progress to single-leg or weighted."
    )
    val donkeyKick = Exercise(
        id = 173, name = "Donkey Kick",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "On all fours, kick one leg back and up, focusing on squeezing the glute at the top of the movement."
    )
    val fireHydrant = Exercise(
        id = 174, name = "Fire Hydrant",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, endurance = 0.2),
        description = "On all fours, raise one knee out to the side at 90 degrees to activate the glute medius and outer glute."
    )
    val singleLegGluteBridgeGlute = Exercise(
        id = 175, name = "Single-Leg Glute Bridge",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "One leg extended, bridge up explosively on the other leg for more isolated and demanding glute work."
    )
    val bulgarianSplitSquatGluteBodyweight = Exercise(
        id = 176, name = "Bulgarian Split Squat (Bodyweight, Glute)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.6, dexterity = 0.2, flexibility = 0.2),
        description = "Rear foot on a surface, deep single-leg squat — heavy glute stimulus even without added weight."
    )
    val squatPulse = Exercise(
        id = 177, name = "Squat Pulse",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(strength = 0.3, endurance = 0.6, focus = 0.1),
        description = "Hold the bottom of a squat and pulse up and down a few inches, keeping constant tension on the glutes."
    )
    val frogPump = Exercise(
        id = 178, name = "Frog Pump",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, flexibility = 0.1),
        description = "Lie on back with soles of feet together, drive hips up in short rapid reps. Targets the glutes with minimal hamstring help."
    )
    val curtsyLunge = Exercise(
        id = 179, name = "Curtsy Lunge",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, flexibility = 0.2),
        description = "Step one leg behind and to the outside (like a curtsy), loading the leading glute and abductors in an oblique plane."
    )
    val stepUpGlute = Exercise(
        id = 180, name = "Step-Up (Glute Drive)",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 26,
        statContribution = StatContribution(strength = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Use a box, step, or stair — an underrated glute builder when the emphasis is on driving through the heel."
    )

    // ─────────────────────────────────────────────────────
    // CALVES (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val standingCalfRaiseMachine = Exercise(
        id = 181, name = "Standing Calf Raise (Machine)",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Loaded on a calf raise machine, rise onto the balls of your feet for full plantarflexion. Hits the gastrocnemius."
    )
    val seatedCalfRaise = Exercise(
        id = 182, name = "Seated Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 26,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, dexterity = 0.1),
        description = "With knees at 90° and a pad across the thighs, the bent knee shifts load to the soleus (deep calf muscle)."
    )
    val legPressCalfRaise = Exercise(
        id = 183, name = "Leg Press Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Using just the balls of your feet on the leg press platform — allows heavy loading and a large range of motion."
    )
    val donkeyCalfRaise = Exercise(
        id = 184, name = "Donkey Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, flexibility = 0.1),
        description = "Hip-hinged with weight on the lower back, these target the gastrocnemius in a stretched hip-extended position."
    )
    val singleLegCalfRaiseMachine = Exercise(
        id = 185, name = "Single-Leg Calf Raise (Machine)",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Unilateral raises allow each calf to work through its full range independently, great for addressing imbalances."
    )
    val smithMachineCalfRaise = Exercise(
        id = 186, name = "Smith Machine Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.SMITH_MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Using a Smith machine bar for guided stability allows very heavy loading and controlled range of motion."
    )
    val standingBarbellCalfRaise = Exercise(
        id = 187, name = "Standing Barbell Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Barbell across your upper back, perform calf raises — requires balance but loads both heads fully."
    )
    val angledLegPressCalfRaise = Exercise(
        id = 188, name = "Angled Leg Press Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Feet low on the sled at different angles (toes in/out) to vary the stress across the two gastrocnemius heads."
    )
    val cableCalfRaise = Exercise(
        id = 189, name = "Cable Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 26,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, dexterity = 0.1),
        description = "Attach a belt around your waist to a cable for a free-standing loaded calf raise with constant resistance."
    )
    val boxCalfRaiseWeighted = Exercise(
        id = 190, name = "Box Calf Raise (Weighted)",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.DUMBBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, flexibility = 0.1),
        description = "Standing on the edge of a step or box with dumbbells, drop heels below the step for a deep stretch each rep."
    )
    val standingCalfRaise = Exercise(
        id = 191, name = "Standing Calf Raise (Bodyweight)",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 15,
        statContribution = StatContribution(strength = 0.3, endurance = 0.6, dexterity = 0.1),
        description = "On flat ground or a step edge, simply rise onto the balls of your feet. Add reps, pauses, and single-leg variations."
    )
    val singleLegCalfRaise = Exercise(
        id = 192, name = "Single-Leg Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, dexterity = 0.1),
        description = "One foot raised, perform raises on the other leg. Doubles demand and addresses left/right strength imbalances."
    )
    val jumpRope = Exercise(
        id = 193, name = "Jump Rope",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.TIME, baseXp = 35,
        statContribution = StatContribution(endurance = 0.4, dexterity = 0.4, power = 0.2),
        description = "Continuous rope jumping is an exceptional calf endurance builder and cardio drill, stressing both calf heads.",
        modes = listOf(
            ExerciseMode(ExerciseContext.STRENGTH, TrackingType.TIME, XpType.STRENGTH_XP),
            ExerciseMode(ExerciseContext.CARDIO, null, XpType.CARDIO_XP)
        )
    )
    val boxJump = Exercise(
        id = 194, name = "Box Jump",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.QUADS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 40,
        statContribution = StatContribution(power = 0.7, dexterity = 0.2, endurance = 0.1),
        description = "Explosive jumps onto a box or step; the landing and take-off mechanic recruits the entire calf complex powerfully."
    )
    val stairCalfRaise = Exercise(
        id = 195, name = "Stair Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, flexibility = 0.1),
        description = "Using stairs as a free step platform, perform single or double-leg calf raises off the edge for depth."
    )
    val slowEccentricCalfRaise = Exercise(
        id = 196, name = "Slow Eccentric Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, focus = 0.1),
        description = "Rise quickly then lower very slowly (5–8 seconds) to increase time-under-tension and eccentric strength."
    )
    val seatedTowelCalfRaise = Exercise(
        id = 197, name = "Seated Towel Calf Raise",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 14,
        statContribution = StatContribution(strength = 0.3, endurance = 0.6, dexterity = 0.1),
        description = "Seated on a chair with a towel across your thighs for resistance, perform upward presses against the resistance."
    )
    val tipToeWalking = Exercise(
        id = 198, name = "Tiptoe Walking",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(strength = 0.2, endurance = 0.6, dexterity = 0.2),
        description = "Walk on the balls of your feet for distance. A low-intensity but continuous calf endurance drill."
    )
    val depthJump = Exercise(
        id = 199, name = "Depth Jump",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(power = 0.6, dexterity = 0.2, endurance = 0.2),
        description = "Step off a box, absorb the landing, and immediately jump — the reactive load trains the Achilles and soleus."
    )
    val pogoJumps = Exercise(
        id = 200, name = "Pogo Jumps",
        muscleGroup = MuscleGroup.CALVES,
        primaryMuscles = listOf(MuscleGroup.CALVES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(power = 0.5, dexterity = 0.3, endurance = 0.2),
        description = "Rapid, small hops using minimal knee bend — almost all energy comes from the ankle/calf springlike action."
    )

    // ─────────────────────────────────────────────────────
    // CORE (20 exercises — 10 gym, 10 bodyweight)
    // ─────────────────────────────────────────────────────

    val cableCrunch = Exercise(
        id = 201, name = "Cable Crunch",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.OBLIQUES),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Kneeling at a cable machine, curl the torso downward against resistance — allows progressive overload of the rectus abdominis."
    )
    val weightedSitUp = Exercise(
        id = 202, name = "Weighted Sit-Up",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 25,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "A sit-up performed with a weight plate held at the chest or overhead for added resistance to the core flexors."
    )
    val abWheelRollout = Exercise(
        id = 203, name = "Ab Wheel Rollout",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.LATS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.HOME,
        trackingType = TrackingType.REPS_ONLY, baseXp = 38,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Roll forward from a kneeling position until horizontal, then pull back. One of the most demanding core anti-extension exercises."
    )
    val hangingLegRaise = Exercise(
        id = 204, name = "Hanging Leg Raise",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.OBLIQUES, MuscleGroup.FOREARMS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Hang from a bar and raise the knees or straight legs toward the chest to target the lower rectus abdominis."
    )
    val cableWoodchop = Exercise(
        id = 205, name = "Cable Woodchop",
        muscleGroup = MuscleGroup.OBLIQUES,
        primaryMuscles = listOf(MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 30,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, power = 0.2),
        description = "Rotate and pull a cable across the body diagonally. Trains the obliques and rotational core powerfully."
    )
    val pallofPress = Exercise(
        id = 206, name = "Pallof Press",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.CABLE,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 25,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, focus = 0.2),
        description = "Press a cable outward and hold the extended position — an anti-rotation exercise that builds lateral core stiffness."
    )
    val declineSitUp = Exercise(
        id = 207, name = "Decline Sit-Up",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.4, endurance = 0.5, dexterity = 0.1),
        description = "Performed on a decline bench, this increases the range and resistance on the upper abs."
    )
    val dragonFlag = Exercise(
        id = 208, name = "Dragon Flag",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 55,
        statContribution = StatContribution(strength = 0.6, endurance = 0.3, dexterity = 0.1),
        description = "Lie on a bench and raise the entire body to vertical, then lower with control. An elite core strength exercise."
    )
    val landmineRotation = Exercise(
        id = 209, name = "Landmine Rotation",
        muscleGroup = MuscleGroup.OBLIQUES,
        primaryMuscles = listOf(MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BARBELL,
        trackingType = TrackingType.REPS_WEIGHT, baseXp = 32,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.4, power = 0.2),
        description = "Hold the end of a bar fixed in a landmine, swing it side-to-side in an arc. Trains obliques and rotational power."
    )
    val ghrSitUp = Exercise(
        id = 210, name = "GHR Sit-Up",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.HAMSTRINGS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.MACHINE,
        trackingType = TrackingType.REPS_ONLY, baseXp = 35,
        statContribution = StatContribution(strength = 0.5, endurance = 0.4, dexterity = 0.1),
        description = "Feet anchored on a glute-ham raise machine, perform a full range sit-up from nearly horizontal to upright."
    )
    val plank = Exercise(
        id = 211, name = "Plank",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.OBLIQUES, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 25,
        statContribution = StatContribution(strength = 0.3, endurance = 0.5, focus = 0.2),
        description = "Hold a forearm or straight-arm plank for time. The foundation of core anti-extension endurance."
    )
    val sidePlank = Exercise(
        id = 212, name = "Side Plank",
        muscleGroup = MuscleGroup.OBLIQUES,
        primaryMuscles = listOf(MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 22,
        statContribution = StatContribution(strength = 0.3, endurance = 0.5, focus = 0.2),
        description = "On one forearm and foot, hold the body in a lateral line. Targets the obliques and lateral core stabilizers."
    )
    val hollowBodyHold = Exercise(
        id = 213, name = "Hollow Body Hold",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 28,
        statContribution = StatContribution(strength = 0.4, endurance = 0.4, focus = 0.2),
        description = "Lying on your back with arms and legs extended low, press the lower back into the floor. Elite core stability training."
    )
    val bicycleCrunch = Exercise(
        id = 214, name = "Bicycle Crunch",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.4, dexterity = 0.3, endurance = 0.3),
        description = "Alternating elbow-to-knee crunches with rotation. One of the highest-activation ab exercises in EMG studies."
    )
    val vUp = Exercise(
        id = 215, name = "V-Up",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 28,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, dexterity = 0.2),
        description = "Simultaneously raise arms and legs toward each other to form a V, targeting both upper and lower abs."
    )
    val mountainClimbers = Exercise(
        id = 216, name = "Mountain Climbers",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.EXPLOSIVE, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 28,
        statContribution = StatContribution(power = 0.4, endurance = 0.4, dexterity = 0.2),
        description = "In a push-up position, alternate driving knees toward the chest rapidly. A dynamic core and cardio combination."
    )
    val lSit = Exercise(
        id = 217, name = "L-Sit",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.QUADS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 45,
        statContribution = StatContribution(strength = 0.5, endurance = 0.3, focus = 0.2),
        description = "Seated on the floor, hands beside hips, lift the body off the ground with legs extended. Intense hip flexor and core compression."
    )
    val russianTwist = Exercise(
        id = 218, name = "Russian Twist",
        muscleGroup = MuscleGroup.OBLIQUES,
        primaryMuscles = listOf(MuscleGroup.OBLIQUES),
        secondaryMuscles = listOf(MuscleGroup.ABS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 20,
        statContribution = StatContribution(strength = 0.3, dexterity = 0.4, endurance = 0.3),
        description = "Seated with torso slightly back, rotate a weight or hands side to side. Targets the obliques and rotational core."
    )
    val flutterKicks = Exercise(
        id = 219, name = "Flutter Kicks",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.QUADS),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 20,
        statContribution = StatContribution(strength = 0.3, endurance = 0.6, dexterity = 0.1),
        description = "Lying on back, legs elevated slightly and alternate kicking in a scissor motion. Long-duration lower ab burner."
    )
    val deadBug = Exercise(
        id = 220, name = "Dead Bug",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.STRENGTH, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 22,
        statContribution = StatContribution(strength = 0.3, dexterity = 0.4, endurance = 0.3),
        description = "Lying on your back, extend opposite arm and leg while pressing lower back to the floor. Premium anti-extension core drill."
    )


    // ─────────────────────────────────────────────────────
    // FLEXIBILITY (20 exercises — IDs 301–320)
    // Primary stat: Flexibility. Monk class exercises.
    // All tracked by TIME unless noted. No equipment required.
    // ─────────────────────────────────────────────────────

    val standingForwardFold = Exercise(
        id = 301, name = "Standing Forward Fold",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.CALVES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.7, focus = 0.2, endurance = 0.1),
        description = "Stand tall and hinge forward from the hips, letting the arms hang toward the floor. A foundational hamstring and lower back stretch."
    )
    val seatedForwardFold = Exercise(
        id = 302, name = "Seated Forward Fold",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.CALVES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.7, focus = 0.2, endurance = 0.1),
        description = "Seated with legs extended, hinge forward and reach for the feet. Deeper hamstring stretch than the standing version."
    )
    val hipFlexorLungeStretch = Exercise(
        id = 303, name = "Hip Flexor Lunge Stretch",
        muscleGroup = MuscleGroup.HIP_FLEXORS,
        primaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.QUADS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Kneeling lunge position, press the hips forward to open the front hip and stretch the iliopsoas. Essential for anyone who sits a lot."
    )
    val pigeonPose = Exercise(
        id = 304, name = "Pigeon Pose",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 16,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Front leg bent across the body, rear leg extended — one of the deepest glute and external hip rotator stretches available."
    )
    val catCowStretch = Exercise(
        id = 305, name = "Cat-Cow Stretch",
        muscleGroup = MuscleGroup.LOWER_BACK,
        primaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.7, dexterity = 0.2, focus = 0.1),
        description = "On all fours, alternate arching and rounding the spine. A gentle spinal mobility drill that warms the entire back."
    )
    val childsPose = Exercise(
        id = 306, name = "Child's Pose",
        muscleGroup = MuscleGroup.LOWER_BACK,
        primaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.LATS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.6, focus = 0.3, endurance = 0.1),
        description = "Sit back on heels with arms extended forward and forehead on the floor. A restorative stretch for the lower back and lats."
    )
    val cobraPose = Exercise(
        id = 307, name = "Cobra Pose",
        muscleGroup = MuscleGroup.ABS,
        primaryMuscles = listOf(MuscleGroup.ABS, MuscleGroup.LOWER_BACK),
        secondaryMuscles = listOf(MuscleGroup.FRONT_SHOULDERS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.7, strength = 0.1, focus = 0.2),
        description = "Lying prone, press the chest up with arms while keeping hips grounded. Opens the anterior core and stretches the abdominals."
    )
    val butterflyStretch = Exercise(
        id = 308, name = "Butterfly Stretch",
        muscleGroup = MuscleGroup.HIP_FLEXORS,
        primaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.GLUTES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Seated with soles of feet together and knees out, press the knees toward the floor. Targets the inner thigh and hip external rotators."
    )
    val figureFourStretch = Exercise(
        id = 309, name = "Figure Four Stretch",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Lying on back, cross one ankle over the opposite knee and pull both legs toward the chest. A supine piriformis and glute stretch."
    )
    val worldsGreatestStretch = Exercise(
        id = 310, name = "World's Greatest Stretch",
        muscleGroup = MuscleGroup.HIP_FLEXORS,
        primaryMuscles = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 18,
        statContribution = StatContribution(flexibility = 0.6, dexterity = 0.3, focus = 0.1),
        description = "A multi-joint dynamic stretch: lunge forward, rotate the torso and reach upward, then transition into a hamstring stretch. Covers hip, thoracic, and hamstring mobility in one flow."
    )
    val couchStretch = Exercise(
        id = 311, name = "Couch Stretch",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Rear foot propped on a wall or couch, lunge down to deeply stretch the quad and hip flexor. One of the most effective quad flexibility exercises."
    )
    val ninetyNinetyHipStretch = Exercise(
        id = 312, name = "90/90 Hip Stretch",
        muscleGroup = MuscleGroup.GLUTES,
        primaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Both legs at 90 degrees — front leg externally rotated, rear internally — simultaneously stretches both hip rotator directions."
    )
    val standingQuadStretch = Exercise(
        id = 313, name = "Standing Quad Stretch",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS),
        secondaryMuscles = listOf(MuscleGroup.HIP_FLEXORS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.7, dexterity = 0.2, focus = 0.1),
        description = "Stand on one leg and pull the other foot toward the glute. The most accessible quad stretch, great for warm-up and cooldown."
    )
    val doorwayChestStretch = Exercise(
        id = 314, name = "Doorway Chest Stretch",
        muscleGroup = MuscleGroup.CHEST,
        primaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.FRONT_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.BICEPS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.7, focus = 0.2, endurance = 0.1),
        description = "Place forearms on a doorframe and lean through it. Opens the pecs and anterior deltoid — important for anyone who does heavy pressing."
    )
    val crossBodyShoulderStretch = Exercise(
        id = 315, name = "Cross-Body Shoulder Stretch",
        muscleGroup = MuscleGroup.REAR_SHOULDERS,
        primaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS),
        secondaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 8,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Pull one arm horizontally across the chest with the other hand. Stretches the posterior deltoid and upper back."
    )
    val threadTheNeedle = Exercise(
        id = 316, name = "Thread the Needle",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.REAR_SHOULDERS),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.7, dexterity = 0.2, focus = 0.1),
        description = "On all fours, slide one arm under the body and rotate the thoracic spine — a highly effective thoracic and upper back mobility drill."
    )
    val thoracicSpineRotation = Exercise(
        id = 317, name = "Thoracic Spine Rotation",
        muscleGroup = MuscleGroup.UPPER_BACK,
        primaryMuscles = listOf(MuscleGroup.UPPER_BACK),
        secondaryMuscles = listOf(MuscleGroup.OBLIQUES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.REPS_ONLY, baseXp = 12,
        statContribution = StatContribution(flexibility = 0.6, dexterity = 0.3, focus = 0.1),
        description = "Seated or lying, rotate the upper spine while keeping the hips still. Restores rotational mobility in the thoracic vertebrae."
    )
    val downwardDog = Exercise(
        id = 318, name = "Downward Dog",
        muscleGroup = MuscleGroup.HAMSTRINGS,
        primaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
        secondaryMuscles = listOf(MuscleGroup.LATS, MuscleGroup.LOWER_BACK),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(flexibility = 0.6, strength = 0.2, focus = 0.2),
        description = "Inverted V position with hands and feet on the floor, pressing heels toward the ground. A yoga staple that stretches the entire posterior chain."
    )
    val deepSquatHold = Exercise(
        id = 319, name = "Deep Squat Hold",
        muscleGroup = MuscleGroup.HIP_FLEXORS,
        primaryMuscles = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.GLUTES),
        secondaryMuscles = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.CALVES),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 14,
        statContribution = StatContribution(flexibility = 0.7, focus = 0.2, endurance = 0.1),
        description = "Hold the bottom of a squat position, using elbows to push the knees out. Opens the hips, ankles, and lower back simultaneously."
    )
    val lyingQuadStretch = Exercise(
        id = 320, name = "Lying Quad Stretch",
        muscleGroup = MuscleGroup.QUADS,
        primaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.HIP_FLEXORS),
        secondaryMuscles = listOf(),
        category = ExerciseCategory.FLEXIBILITY, equipmentType = EquipmentType.BODYWEIGHT,
        trackingType = TrackingType.TIME, baseXp = 10,
        statContribution = StatContribution(flexibility = 0.8, focus = 0.1, endurance = 0.1),
        description = "Lying on one side, pull the top foot toward the glute. A comfortable deep quad stretch that requires no balance."
    )

    // ─────────────────────────────────────────────────────
    // ALL EXERCISES LIST
    // ─────────────────────────────────────────────────────

    val all: List<Exercise> = listOf(
        // Chest (1-20)
        barbellBenchPress, inclineBarbellPress, declineBarbellPress, dumbbellBenchPress,
        inclineDumbbellPress, cableCrossover, pecDeck, chestPressMachine, lowCableFly,
        dumbbellPullover, standardPushUp, wideGripPushUp, declinePushUp, inclinePushUp,
        archerPushUp, plyometricPushUp, pikePushUp, chestDipBodyweight, pseudoPlanchePushUp,
        diamondPushUpChest,
        // Back (21-40)
        barbellDeadlift, bentOverBarbellRow, latPulldown, seatedCableRow, tBarRow,
        singleArmDumbbellRow, facePull, machineRow, straightArmCablePulldown, rackPull,
        pullUp, invertedRow, neutralGripPullUp, scapularPullUp, archerPullUp,
        deadHang, supermanHold, negativePullUp, towelRow, towelPullUp,
        // Shoulders (41-60)
        barbellOverheadPress, dumbbellShoulderPress, lateralRaise, frontRaise, rearDeltFly,
        arnoldPress, cableLateralRaise, machineShoulderPress, uprightRow, shoulderFacePull,
        handstandPushUp, wallWalk, shoulderTapPushUp, plankShoulderTap, crabWalk,
        ytwOnFloor, bearCrawl, elevatedPikePushUp, bodyweightTRaise, pikePushUpShoulder,
        // Biceps (61-80)
        barbellCurl, dumbbellCurl, hammerCurl, preacherCurl, inclineDumbbellCurl,
        cableCurl, concentrationCurl, ezBarCurl, spiderCurl, highCableCurl,
        chinUp, invertedUnderhangRow, towelCurl, australianChinUp, flexedArmHang,
        negativeChinUp, archerChinUp, closeGripChinUp, ringCurl, bodyweightFootAssistedCurl,
        // Triceps (81-100)
        closeGripBenchPress, skullCrusher, tricepPushdown, overheadTricepExtension,
        tricepKickback, machineTricepPress, ropePushdown, jmPress,
        seatedOverheadDumbbellExtension, tatePress, tricepDipBench, parallelBarDip,
        bodyweightSkullcrusher, diamondPushUpTricep, clappingPushUpBehind,
        narrowGripWallPushUp, handstandPushUpElbowsTucked, ringDip, pikePushUpTricep,
        closeKnucklePushUp,
        // Forearms (101-120)
        wristCurl, reverseWristCurl, reverseBarbellCurl, farmersCarry, platePinch,
        barbellWristRoll, cableWristCurl, deadHangVariations, sledgehammerRotation,
        towelPullUpForearm, fingerPushUp, oneArmDeadHang, wristRotationPushUp,
        riceBucketTraining, towelWringing, knucklePushUp, plankOnFists, wallFingerPress,
        deadHangForearm, towelPullUpBodyweight,
        // Quads (121-140)
        barbellBackSquat, frontSquat, legPress, hackSquat, legExtension,
        bulgarianSplitSquat, gobletSquat, barbellLunge, sissySquat, spanishSquat,
        bodyweightSquat, jumpSquat, lunge, reverseLunge, bulgarianSplitSquatBodyweight,
        stepUp, wallSit, pistolSquat, sissySquatBodyweight, skaterSquat,
        // Hamstrings (141-160)
        romanianDeadlift, lyingLegCurl, seatedLegCurl, stiffLegDeadlift, goodMorning,
        singleLegRdl, hamstringCableCurl, gluteHamRaise, sumoDeadlift, dumbbellRdl,
        nordicHamstringCurl, singleLegRdlBodyweight, inchworm, stabilityBallLegCurl,
        goodMorningBodyweight, reverseHyperextension, standingHamstringCurlBand,
        sprinting, cablePullThrough, hipExtensionMachine,
        // Glutes (161-180)
        barbellHipThrust, squatGlute, rdlGlute, cableKickback, bulgarianSplitSquatGlute,
        abductorMachine, hyperextension45, stepUpDumbbell, sumoDeadliftGlute, dumbbellHipThrust,
        gluteBridgeBodyweight, hipThrustBodyweight, donkeyKick, fireHydrant,
        singleLegGluteBridgeGlute, bulgarianSplitSquatGluteBodyweight, squatPulse,
        frogPump, curtsyLunge, stepUpGlute,
        // Calves (181-200)
        standingCalfRaiseMachine, seatedCalfRaise, legPressCalfRaise, donkeyCalfRaise,
        singleLegCalfRaiseMachine, smithMachineCalfRaise, standingBarbellCalfRaise,
        angledLegPressCalfRaise, cableCalfRaise, boxCalfRaiseWeighted,
        standingCalfRaise, singleLegCalfRaise, jumpRope, boxJump, stairCalfRaise,
        slowEccentricCalfRaise, seatedTowelCalfRaise, tipToeWalking, depthJump, pogoJumps,
        // Core (201-220)
        cableCrunch, weightedSitUp, abWheelRollout, hangingLegRaise, cableWoodchop,
        pallofPress, declineSitUp, dragonFlag, landmineRotation, ghrSitUp,
        plank, sidePlank, hollowBodyHold, bicycleCrunch, vUp,
        mountainClimbers, lSit, russianTwist, flutterKicks, deadBug,
        // Flexibility (301-320)
        standingForwardFold, seatedForwardFold, hipFlexorLungeStretch, pigeonPose,
        catCowStretch, childsPose, cobraPose, butterflyStretch, figureFourStretch,
        worldsGreatestStretch, couchStretch, ninetyNinetyHipStretch, standingQuadStretch,
        doorwayChestStretch, crossBodyShoulderStretch, threadTheNeedle,
        thoracicSpineRotation, downwardDog, deepSquatHold, lyingQuadStretch
    )

    // ─────────────────────────────────────────────────────
    // HELPER FUNCTIONS
    // ─────────────────────────────────────────────────────

    fun getByMuscleGroup(muscleGroup: MuscleGroup) = all.filter { it.muscleGroup == muscleGroup }
    fun getByCategory(category: ExerciseCategory) = all.filter { it.category == category }
    fun getByEquipment(equipment: EquipmentType) = all.filter { it.equipmentType == equipment }
    fun getById(id: Int) = all.find { it.id == id }
    fun getGymExercises(muscleGroup: MuscleGroup) =
        all.filter { it.muscleGroup == muscleGroup && it.equipmentType != EquipmentType.BODYWEIGHT && it.equipmentType != EquipmentType.HOME }
    fun getBodyweightExercises(muscleGroup: MuscleGroup) =
        all.filter { it.muscleGroup == muscleGroup && (it.equipmentType == EquipmentType.BODYWEIGHT || it.equipmentType == EquipmentType.HOME) }
}

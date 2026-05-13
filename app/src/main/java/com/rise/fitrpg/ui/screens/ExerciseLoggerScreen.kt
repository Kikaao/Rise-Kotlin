package com.rise.fitrpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rise.fitrpg.data.models.Exercise
import com.rise.fitrpg.data.models.ExerciseLibrary
import com.rise.fitrpg.data.models.MuscleGroup
import com.rise.fitrpg.data.models.WorkoutType
import com.rise.fitrpg.ui.theme.*

// ── HELPERS ───────────────────────────────────────────────

fun WorkoutType.isCardio(): Boolean = when (this) {
    WorkoutType.RUNNING, WorkoutType.CYCLING,
    WorkoutType.SWIMMING, WorkoutType.HIKING -> true
    else -> false
}

fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

fun estimateXp(reps: Int?, weightKg: Double?, durationSeconds: Int?): Int {
    return when {
        weightKg != null && reps != null -> ((weightKg * reps) * 0.1).toInt().coerceAtLeast(5)
        reps != null -> (reps * 0.5).toInt().coerceAtLeast(5)
        durationSeconds != null -> (durationSeconds * 0.2).toInt().coerceAtLeast(5)
        else -> 5
    }
}

// ── DATA MODEL ────────────────────────────────────────────

data class LoggedSet(
    val exerciseId: Int,
    val exerciseName: String,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val xpEarned: Int = 0
)

// Controls which mode the Running screen is in.
// IDLE = mode picker shown. TRACKING = live timer. MANUAL = distance+duration form.
private enum class RunMode { IDLE, TRACKING, MANUAL }

// Controls which timer state we're in during a tracked run.
// READY = landed on screen, waiting for user to press START.
// RUNNING = actively counting. PAUSED = user paused.
private enum class TrackState { READY, RUNNING, PAUSED }

// ── EXERCISE LOGGER SCREEN ────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLoggerScreen(
    workoutType: WorkoutType,
    onFinish: (sets: List<LoggedSet>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    var loggedSets by remember { mutableStateOf<List<LoggedSet>>(emptyList()) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    // ── MIXED mode selection: Running and muscle groups are mutually exclusive.
    // null = nothing selected, MuscleGroup = muscle group active, "RUNNING" = cardio active.
    // Using a sealed state here avoids the old bug where showCardioForMixed and
    // selectedMuscleGroup could both be non-null simultaneously.
    var mixedSelection by remember { mutableStateOf<Any?>(null) }
    val showCardioForMixed = mixedSelection == "RUNNING"

    // Workout-level timer — counts total session time regardless of run state.
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
        }
    }

    // Re-derive activeMuscleGroup from mixedSelection so the picker and exercise list
    // react correctly whether we're in MIXED or a pure strength workout.
    val activeMuscleGroup: MuscleGroup? = when {
        workoutType == WorkoutType.MIXED -> mixedSelection as? MuscleGroup
        else -> selectedMuscleGroup
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── HEADER ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 48.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "✕",
                color = TextSecondary,
                fontSize = 20.sp,
                modifier = Modifier.clickable { showCancelDialog = true }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = workoutType.name.replace("_", " "),
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = formatTimer(elapsedSeconds),
                    fontFamily = RajdhaniFamily,
                    fontSize = 13.sp,
                    color = PurpleLight
                )
            }
            Text(
                text = "+${loggedSets.sumOf { it.xpEarned }} XP",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PurpleLight
            )
        }

        // ── ROUTE TO CORRECT CONTENT ──────────────────────
        when {
            // Pure running workout → full RunningInputSection with Start/Manual picker
            workoutType == WorkoutType.RUNNING -> {
                RunningInputSection(onFinish = onFinish)
            }

            // Other pure cardio (cycling, swimming, hiking) → simple distance + duration form
            workoutType.isCardio() -> {
                CardioInputSection(workoutType = workoutType, onFinish = onFinish)
            }

            // Strength or Mixed → grouped muscle picker + exercise list
            else -> {
                MusclePickerWithExercises(
                    modifier = Modifier.weight(1f),
                    workoutType = workoutType,
                    loggedSets = loggedSets,
                    showCardioForMixed = showCardioForMixed,
                    onRunningChipClick = {
                        mixedSelection = if (showCardioForMixed) null else "RUNNING"
                    },
                    onMuscleEntrySelected = { entry ->
                        // entry.representative is the canonical MuscleGroup used as the key.
                        // For Shoulders it is FRONT_SHOULDERS; for singles it is the group itself.
                        val rep = entry.representative
                        if (workoutType == WorkoutType.MIXED) {
                            mixedSelection = if (activeMuscleGroup == rep) null else rep
                        } else {
                            selectedMuscleGroup = if (selectedMuscleGroup == rep) null else rep
                        }
                    },
                    activeMuscleGroup = activeMuscleGroup,
                    onExerciseClick = { exercise ->
                        selectedExercise = exercise
                        showBottomSheet = true
                    },
                    onRunningFinish = { runSets ->
                        loggedSets = loggedSets + runSets
                        mixedSelection = null
                    },
                    onRemoveSet = { set -> loggedSets = loggedSets - set },
                    onFinish = { onFinish(loggedSets) }
                )
            }
        }
    }

    // ── ADD SET BOTTOM SHEET ──────────────────────────────
    if (showBottomSheet && selectedExercise != null) {
        AddSetBottomSheet(
            exercise = selectedExercise!!,
            onAdd = { sets ->
                loggedSets = loggedSets + sets
                showBottomSheet = false
            },
            onDismiss = { showBottomSheet = false }
        )
    }

    // ── CANCEL DIALOG ─────────────────────────────────────
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = CardDark,
            title = {
                Text(
                    text = "Cancel Workout?",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Your progress will be lost.",
                    fontFamily = OutfitFamily,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Text(
                    text = "CANCEL WORKOUT",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    text = "KEEP GOING",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PurpleLight,
                    modifier = Modifier
                        .clickable { showCancelDialog = false }
                        .padding(8.dp)
                )
            }
        )
    }
}

// ── MUSCLE CHIP ───────────────────────────────────────────
// Extracted to avoid duplicating the chip styling between Running and muscle groups.

@Composable
private fun MuscleChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) selectedColor else CardDark)
            .border(1.dp, if (isSelected) selectedColor else BorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

// ── MUSCLE ENTRY ──────────────────────────────────────────
// UI-only model. Represents one row in the muscle picker.
// Shoulders merges FRONT/SIDE/REAR into one entry — model enums are untouched.
// representative is the key used in mixedSelection / selectedMuscleGroup.

private sealed class MuscleEntry {
    abstract val label: String
    abstract val region: String
    abstract val representative: MuscleGroup   // key for selection state
    abstract val groups: List<MuscleGroup>     // all enums this entry covers

    data class Single(
        val group: MuscleGroup,
        override val region: String
    ) : MuscleEntry() {
        override val label = group.name
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
        override val representative = group
        override val groups = listOf(group)
    }

    object Shoulders : MuscleEntry() {
        override val label = "Shoulders"
        override val region = "UPPER"
        override val representative = MuscleGroup.FRONT_SHOULDERS
        override val groups = listOf(
            MuscleGroup.FRONT_SHOULDERS,
            MuscleGroup.SIDE_SHOULDERS,
            MuscleGroup.REAR_SHOULDERS
        )
    }
}

// Ordered list of all picker entries — Upper → Core → Lower.
// FRONT/SIDE/REAR_SHOULDERS are replaced by the single Shoulders entry.
private val ALL_MUSCLE_ENTRIES: List<MuscleEntry> = listOf(
    MuscleEntry.Single(MuscleGroup.CHEST,       "UPPER"),
    MuscleEntry.Shoulders,
    MuscleEntry.Single(MuscleGroup.TRICEPS,     "UPPER"),
    MuscleEntry.Single(MuscleGroup.BICEPS,      "UPPER"),
    MuscleEntry.Single(MuscleGroup.FOREARMS,    "UPPER"),
    MuscleEntry.Single(MuscleGroup.LATS,        "UPPER"),
    MuscleEntry.Single(MuscleGroup.UPPER_BACK,  "UPPER"),
    MuscleEntry.Single(MuscleGroup.ABS,         "CORE"),
    MuscleEntry.Single(MuscleGroup.OBLIQUES,    "CORE"),
    MuscleEntry.Single(MuscleGroup.LOWER_BACK,  "CORE"),
    MuscleEntry.Single(MuscleGroup.QUADS,       "LOWER"),
    MuscleEntry.Single(MuscleGroup.HAMSTRINGS,  "LOWER"),
    MuscleEntry.Single(MuscleGroup.GLUTES,      "LOWER"),
    MuscleEntry.Single(MuscleGroup.CALVES,      "LOWER"),
    MuscleEntry.Single(MuscleGroup.HIP_FLEXORS, "LOWER")
)

// ── MUSCLE PICKER WITH EXERCISES ─────────────────────────
// Vertical scrollable picker: search bar → region headers → muscle rows → exercise list.
// When a muscle entry is selected its exercises expand inline below it in the same scroll.

@Composable
private fun MusclePickerWithExercises(
    modifier: Modifier = Modifier,
    workoutType: WorkoutType,
    loggedSets: List<LoggedSet>,
    showCardioForMixed: Boolean,
    activeMuscleGroup: MuscleGroup?,
    onRunningChipClick: () -> Unit,
    onMuscleEntrySelected: (MuscleEntry) -> Unit,
    onExerciseClick: (Exercise) -> Unit,
    onRunningFinish: (List<LoggedSet>) -> Unit,
    onRemoveSet: (LoggedSet) -> Unit,
    onFinish: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Find which MuscleEntry the current activeMuscleGroup belongs to, so we know
    // which entry is selected even when activeMuscleGroup is a non-representative enum
    // (e.g. SIDE_SHOULDERS still matches the Shoulders entry).
    val selectedEntry: MuscleEntry? = if (activeMuscleGroup == null) null else
        ALL_MUSCLE_ENTRIES.firstOrNull { it.groups.contains(activeMuscleGroup) }

    // Filter entries by search query against label. Empty query shows all.
    val filteredEntries = remember(searchQuery) {
        if (searchQuery.isBlank()) ALL_MUSCLE_ENTRIES
        else ALL_MUSCLE_ENTRIES.filter {
            it.label.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    // Exercises to show beneath the selected entry.
    val selectedExercises = remember(selectedEntry) {
        if (selectedEntry == null) emptyList()
        else selectedEntry.groups.flatMap { ExerciseLibrary.getByMuscleGroup(it) }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ── SEARCH BAR ────────────────────────────────
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search muscles...",
                        fontFamily = OutfitFamily,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PurplePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // ── MIXED: Running chip at top ─────────────────
        if (workoutType == WorkoutType.MIXED) {
            item {
                MuscleChip(
                    label = "🏃 Running",
                    isSelected = showCardioForMixed,
                    selectedColor = Color(0xFF0EA5E9),
                    onClick = onRunningChipClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (showCardioForMixed) {
                item {
                    RunningInputSection(
                        modifier = Modifier.fillMaxWidth(),
                        onFinish = onRunningFinish
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                }
                // When Running is open, hide the muscle list so we don't crowd the screen
                return@LazyColumn
            }
        }

        // ── REGION SECTIONS ───────────────────────────
        // Collect regions in order; only show a header when the region changes.
        val regions = listOf("UPPER", "CORE", "LOWER")
        for (region in regions) {
            val regionEntries = filteredEntries.filter { it.region == region }
            if (regionEntries.isEmpty()) continue

            // Region header — only shown when not searching (searching collapses headers)
            if (searchQuery.isBlank()) {
                item(key = "header_$region") {
                    Text(
                        text = region,
                        fontFamily = OutfitFamily,
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )
                }
            }

            for (entry in regionEntries) {
                val isSelected = selectedEntry == entry

                // Muscle group row
                item(key = entry.label) {
                    MuscleGroupRow(
                        entry = entry,
                        isSelected = isSelected,
                        setCount = loggedSets.count { set ->
                            entry.groups.any { g ->
                                // Match by group name since exerciseId -1 is used for cardio
                                set.exerciseName.equals(g.name.replace("_", " "), ignoreCase = true) ||
                                        selectedExercises.any { ex -> ex.id == set.exerciseId && entry.groups.contains(ex.muscleGroup) }
                            }
                        },
                        onClick = { onMuscleEntrySelected(entry) }
                    )
                }

                // Exercise list expands inline immediately below the selected entry
                if (isSelected) {
                    items(
                        items = selectedExercises,
                        key = { "ex_${it.id}" }
                    ) { exercise ->
                        ExerciseRow(
                            exercise = exercise,
                            setCount = loggedSets.count { it.exerciseId == exercise.id },
                            onClick = { onExerciseClick(exercise) },
                            indented = true
                        )
                    }

                    if (selectedExercises.isEmpty()) {
                        item(key = "empty_$region") {
                            Text(
                                text = "No exercises found",
                                fontFamily = OutfitFamily,
                                fontSize = 13.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── LOGGED SETS ───────────────────────────────
        if (loggedSets.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "LOGGED SETS",
                    fontFamily = OutfitFamily,
                    fontSize = 10.sp,
                    color = TextMuted,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(loggedSets, key = { "set_${it.exerciseId}_${it.hashCode()}" }) { set ->
                LoggedSetRow(set = set, onRemove = { onRemoveSet(set) })
            }
        }

        // ── FINISH BUTTON ─────────────────────────────
        if (loggedSets.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PurplePrimary)
                        .clickable { onFinish() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FINISH WORKOUT",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ── MUSCLE GROUP ROW ──────────────────────────────────────
// One tappable row in the vertical muscle picker.
// Shows the group name, region badge, set count if any, and a chevron.

@Composable
private fun MuscleGroupRow(
    entry: MuscleEntry,
    isSelected: Boolean,
    setCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurpleSurface else CardDark)
            .border(
                1.dp,
                if (isSelected) PurplePrimary else BorderColor,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = entry.label,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = if (isSelected) PurpleLight else TextPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (setCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PurplePrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$setCount sets",
                        fontFamily = OutfitFamily,
                        fontSize = 11.sp,
                        color = PurpleLight
                    )
                }
            }
            Text(
                text = if (isSelected) "▲" else "▼",
                fontSize = 12.sp,
                color = if (isSelected) PurpleLight else TextMuted
            )
        }
    }
}

// ── RUNNING INPUT SECTION ─────────────────────────────────
// Handles the full Running experience: mode picker → either live timer or manual form.
// Used both for pure RUNNING workouts and for the Running sub-section of MIXED.
//
// onFinish receives the logged set and either ends the workout (pure RUNNING)
// or appends to the session and returns to the muscle picker (MIXED).

@Composable
private fun RunningInputSection(
    modifier: Modifier = Modifier,
    onFinish: (List<LoggedSet>) -> Unit
) {
    var mode by remember { mutableStateOf(RunMode.IDLE) }

    when (mode) {
        RunMode.IDLE -> RunModePicker(
            modifier = modifier,
            onStartRun = { mode = RunMode.TRACKING },
            onEnterManually = { mode = RunMode.MANUAL }
        )
        RunMode.TRACKING -> StartRunScreen(
            modifier = modifier,
            onStop = { durationSeconds ->
                // After stopping, drop into manual entry with duration pre-filled
                // so the user only needs to enter distance.
                // We pass the recorded duration out via a dedicated compose state
                // by switching mode. The RunStopState holds the duration bridge.
                mode = RunMode.MANUAL
                // Duration is passed by hoisting state; see RunStopState below.
                // For now, StartRunScreen calls onStop(seconds) and we embed it
                // in a wrapper state that ManualRunForm reads.
                // Implementation: ManualRunForm accepts an optional pre-filled duration.
                _runStopDuration = durationSeconds
            }
        )
        RunMode.MANUAL -> ManualRunForm(
            modifier = modifier,
            prefilledDurationSeconds = _runStopDuration,
            onFinish = { set ->
                _runStopDuration = null   // Reset for next use
                onFinish(listOf(set))
            }
        )
    }
}

// Simple file-level mutable to bridge duration from StartRunScreen → ManualRunForm.
// This is intentionally not a ViewModel — it lives only for the lifetime of
// RunningInputSection's composition. Null when coming from IDLE → MANUAL directly.
private var _runStopDuration: Int? = null

// ── RUN MODE PICKER ───────────────────────────────────────
// The first screen the user sees when entering a Running workout.
// Two large cards: Start Run and Enter Manually.

@Composable
private fun RunModePicker(
    modifier: Modifier = Modifier,
    onStartRun: () -> Unit,
    onEnterManually: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "HOW DO YOU WANT TO LOG?",
            fontFamily = OutfitFamily,
            fontSize = 10.sp,
            color = TextMuted,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── START RUN CARD ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .border(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable(onClick = onStartRun)
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🏃", fontSize = 40.sp)
                Text(
                    text = "START RUN",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0EA5E9),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Track your run with a live timer",
                    fontFamily = OutfitFamily,
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── ENTER MANUALLY CARD ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .clickable(onClick = onEnterManually)
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "✏️", fontSize = 40.sp)
                Text(
                    text = "ENTER MANUALLY",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Enter distance and duration yourself",
                    fontFamily = OutfitFamily,
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── START RUN SCREEN ──────────────────────────────────────
// Three states:
//   READY   — user landed here; clock shows 00:00, big START button visible.
//   RUNNING — clock ticking; PAUSE and STOP visible.
//   PAUSED  — clock frozen; RESUME and STOP visible.
// STOP exits with the elapsed seconds so ManualRunForm can pre-fill duration.

@Composable
private fun StartRunScreen(
    modifier: Modifier = Modifier,
    onStop: (durationSeconds: Int) -> Unit
) {
    // Start in READY so the timer never auto-begins on composition.
    var trackState by remember { mutableStateOf(TrackState.READY) }
    var runSeconds by remember { mutableStateOf(0) }

    // Timer only ticks while RUNNING. Restarting the effect on state change
    // naturally pauses it when transitioning to PAUSED or READY.
    LaunchedEffect(trackState) {
        while (trackState == TrackState.RUNNING) {
            kotlinx.coroutines.delay(1000)
            runSeconds++
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // ── TIMER DISPLAY ──────────────────────────────
        Text(
            text = formatTimer(runSeconds),
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp,
            // Grey when READY, blue when active
            color = if (trackState == TrackState.READY) TextMuted else Color(0xFF0EA5E9),
            letterSpacing = 2.sp
        )

        Text(
            text = when (trackState) {
                TrackState.READY   -> "READY"
                TrackState.RUNNING -> "RUNNING"
                TrackState.PAUSED  -> "PAUSED"
            },
            fontFamily = OutfitFamily,
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            color = when (trackState) {
                TrackState.READY   -> TextMuted
                TrackState.RUNNING -> Color(0xFF0EA5E9)
                TrackState.PAUSED  -> TextMuted
            }
        )

        Spacer(modifier = Modifier.height(64.dp))

        // ── READY STATE: big START button only ─────────
        if (trackState == TrackState.READY) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0EA5E9))
                    .clickable { trackState = TrackState.RUNNING },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 36.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "START",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0EA5E9),
                letterSpacing = 3.sp
            )
        }

        // ── RUNNING / PAUSED STATE: PAUSE + STOP ───────
        if (trackState == TrackState.RUNNING || trackState == TrackState.PAUSED) {
            // PAUSE / RESUME circle button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(CardDark)
                    .border(2.dp, BorderColor, CircleShape)
                    .clickable {
                        trackState = if (trackState == TrackState.RUNNING)
                            TrackState.PAUSED else TrackState.RUNNING
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // ❚❚ for pause, ▶ for resume
                    text = if (trackState == TrackState.RUNNING) "❚❚" else "▶",
                    fontSize = 22.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (trackState == TrackState.RUNNING) "PAUSE" else "RESUME",
                fontFamily = OutfitFamily,
                fontSize = 10.sp,
                color = TextMuted,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // STOP button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFB91C1C))
                    .clickable { onStop(runSeconds) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "STOP",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

// ── MANUAL RUN FORM ───────────────────────────────────────
// Simple distance + duration entry. When reached from StartRunScreen,
// durationSeconds is pre-filled with the recorded run time.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualRunForm(
    modifier: Modifier = Modifier,
    prefilledDurationSeconds: Int? = null,
    onFinish: (LoggedSet) -> Unit
) {
    var distance by remember { mutableStateOf("") }
    // MM and SS are only used in manual entry mode (prefilledDurationSeconds == null).
    // When coming from the tracker, prefilledDurationSeconds is passed directly.
    var durationMm by remember { mutableStateOf("") }
    var durationSs by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (prefilledDurationSeconds != null) {
            // Let the user know the duration was captured from the timer
            Text(
                text = "Run tracked: ${formatTimer(prefilledDurationSeconds)}. Enter your distance.",
                fontFamily = OutfitFamily,
                fontSize = 13.sp,
                color = Color(0xFF0EA5E9)
            )
        } else {
            Text(
                text = "Enter your run details",
                fontFamily = OutfitFamily,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (km)", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        if (prefilledDurationSeconds != null) {
            // Duration came from the live tracker — show MM:SS, no editing needed.
            // NOT using a text field: converting seconds to minutes loses the remainder
            // (e.g. 45s becomes "0 minutes"). The raw seconds pass through directly on log.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .border(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = "Duration",
                        fontFamily = OutfitFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF0EA5E9).copy(alpha = 0.7f)
                    )
                    Text(
                        text = formatTimer(prefilledDurationSeconds),
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0EA5E9)
                    )
                }
            }
        } else {
            // Manual entry: two side-by-side fields for minutes and seconds.
            DurationInput(
                minutes = durationMm,
                seconds = durationSs,
                onMinutesChange = { durationMm = it },
                onSecondsChange = { durationSs = it }
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PurplePrimary)
                .clickable {
                    val durationSeconds = prefilledDurationSeconds
                        ?: ((durationMm.toIntOrNull() ?: 0) * 60 + (durationSs.toIntOrNull() ?: 0))
                    val set = LoggedSet(
                        exerciseId = -1,
                        exerciseName = "Running",
                        reps = null,
                        weightKg = distance.toDoubleOrNull(),
                        durationSeconds = durationSeconds,
                        xpEarned = estimateXp(null, distance.toDoubleOrNull(), durationSeconds)
                    )
                    onFinish(set)
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LOG RUN",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}

// ── DURATION INPUT ───────────────────────────────────────
// Shared MM / SS side-by-side input used in ManualRunForm and CardioInputSection.
// Stores minutes and seconds separately so no precision is lost converting to/from
// a plain "minutes" integer (which would drop any remainder seconds).

@Composable
private fun DurationInput(
    minutes: String,
    seconds: String,
    onMinutesChange: (String) -> Unit,
    onSecondsChange: (String) -> Unit
) {
    // Label above the row
    Text(
        text = "DURATION",
        fontFamily = OutfitFamily,
        fontSize = 10.sp,
        color = TextMuted,
        letterSpacing = 2.sp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = minutes,
            onValueChange = { input ->
                // Only accept up to 3 digits, no negative values
                if (input.length <= 3 && input.all { it.isDigit() }) onMinutesChange(input)
            },
            label = { Text("Min", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PurplePrimary
            )
        )
        OutlinedTextField(
            value = seconds,
            onValueChange = { input ->
                // Seconds must be 0-59, up to 2 digits
                val n = input.toIntOrNull()
                if (input.isEmpty() || (input.length <= 2 && input.all { it.isDigit() } && (n == null || n < 60))) {
                    onSecondsChange(input)
                }
            },
            label = { Text("Sec", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PurplePrimary
            )
        )
    }
}

// ── EXERCISE ROW ──────────────────────────────────────────
// indented = true when shown expanded below a muscle group row, adds left padding
// to visually connect it to its parent.

@Composable
fun ExerciseRow(
    exercise: Exercise,
    setCount: Int,
    onClick: () -> Unit,
    indented: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (indented) 12.dp else 0.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = exercise.muscleGroup.name.replace("_", " "),
                fontFamily = OutfitFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        if (setCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PurpleSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$setCount sets",
                    fontFamily = OutfitFamily,
                    fontSize = 11.sp,
                    color = PurpleLight
                )
            }
        }
        Text(
            text = "+",
            fontSize = 22.sp,
            color = PurpleLight,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// ── LOGGED SET ROW ────────────────────────────────────────

@Composable
fun LoggedSetRow(set: LoggedSet, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardDark)
            .border(1.dp, PurplePrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = set.exerciseName,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            Text(
                text = buildString {
                    if (set.reps != null) append("${set.reps} reps")
                    if (set.weightKg != null) append(" · ${set.weightKg}kg")
                    if (set.durationSeconds != null) append(" · ${set.durationSeconds}s")
                },
                fontFamily = OutfitFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "+${set.xpEarned} XP",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = PurpleLight
            )
            Text(
                text = "✕",
                fontSize = 14.sp,
                color = TextMuted,
                modifier = Modifier.clickable { onRemove() }
            )
        }
    }
}

// ── ADD SET BOTTOM SHEET ──────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSetBottomSheet(
    exercise: Exercise,
    onAdd: (List<LoggedSet>) -> Unit,
    onDismiss: () -> Unit
) {
    var sets by remember { mutableStateOf("1") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val isBodyweight = exercise.usesBodyweight
    val isTimed = exercise.trackingType == com.rise.fitrpg.data.models.TrackingType.TIME

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = exercise.name,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )
            Text(
                text = exercise.muscleGroup.name.replace("_", " "),
                fontFamily = OutfitFamily,
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Sets", color = TextMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            if (!isTimed) {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            if (!isBodyweight) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            if (isTimed) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (seconds)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurplePrimary)
                    .clickable {
                        val setCount = sets.toIntOrNull() ?: 1
                        val newSets = (1..setCount).map {
                            LoggedSet(
                                exerciseId = exercise.id,
                                exerciseName = exercise.name,
                                reps = reps.toIntOrNull(),
                                weightKg = weight.toDoubleOrNull(),
                                durationSeconds = duration.toIntOrNull(),
                                xpEarned = estimateXp(
                                    reps = reps.toIntOrNull(),
                                    weightKg = weight.toDoubleOrNull(),
                                    durationSeconds = duration.toIntOrNull()
                                )
                            )
                        }
                        onAdd(newSets)
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ADD SET",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

// ── CARDIO INPUT SECTION ──────────────────────────────────
// Used for non-running cardio types: CYCLING, SWIMMING, HIKING.
// RUNNING has its own RunningInputSection above.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardioInputSection(
    workoutType: WorkoutType,
    onFinish: (List<LoggedSet>) -> Unit
) {
    var distance by remember { mutableStateOf("") }
    var durationMm by remember { mutableStateOf("") }
    var durationSs by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Log your ${workoutType.name.replace("_", " ").lowercase()} session",
            fontFamily = OutfitFamily,
            fontSize = 14.sp,
            color = TextSecondary
        )

        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (km)", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        DurationInput(
            minutes = durationMm,
            seconds = durationSs,
            onMinutesChange = { durationMm = it },
            onSecondsChange = { durationSs = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PurplePrimary)
                .clickable {
                    val totalSeconds = (durationMm.toIntOrNull() ?: 0) * 60 +
                            (durationSs.toIntOrNull() ?: 0)
                    val cardioSet = LoggedSet(
                        exerciseId = -1,
                        exerciseName = workoutType.name.replace("_", " "),
                        reps = null,
                        weightKg = distance.toDoubleOrNull(),
                        durationSeconds = totalSeconds.takeIf { it > 0 },
                        xpEarned = estimateXp(
                            null,
                            distance.toDoubleOrNull(),
                            totalSeconds.takeIf { it > 0 }
                        )
                    )
                    onFinish(listOf(cardioSet))
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FINISH SESSION",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}
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

    val muscleGroups = MuscleGroup.entries.toList()

    // Re-derive selectedMuscleGroup from mixedSelection so the exercise list
    // reacts correctly whether we're in MIXED or a pure strength workout.
    val activeMuscleGroup: MuscleGroup? = when {
        workoutType == WorkoutType.MIXED -> mixedSelection as? MuscleGroup
        else -> selectedMuscleGroup
    }

    val exercises = remember(activeMuscleGroup) {
        if (activeMuscleGroup == null) emptyList()
        else ExerciseLibrary.getByMuscleGroup(activeMuscleGroup)
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

            // Strength or Mixed → muscle group picker + exercise list
            else -> {
                // ── MUSCLE GROUP SELECTOR ─────────────────
                Text(
                    text = "MUSCLE GROUP",
                    fontFamily = OutfitFamily,
                    fontSize = 10.sp,
                    color = TextMuted,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // MIXED: prepend a Running chip that is mutually exclusive with muscle chips
                    if (workoutType == WorkoutType.MIXED) {
                        item {
                            MuscleChip(
                                label = "🏃 Running",
                                isSelected = showCardioForMixed,
                                selectedColor = Color(0xFF0EA5E9),
                                onClick = {
                                    // Toggle: if already selected, deselect; otherwise select
                                    // and clear any active muscle group.
                                    mixedSelection = if (showCardioForMixed) null else "RUNNING"
                                }
                            )
                        }
                    }

                    items(muscleGroups) { group ->
                        val isSelected = activeMuscleGroup == group
                        MuscleChip(
                            label = group.name.replace("_", " "),
                            isSelected = isSelected,
                            selectedColor = PurplePrimary,
                            onClick = {
                                if (workoutType == WorkoutType.MIXED) {
                                    // Selecting a muscle group clears the Running selection
                                    mixedSelection = if (isSelected) null else group
                                } else {
                                    selectedMuscleGroup = if (isSelected) null else group
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── CARDIO PANEL FOR MIXED ─────────────────
                // When Running is selected in MIXED, show the full running input inline
                // rather than routing to a separate screen, because the user is mid-workout.
                if (workoutType == WorkoutType.MIXED && showCardioForMixed) {
                    RunningInputSection(
                        modifier = Modifier.weight(1f),
                        onFinish = { runSets ->
                            // Add run to the overall logged sets instead of finishing the workout
                            loggedSets = loggedSets + runSets
                            // Deselect Running chip after logging so user returns to muscle picker
                            mixedSelection = null
                        }
                    )
                } else {
                    // ── EXERCISE LIST ─────────────────────────
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (activeMuscleGroup == null) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Select a muscle group\nto browse exercises",
                                        fontFamily = OutfitFamily,
                                        fontSize = 14.sp,
                                        color = TextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            items(exercises) { exercise ->
                                ExerciseRow(
                                    exercise = exercise,
                                    setCount = loggedSets.count { it.exerciseId == exercise.id },
                                    onClick = {
                                        selectedExercise = exercise
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }

                        if (loggedSets.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "LOGGED SETS",
                                    fontFamily = OutfitFamily,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(loggedSets) { set ->
                                LoggedSetRow(set = set, onRemove = {
                                    loggedSets = loggedSets - set
                                })
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }

                    // ── FINISH BUTTON ─────────────────────────
                    if (loggedSets.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PurplePrimary)
                                .clickable { onFinish(loggedSets) }
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
    // Pre-fill duration in minutes if we came from a tracked run, otherwise empty.
    var distance by remember { mutableStateOf("") }
    var duration by remember {
        mutableStateOf(
            prefilledDurationSeconds?.let { (it / 60).toString() } ?: ""
        )
    }

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

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (minutes)", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            // If duration came from the tracker it's already filled; visually distinguish it.
            enabled = prefilledDurationSeconds == null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = if (prefilledDurationSeconds != null)
                    Color(0xFF0EA5E9).copy(alpha = 0.5f) else BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = Color(0xFF0EA5E9),
                disabledBorderColor = Color(0xFF0EA5E9).copy(alpha = 0.5f),
                disabledLabelColor = Color(0xFF0EA5E9).copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PurplePrimary)
                .clickable {
                    val durationSeconds = prefilledDurationSeconds
                        ?: (duration.toIntOrNull()?.times(60))
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

// ── EXERCISE ROW ──────────────────────────────────────────

@Composable
fun ExerciseRow(
    exercise: Exercise,
    setCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
    var duration by remember { mutableStateOf("") }

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

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (minutes)", color = TextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PurplePrimary)
                .clickable {
                    val cardioSet = LoggedSet(
                        exerciseId = -1,
                        exerciseName = workoutType.name.replace("_", " "),
                        reps = null,
                        weightKg = distance.toDoubleOrNull(),
                        durationSeconds = duration.toIntOrNull()?.times(60),
                        xpEarned = estimateXp(
                            null,
                            distance.toDoubleOrNull(),
                            duration.toIntOrNull()?.times(60)
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
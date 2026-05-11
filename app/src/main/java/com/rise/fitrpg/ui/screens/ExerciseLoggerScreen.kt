package com.rise.fitrpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    var elapsedSeconds by remember { mutableStateOf(0) }

    // Timer
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
        }
    }

    val muscleGroups = MuscleGroup.entries.toList()
    val exercises = remember(selectedMuscleGroup) {
        if (selectedMuscleGroup == null) emptyList()
        else ExerciseLibrary.getByMuscleGroup(selectedMuscleGroup!!)
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
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "✕",
                color = TextSecondary,
                fontSize = 20.sp,
                modifier = Modifier.clickable { onDismiss() }
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
            // Total XP preview
            Text(
                text = "+${loggedSets.sumOf { it.xpEarned }} XP",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PurpleLight
            )
        }

        // ── MUSCLE GROUP SELECTOR ─────────────────────────
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
            items(muscleGroups) { group ->
                val isSelected = selectedMuscleGroup == group
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) PurplePrimary
                            else CardDark
                        )
                        .border(
                            1.dp,
                            if (isSelected) PurplePrimary else BorderColor,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedMuscleGroup = group }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = group.name.replace("_", " "),
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── EXERCISE LIST ─────────────────────────────────
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedMuscleGroup == null) {
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

            // Logged sets summary
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

        // ── FINISH BUTTON ─────────────────────────────────
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

    // ── ADD SET BOTTOM SHEET ──────────────────────────────
    if (showBottomSheet && selectedExercise != null) {
        AddSetBottomSheet(
            exercise = selectedExercise!!,
            onAdd = { set ->
                loggedSets = loggedSets + set
                showBottomSheet = false
            },
            onDismiss = { showBottomSheet = false }
        )
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
    onAdd: (LoggedSet) -> Unit,
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


            // Sets input
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

            // Reps input — always shown unless pure timed
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

            // Weight input — shown for non-bodyweight exercises
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

            // Duration input — shown for timed exercises
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

            // Add button
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
                        newSets.forEach { onAdd(it) }
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

// ── DATA MODELS ───────────────────────────────────────────
data class LoggedSet(
    val exerciseId: Int,
    val exerciseName: String,
    val reps: Int? = null,
    val weightKg: Double? = null,
    val durationSeconds: Int? = null,
    val xpEarned: Int = 0
)

// ── HELPERS ───────────────────────────────────────────────
fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

// Simple XP estimate for UI preview — real XP calculated by XpSystem on save
fun estimateXp(reps: Int?, weightKg: Double?, durationSeconds: Int?): Int {
    return when {
        weightKg != null && reps != null -> ((weightKg * reps) * 0.1).toInt().coerceAtLeast(5)
        reps != null -> (reps * 0.5).toInt().coerceAtLeast(5)
        durationSeconds != null -> (durationSeconds * 0.2).toInt().coerceAtLeast(5)
        else -> 5
    }
}
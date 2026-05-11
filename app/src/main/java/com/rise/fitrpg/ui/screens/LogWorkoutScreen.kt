package com.rise.fitrpg.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rise.fitrpg.data.models.WorkoutType
import com.rise.fitrpg.ui.theme.BackgroundDark
import com.rise.fitrpg.ui.theme.BorderColor
import com.rise.fitrpg.ui.theme.CardDark
import com.rise.fitrpg.ui.theme.OutfitFamily
import com.rise.fitrpg.ui.theme.PurpleLight
import com.rise.fitrpg.ui.theme.PurplePrimary
import com.rise.fitrpg.ui.theme.PurpleSurface
import com.rise.fitrpg.ui.theme.RajdhaniFamily
import com.rise.fitrpg.ui.theme.TextMuted
import com.rise.fitrpg.ui.theme.TextPrimary
import com.rise.fitrpg.ui.theme.TextSecondary

// ── WORKOUT TYPE DATA ─────────────────────────────────────
data class WorkoutTypeOption(
    val type: WorkoutType,
    val label: String,
    val emoji: String,
    val description: String,
    val color: Color
)

val mainWorkoutTypes = listOf(
    WorkoutTypeOption(
        WorkoutType.WEIGHT_TRAINING, "Weightlifting", "🏋️",
        "Barbell, dumbbell, machine exercises", Color(0xFFE53935)
    ),
    WorkoutTypeOption(
        WorkoutType.CALISTHENICS, "Calisthenics", "💪",
        "Bodyweight movements, gymnastics", Color(0xFF7C3AED)
    ),
    WorkoutTypeOption(
        WorkoutType.RUNNING, "Running", "🏃",
        "Outdoor or treadmill running", Color(0xFF0EA5E9)
    ),
    WorkoutTypeOption(
        WorkoutType.MIXED, "Mixed", "⚡",
        "Combine any workout types", Color(0xFFEAB308)
    )
)

val extendedWorkoutTypes = listOf(
    WorkoutTypeOption(
        WorkoutType.CYCLING, "Cycling", "🚴",
        "Road, mountain or stationary bike", Color(0xFF0D9488)
    ),
    WorkoutTypeOption(
        WorkoutType.SWIMMING, "Swimming", "🏊",
        "Pool or open water swimming", Color(0xFF0EA5E9)
    ),
    WorkoutTypeOption(
        WorkoutType.HIKING, "Hiking", "🥾",
        "Trail walking and hiking", Color(0xFF16A34A)
    ),
    WorkoutTypeOption(
        WorkoutType.YOGA, "Yoga", "🧘",
        "Flexibility and mindfulness", Color(0xFF0D9488)
    ),
    WorkoutTypeOption(
        WorkoutType.PILATES, "Pilates", "🤸",
        "Core strength and flexibility", Color(0xFF7C3AED)
    ),
    WorkoutTypeOption(
        WorkoutType.HIIT, "HIIT", "🔥",
        "High intensity interval training", Color(0xFFF97316)
    ),
    WorkoutTypeOption(
        WorkoutType.SPORT, "Sport", "🏅",
        "Basketball, football, tennis...", Color(0xFFEAB308)
    )
)

// ── WORKOUT TYPE PICKER SCREEN ────────────────────────────
@Composable
fun WorkoutTypePickerScreen(
    onTypeSelected: (WorkoutType) -> Unit,
    onDismiss: () -> Unit
) {
    var showMore by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.size(40.dp))

            Text(
                text = "LOG WORKOUT",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            Box(modifier = Modifier.size(20.dp))
        }

        Text(
            text = "What are you training today?",
            fontFamily = OutfitFamily,
            fontSize = 14.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main types
            items(mainWorkoutTypes) { option ->
                WorkoutTypeCard(
                    option = option,
                    onClick = { onTypeSelected(option.type) }
                )
            }

            // View More button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .clickable { showMore = !showMore }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showMore) "▲  Show Less" else "▼  View More",
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = PurpleLight
                    )
                }
            }

            // Extended types
            item {
                AnimatedVisibility(
                    visible = showMore,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        extendedWorkoutTypes.forEach { option ->
                            WorkoutTypeCard(
                                option = option,
                                onClick = { onTypeSelected(option.type) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ── WORKOUT TYPE CARD ─────────────────────────────────────
@Composable
fun WorkoutTypeCard(
    option: WorkoutTypeOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, option.color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(option.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = option.emoji, fontSize = 22.sp)
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Text(
                text = option.description,
                fontFamily = OutfitFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        // Arrow
        Text(
            text = "›",
            fontSize = 24.sp,
            color = option.color.copy(alpha = 0.6f)
        )
    }
}
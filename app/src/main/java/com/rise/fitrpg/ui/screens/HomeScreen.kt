package com.rise.fitrpg.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rise.fitrpg.R
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.ui.theme.AdventurerColor
import com.rise.fitrpg.ui.theme.BackgroundDark
import com.rise.fitrpg.ui.theme.BlueAccent
import com.rise.fitrpg.ui.theme.BorderColor
import com.rise.fitrpg.ui.theme.CardDark
import com.rise.fitrpg.ui.theme.ChampionColor
import com.rise.fitrpg.ui.theme.GoldColor
import com.rise.fitrpg.ui.theme.MonkColor
import com.rise.fitrpg.ui.theme.OrangeAccent
import com.rise.fitrpg.ui.theme.OutfitFamily
import com.rise.fitrpg.ui.theme.ParagonColor
import com.rise.fitrpg.ui.theme.PurpleLight
import com.rise.fitrpg.ui.theme.PurplePrimary
import com.rise.fitrpg.ui.theme.PurpleSurface
import com.rise.fitrpg.ui.theme.RajdhaniFamily
import com.rise.fitrpg.ui.theme.RogueColor
import com.rise.fitrpg.ui.theme.StrikerColor
import com.rise.fitrpg.ui.theme.TextMuted
import com.rise.fitrpg.ui.theme.TextPrimary
import com.rise.fitrpg.ui.theme.TextSecondary
import com.rise.fitrpg.ui.viewmodel.HomeViewModel

// ── CLASS → COLOR MAPPING ─────────────────────────────────
fun FitnessClass.toColor(): Color = when (this) {
    FitnessClass.CHAMPION   -> ChampionColor
    FitnessClass.STRIKER    -> StrikerColor
    FitnessClass.ROGUE      -> RogueColor
    FitnessClass.MONK       -> MonkColor
    FitnessClass.ADVENTURER -> AdventurerColor
    FitnessClass.PARAGON    -> ParagonColor
}

// ── CLASS → DRAWABLE MAPPING ──────────────────────────────
// All classes use the same placeholder image for now.
// When you have all 6 character images, replace R.drawable.char_placeholder
// with the correct drawable per class (e.g. R.drawable.char_rogue).
// Zero code changes needed — just swap the drawable reference here.
fun FitnessClass.toCharacterImage(): Int = when (this) {
    FitnessClass.CHAMPION   -> R.drawable.char_placeholder
    FitnessClass.STRIKER    -> R.drawable.char_placeholder
    FitnessClass.ROGUE      -> R.drawable.char_placeholder
    FitnessClass.MONK       -> R.drawable.char_placeholder
    FitnessClass.ADVENTURER -> R.drawable.char_placeholder
    FitnessClass.PARAGON    -> R.drawable.char_placeholder
}

// ── HOME SCREEN ───────────────────────────────────────────
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogWorkout: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val activeQuests by viewModel.activeQuests.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── CHARACTER IMAGE (background layer) ────────────
        user?.let { u ->
            Image(
                painter = painterResource(u.currentClass.toCharacterImage()),
                contentDescription = "Character",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .offset(x = 20.dp)
                    .height(620.dp)
                    .align(Alignment.TopCenter)
            )

            // Gradient fades over the character image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                BackgroundDark,
                                BackgroundDark.copy(alpha = 0.85f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 700f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                BackgroundDark.copy(alpha = 0.6f),
                                BackgroundDark
                            ),
                            startY = 400f,
                            endY = 1000f
                        )
                    )
            )
        }

        // ── ALL UI (foreground layer) ──────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar — gold pill
            user?.let { u ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 26.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    GoldPill(gold = u.gold)
                }

                // Player name + class badge
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = "WELCOME BACK",
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        letterSpacing = 2.5.sp,
                        color = u.currentClass.toColor().copy(alpha = 0.6f)
                    )
                    Text(
                        text = u.name,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 46.sp,
                        color = TextPrimary,
                        lineHeight = 46.sp
                    )
                    ClassBadge(user = u)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // XP Bar
                val classProgress = viewModel.getCurrentClassProgress(u)
                val fraction = viewModel.getLevelProgressFraction(u).toFloat()
                val xpForNext = viewModel.getXpForNextLevel(u)
                val currentXp = classProgress?.xp ?: 0
                val level = classProgress?.level ?: 0

                XpBar(
                    currentXp = currentXp,
                    xpForNext = xpForNext,
                    fraction = fraction,
                    level = level,
                    classColor = u.currentClass.toColor()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Log Workout Button
                LogWorkoutButton(
                    classColor = u.currentClass.toColor(),
                    onClick = onLogWorkout
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stat Cards
                StatCardsRow(user = u, viewModel = viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Active Quests
                if (activeQuests.isNotEmpty()) {
                    SectionHeader(title = "ACTIVE QUESTS", showViewAll = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        activeQuests.take(2).forEach { quest ->
                            QuestCard(quest = quest)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recent Workouts
                if (recentWorkouts.isNotEmpty()) {
                    SectionHeader(title = "RECENT", showViewAll = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        recentWorkouts.forEach { session ->
                            WorkoutRow(session = session)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Loading state
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        color = TextSecondary,
                        fontFamily = OutfitFamily
                    )
                }
            }
        }
    }
}

// ── GOLD PILL ─────────────────────────────────────────────
@Composable
fun GoldPill(gold: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GoldColor.copy(alpha = 0.1f))
            .border(1.dp, GoldColor.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(GoldColor)
        )
        Text(
            text = gold.toString(),
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = GoldColor
        )
    }
}

// ── CLASS BADGE ───────────────────────────────────────────
@Composable
fun ClassBadge(user: User) {
    val classColor = user.currentClass.toColor()
    val classProgress = user.classMap[user.currentClass]
    val level = classProgress?.level ?: 0

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(classColor.copy(alpha = 0.15f))
            .border(1.dp, classColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(start = 5.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(classColor.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.currentClass.toEmoji(),
                fontSize = 10.sp
            )
        }
        Text(
            text = user.currentClass.name
                .lowercase()
                .replaceFirstChar { it.uppercase() },
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = classColor,
            letterSpacing = 1.sp
        )
        Text(
            text = "· Lv $level",
            fontFamily = OutfitFamily,
            fontSize = 10.sp,
            color = classColor.copy(alpha = 0.45f)
        )
    }
}

fun FitnessClass.toEmoji(): String = when (this) {
    FitnessClass.CHAMPION   -> "⚔"
    FitnessClass.STRIKER    -> "👊"
    FitnessClass.ROGUE      -> "🗡"
    FitnessClass.MONK       -> "🧘"
    FitnessClass.ADVENTURER -> "🏃"
    FitnessClass.PARAGON    -> "👑"
}

// ── XP BAR ────────────────────────────────────────────────
@Composable
fun XpBar(
    currentXp: Int,
    xpForNext: Int,
    fraction: Float,
    level: Int,
    classColor: Color
) {
    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "XP",
                fontFamily = OutfitFamily,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = TextMuted
            )
            Text(
                text = "$currentXp / $xpForNext",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = classColor.copy(alpha = 0.75f)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                classColor.copy(alpha = 0.7f),
                                classColor
                            )
                        )
                    )
            )
        }
    }
}

// ── LOG WORKOUT BUTTON ────────────────────────────────────
@Composable
fun LogWorkoutButton(
    classColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(classColor.copy(alpha = 0.12f))
            .border(1.dp, classColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(classColor.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", color = classColor, fontSize = 16.sp,
                    fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "LOG WORKOUT",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.5.sp,
                color = classColor
            )
        }
    }
}

// ── STAT CARDS ROW ────────────────────────────────────────
@Composable
fun StatCardsRow(user: User, viewModel: HomeViewModel) {
    val classProgress = viewModel.getCurrentClassProgress(user)
    val streakMultiplier = viewModel.getStreakMultiplier(user)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // Overall level
        StatCard(
            modifier = Modifier.weight(1f),
            topColor = PurplePrimary,
            value = user.overallLevel.toString(),
            label = "OVERALL"
        )

        // This week
        StatCard(
            modifier = Modifier.weight(1f),
            topColor = BlueAccent,
            label = "THIS WEEK",
            content = {
                WeekDots(count = user.weeklyWorkoutCount)
                Text(
                    text = user.weeklyWorkoutCount.toString(),
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BlueAccent
                )
            }
        )

        // Streak
        StatCard(
            modifier = Modifier.weight(1f),
            topColor = OrangeAccent,
            label = "STREAK",
            content = {
                Text(
                    text = user.currentStreak.toString(),
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = OrangeAccent
                )
                Text(
                    text = user.streakTier.name,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 8.sp,
                    letterSpacing = 1.5.sp,
                    color = OrangeAccent.copy(alpha = 0.7f)
                )
            }
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    topColor: Color,
    value: String? = null,
    label: String,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Colored top line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(topColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (content != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        } else {
            Text(
                text = value ?: "0",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = topColor
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
            color = TextMuted
        )
    }
}

@Composable
fun WeekDots(count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.padding(bottom = 3.dp)
    ) {
        (1..7).forEach { day ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (day <= count) BlueAccent
                        else Color.White.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

// ── SECTION HEADER ────────────────────────────────────────
@Composable
fun SectionHeader(title: String, showViewAll: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 2.5.sp,
            color = TextMuted
        )
        if (showViewAll) {
            Text(
                text = "VIEW ALL",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = PurplePrimary
            )
        }
    }
}

// ── QUEST CARD ────────────────────────────────────────────
@Composable
fun QuestCard(quest: com.rise.fitrpg.data.models.Quest) {
    val rarityColor = when (quest.rarity) {
        com.rise.fitrpg.data.models.QuestRarity.COMMON   -> Color.Gray
        com.rise.fitrpg.data.models.QuestRarity.UNCOMMON -> BlueAccent
        com.rise.fitrpg.data.models.QuestRarity.RARE     -> PurpleLight
    }
    val progress = if (quest.targetValue > 0)
        (quest.currentProgress.toFloat() / quest.targetValue)
    else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        // Left colored bar
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(64.dp)
                .background(rarityColor)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quest icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(rarityColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 14.sp)
            }

            // Quest info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = quest.description,
                    fontFamily = OutfitFamily,
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right side — rarity + XP + progress bar
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = quest.rarity.name,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp,
                    color = rarityColor.copy(alpha = 0.55f)
                )
                Text(
                    text = "+${quest.xpReward} XP",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = rarityColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(2.dp)
                            .background(rarityColor)
                    )
                }
            }
        }
    }
}

// ── WORKOUT ROW ───────────────────────────────────────────
@Composable
fun WorkoutRow(session: com.rise.fitrpg.data.models.WorkoutSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardDark)
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(PurplePrimary)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Workout · ${session.fitnessClass.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }}",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.85f)
            )
            Text(
                text = "Today · ${session.sets.size} sets",
                fontFamily = OutfitFamily,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
        Text(
            text = "+${session.totalXpEarned} XP",
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = PurpleLight.copy(alpha = 0.7f)
        )
    }
}
package com.rise.fitrpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.QuestRarity
import com.rise.fitrpg.data.models.QuestType
import com.rise.fitrpg.ui.theme.*
import com.rise.fitrpg.ui.viewmodel.QuestViewModel
import androidx.compose.foundation.clickable

// ── CLASS COLOR MAP ───────────────────────────────────────
// Maps FitnessClass to its locked color. Used for quest card
// accent color — each quest is colored by its targetClass.

private fun FitnessClass.color(): Color = when (this) {
    FitnessClass.CHAMPION   -> ChampionColor
    FitnessClass.STRIKER    -> StrikerColor
    FitnessClass.ROGUE      -> RogueColor
    FitnessClass.MONK       -> MonkColor
    FitnessClass.ADVENTURER -> AdventurerColor
    FitnessClass.PARAGON    -> ParagonColor
}

// ── RARITY COLOR ──────────────────────────────────────────

private fun QuestRarity.color(): Color = when (this) {
    QuestRarity.COMMON   -> Color(0xFF9CA3AF) // grey
    QuestRarity.UNCOMMON -> Color(0xFF34D399) // green
    QuestRarity.RARE     -> Color(0xFFFBBF24) // gold
}

private fun QuestRarity.label(): String = when (this) {
    QuestRarity.COMMON   -> "COMMON"
    QuestRarity.UNCOMMON -> "UNCOMMON"
    QuestRarity.RARE     -> "RARE"
}

// ── QUEST TYPE ICON ───────────────────────────────────────

private fun QuestType.icon(): String = when (this) {
    QuestType.COMPLETE_WORKOUTS    -> "⚔️"
    QuestType.RUN_DISTANCE         -> "🏃"
    QuestType.HIT_STREAK           -> "🔥"
    QuestType.LOG_MUSCLE_GROUP_SETS -> "💪"
}

// ── QUEST TYPE UNIT ───────────────────────────────────────
// The unit label shown in the progress fraction (e.g. "2/5 km").

private fun QuestType.unit(): String = when (this) {
    QuestType.COMPLETE_WORKOUTS    -> "sessions"
    QuestType.RUN_DISTANCE         -> "km"
    QuestType.HIT_STREAK           -> "days"
    QuestType.LOG_MUSCLE_GROUP_SETS -> "sets"
}

// ── QUEST SCREEN ──────────────────────────────────────────

@Composable
fun QuestScreen(viewModel: QuestViewModel) {
    val activeQuests by viewModel.activeQuests.collectAsStateWithLifecycle()
    val completedQuests by viewModel.completedQuests.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // 0 = Active tab, 1 = Completed tab
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── HEADER ────────────────────────────────────────
        Spacer(modifier = Modifier.height(52.dp))

        Text(
            text = "QUESTS",
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Text(
            text = "Complete quests to earn XP and gold",
            fontFamily = OutfitFamily,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── TABS ──────────────────────────────────────────
        QuestTabs(
            selectedTab = selectedTab,
            activeCount = activeQuests.size,
            completedCount = completedQuests.size,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── CONTENT ───────────────────────────────────────
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading quests...",
                        fontFamily = OutfitFamily,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }

            selectedTab == 0 -> {
                QuestList(
                    quests = activeQuests,
                    emptyMessage = "No active quests.\nCheck back after your next workout.",
                    isCompleted = false
                )
            }

            else -> {
                QuestList(
                    quests = completedQuests,
                    emptyMessage = "No completed quests yet.\nFinish a quest to see it here.",
                    isCompleted = true
                )
            }
        }
    }
}

// ── QUEST TABS ────────────────────────────────────────────

@Composable
private fun QuestTabs(
    selectedTab: Int,
    activeCount: Int,
    completedCount: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        listOf(
            "Active" to activeCount,
            "Completed" to completedCount
        ).forEachIndexed { index, (label, count) ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PurpleSurface else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isSelected) PurpleLight else TextMuted,
                        letterSpacing = 1.sp
                    )
                    // Count badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) PurplePrimary.copy(alpha = 0.3f)
                                else BorderColor
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            fontFamily = OutfitFamily,
                            fontSize = 11.sp,
                            color = if (isSelected) PurpleLight else TextMuted
                        )
                    }
                }
            }
        }
    }
}

// ── QUEST LIST ────────────────────────────────────────────

@Composable
private fun QuestList(
    quests: List<Quest>,
    emptyMessage: String,
    isCompleted: Boolean
) {
    if (quests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                fontFamily = OutfitFamily,
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(quests, key = { it.id }) { quest ->
            QuestCard(quest = quest, isCompleted = isCompleted)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ── QUEST CARD ────────────────────────────────────────────
// Each card is accented by the quest's targetClass color.
// Completed cards are dimmed and show a checkmark.

@Composable
private fun QuestCard(quest: Quest, isCompleted: Boolean) {
    val accentColor = if (isCompleted) TextMuted else quest.targetClass.color()
    val cardAlpha = if (isCompleted) 0.5f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(
                1.dp,
                accentColor.copy(alpha = if (isCompleted) 0.15f else 0.3f),
                RoundedCornerShape(16.dp)
            )
    ) {
        // Left accent bar — class color
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(accentColor.copy(alpha = cardAlpha))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── TOP ROW: icon + title + rarity + completed check ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Quest type icon
                    Text(
                        text = quest.type.icon(),
                        fontSize = 22.sp
                    )

                    Column {
                        Text(
                            text = quest.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary.copy(alpha = cardAlpha)
                        )
                        // Balance quest tag
                        if (quest.isBalanceQuest) {
                            Text(
                                text = "BALANCE QUEST",
                                fontFamily = OutfitFamily,
                                fontSize = 9.sp,
                                color = OrangeAccent.copy(alpha = cardAlpha),
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }

                // Completed checkmark OR rarity badge
                if (isCompleted) {
                    Text(
                        text = "✓",
                        fontSize = 18.sp,
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    RarityBadge(rarity = quest.rarity)
                }
            }

            // ── DESCRIPTION ───────────────────────────────
            Text(
                text = quest.description,
                fontFamily = OutfitFamily,
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = cardAlpha),
                lineHeight = 17.sp
            )

            // ── PROGRESS BAR + FRACTION ───────────────────
            // Shown on both active and completed — completed always shows full bar.
            QuestProgress(quest = quest, isCompleted = isCompleted, accentColor = accentColor)

            // ── REWARDS ROW ───────────────────────────────
            RewardsRow(quest = quest, cardAlpha = cardAlpha)
        }
    }
}

// ── RARITY BADGE ─────────────────────────────────────────

@Composable
private fun RarityBadge(rarity: QuestRarity) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(rarity.color().copy(alpha = 0.15f))
            .border(1.dp, rarity.color().copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = rarity.label(),
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            color = rarity.color(),
            letterSpacing = 1.sp
        )
    }
}

// ── QUEST PROGRESS ────────────────────────────────────────

@Composable
private fun QuestProgress(
    quest: Quest,
    isCompleted: Boolean,
    accentColor: Color
) {
    val fraction = if (isCompleted) 1.0 else quest.progressFraction
    val progress = if (isCompleted) quest.targetValue else quest.currentProgress
    val unit = quest.type.unit()

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        // Progress bar track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.toFloat().coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(accentColor)
            )
        }

        // Fraction label
        Text(
            text = "$progress / ${quest.targetValue} $unit",
            fontFamily = OutfitFamily,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

// ── REWARDS ROW ───────────────────────────────────────────

@Composable
private fun RewardsRow(quest: Quest, cardAlpha: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // XP reward
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "⚡", fontSize = 12.sp)
            Text(
                text = "+${quest.xpReward} XP",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = PurpleLight.copy(alpha = cardAlpha)
            )
        }

        // Gold reward — only shown if non-zero
        if (quest.goldReward > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🪙", fontSize = 12.sp)
                Text(
                    text = "+${quest.goldReward}",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = GoldColor.copy(alpha = cardAlpha)
                )
            }
        }
    }
}
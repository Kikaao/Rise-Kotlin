package com.rise.fitrpg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rise.fitrpg.R
import com.rise.fitrpg.ui.Routes
import com.rise.fitrpg.ui.theme.BackgroundDark
import com.rise.fitrpg.ui.theme.BorderColor
import com.rise.fitrpg.ui.theme.PurpleLight
import com.rise.fitrpg.ui.theme.PurplePrimary
import com.rise.fitrpg.ui.theme.TextMuted

data class NavItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

@Composable
fun RiseBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem(Routes.HOME,      "Home",      R.drawable.ic_home),
        NavItem(Routes.QUESTS,    "Quests",    R.drawable.ic_quests),
        NavItem(Routes.CHARACTER, "Character", R.drawable.ic_character),
        NavItem(Routes.FEATS,     "Feats",     R.drawable.ic_feats)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(top = 1.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        NavTab(
            item = items[0],
            isActive = currentRoute == items[0].route,
            onClick = { onNavigate(items[0].route) }
        )

        // Quests
        NavTab(
            item = items[1],
            isActive = currentRoute == items[1].route,
            onClick = { onNavigate(items[1].route) }
        )

        // Center Log button
        LogCenterButton(
            isActive = currentRoute == Routes.LOG,
            onClick = { onNavigate(Routes.LOG) }
        )

        // Character
        NavTab(
            item = items[2],
            isActive = currentRoute == items[2].route,
            onClick = { onNavigate(items[2].route) }
        )

        // Feats
        NavTab(
            item = items[3],
            isActive = currentRoute == items[3].route,
            onClick = { onNavigate(items[3].route) }
        )
    }
}

@Composable
private fun NavTab(
    item: NavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = if (isActive) PurpleLight else TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = item.label,
            fontSize = 9.sp,
            color = if (isActive) PurpleLight else TextMuted
        )
        // Active dot
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isActive) PurplePrimary else Color.Transparent)
        )
    }
}

@Composable
private fun LogCenterButton(
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isActive) PurpleLight
                    else PurplePrimary
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Log Workout",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = "Log",
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}
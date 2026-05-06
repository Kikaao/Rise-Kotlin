package com.rise.fitrpg.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Routes {
    const val HOME      = "home"
    const val QUESTS    = "quests"
    const val LOG       = "log"
    const val CHARACTER = "character"
    const val FEATS     = "feats"
}

@Composable
fun RiseNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            PlaceholderScreen("Home")
        }
        composable(Routes.QUESTS) {
            PlaceholderScreen("Quests")
        }
        composable(Routes.LOG) {
            PlaceholderScreen("Log Workout")
        }
        composable(Routes.CHARACTER) {
            PlaceholderScreen("Character")
        }
        composable(Routes.FEATS) {
            PlaceholderScreen("Feats")
        }
    }
}
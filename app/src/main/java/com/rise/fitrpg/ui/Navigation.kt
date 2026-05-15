package com.rise.fitrpg.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rise.fitrpg.data.repository.CardioRepository
import com.rise.fitrpg.data.repository.QuestRepository
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.ui.screens.HomeScreen
import com.rise.fitrpg.ui.screens.QuestScreen
import com.rise.fitrpg.ui.screens.WorkoutTypePickerScreen
import com.rise.fitrpg.ui.viewmodel.HomeViewModel
import com.rise.fitrpg.ui.viewmodel.QuestViewModel
import com.rise.fitrpg.ui.viewmodel.QuestViewModelFactory

object Routes {
    const val HOME      = "home"
    const val QUESTS    = "quests"
    const val LOG       = "log"
    const val CHARACTER = "character"
    const val FEATS     = "feats"
}

@Composable
fun RiseNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    questRepository: QuestRepository,
    userRepository: UserRepository,
    workoutRepository: WorkoutRepository,
    cardioRepository: CardioRepository,
    onLogWorkout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onLogWorkout = onLogWorkout
            )
        }

        composable(Routes.QUESTS) {
            val questViewModel: QuestViewModel = viewModel(
                factory = QuestViewModelFactory(
                    questRepository   = questRepository,
                    userRepository    = userRepository,
                    workoutRepository = workoutRepository,
                    cardioRepository  = cardioRepository
                )
            )
            QuestScreen(viewModel = questViewModel)
        }

        composable(Routes.CHARACTER) {
            PlaceholderScreen("Character")
        }

        composable(Routes.FEATS) {
            PlaceholderScreen("Feats")
        }
    }
}
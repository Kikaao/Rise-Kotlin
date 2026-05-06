package com.rise.fitrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rise.fitrpg.data.database.RiseDatabase
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.StreakTier
import com.rise.fitrpg.data.models.User
import com.rise.fitrpg.data.repository.CardioRepository
import com.rise.fitrpg.data.repository.QuestRepository
import com.rise.fitrpg.data.repository.UserRepository
import com.rise.fitrpg.data.repository.WorkoutRepository
import com.rise.fitrpg.ui.RiseNavGraph
import com.rise.fitrpg.ui.components.RiseBottomNav
import com.rise.fitrpg.ui.theme.BackgroundDark
import com.rise.fitrpg.ui.theme.RiseTheme
import com.rise.fitrpg.ui.viewmodel.HomeViewModel
import com.rise.fitrpg.ui.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var cardioRepository: CardioRepository
    private lateinit var questRepository: QuestRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = RiseDatabase.getInstance(this)
        userRepository    = UserRepository(
            db.userDao(),
            db.classProgressDao(),
            db.playerProfileDao(),
            db.inventoryDao()
        )
        workoutRepository = WorkoutRepository(db.workoutDao())
        cardioRepository  = CardioRepository(db.cardioDao())
        questRepository   = QuestRepository(db.questDao())

        // Seed a default user on first launch
        lifecycleScope.launch {
            seedDefaultUserIfNeeded()
        }

        setContent {
            RiseTheme {
                RiseApp(
                    userRepository    = userRepository,
                    workoutRepository = workoutRepository,
                    cardioRepository  = cardioRepository,
                    questRepository   = questRepository
                )
            }
        }
    }

    // Creates a default user if none exists.
    // Runs once on first install — skipped on subsequent launches.
    private suspend fun seedDefaultUserIfNeeded() {
        val existing = userRepository.getUser(AppConstants.CURRENT_USER_ID)
        if (existing != null) return

        val defaultUser = User(
            id = AppConstants.CURRENT_USER_ID,
            name = "Kael",
            email = "kael@rise.app",
            createdAt = System.currentTimeMillis(),
            currentClass = FitnessClass.ADVENTURER,
            streakTier = StreakTier.KNIGHT,
            currentStreak = 23,
            weeklyWorkoutCount = 3,
            weekStartDate = System.currentTimeMillis(),
            gold = 740
        )
        userRepository.createUser(defaultUser)
    }
}

@Composable
fun RiseApp(
    userRepository: UserRepository,
    workoutRepository: WorkoutRepository,
    cardioRepository: CardioRepository,
    questRepository: QuestRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            userRepository    = userRepository,
            workoutRepository = workoutRepository,
            cardioRepository  = cardioRepository,
            questRepository   = questRepository
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            RiseNavGraph(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }

        RiseBottomNav(
            currentRoute = currentRoute,
            onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
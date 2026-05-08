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
import com.rise.fitrpg.data.models.Quest
import com.rise.fitrpg.data.models.QuestRarity
import com.rise.fitrpg.data.models.QuestType
import com.rise.fitrpg.data.models.WorkoutSession
import com.rise.fitrpg.data.models.WorkoutType


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

            setContent {
                RiseTheme {
                    RiseApp(
                        userRepository = userRepository,
                        workoutRepository = workoutRepository,
                        cardioRepository = cardioRepository,
                        questRepository = questRepository
                    )
                }
            }
        }
    }

    // Creates a default user if none exists.
    // Runs once on first install — skipped on subsequent launches.
    private suspend fun seedDefaultUserIfNeeded() {
        val existing = userRepository.getUser(AppConstants.CURRENT_USER_ID)
        if (existing != null) return

        val nowMs = System.currentTimeMillis()
        val weekMs = 7 * 24 * 60 * 60 * 1000L

        // ── Seed user ──
        val defaultUser = User(
            id = AppConstants.CURRENT_USER_ID,
            name = "Kael",
            email = "kael@rise.app",
            createdAt = nowMs,
            currentClass = FitnessClass.ADVENTURER,
            streakTier = StreakTier.KNIGHT,
            currentStreak = 23,
            weeklyWorkoutCount = 3,
            weekStartDate = nowMs,
            gold = 740
        )
        userRepository.createUser(defaultUser)

        // ── Seed quests ──
        val quests = listOf(
            Quest(
                id = "quest_trail_blazer_1",
                type = QuestType.RUN_DISTANCE,
                rarity = QuestRarity.UNCOMMON,
                title = "Trail Blazer",
                description = "Log 5 cardio sessions this week",
                targetClass = FitnessClass.ADVENTURER,
                isBalanceQuest = false,
                targetValue = 5,
                currentProgress = 2,
                isCompleted = false,
                xpReward = 350,
                goldReward = 50,
                weekStartMs = nowMs,
                expiryMs = nowMs + weekMs
            ),
            Quest(
                id = "quest_unbroken_1",
                type = QuestType.HIT_STREAK,
                rarity = QuestRarity.RARE,
                title = "Unbroken",
                description = "Maintain a 10-day workout streak",
                targetClass = FitnessClass.ADVENTURER,
                isBalanceQuest = false,
                targetValue = 10,
                currentProgress = 1,
                isCompleted = false,
                xpReward = 1000,
                goldReward = 150,
                weekStartMs = nowMs,
                expiryMs = nowMs + weekMs
            )
        )
        questRepository.saveQuests(quests, AppConstants.CURRENT_USER_ID)

        // ── Seed recent workouts ──
        val session1 = WorkoutSession(
            id = 1,
            userId = AppConstants.CURRENT_USER_ID,
            date = nowMs - (1 * 60 * 60 * 1000L),
            fitnessClass = FitnessClass.ADVENTURER,
            workoutType = WorkoutType.RUNNING,
            sets = emptyList(),
            totalXpEarned = 284,
            durationSeconds = 3120
        )
        val session2 = WorkoutSession(
            id = 2,
            userId = AppConstants.CURRENT_USER_ID,
            date = nowMs - (24 * 60 * 60 * 1000L),
            fitnessClass = FitnessClass.ADVENTURER,
            workoutType = WorkoutType.RUNNING,
            sets = emptyList(),
            totalXpEarned = 190,
            durationSeconds = 1680
        )
        val session3 = WorkoutSession(
            id = 3,
            userId = AppConstants.CURRENT_USER_ID,
            date = nowMs - (48 * 60 * 60 * 1000L),
            fitnessClass = FitnessClass.CHAMPION,
            workoutType = WorkoutType.WEIGHT_TRAINING,
            sets = emptyList(),
            totalXpEarned = 310,
            durationSeconds = 3900
        )
        workoutRepository.saveWorkout(session1)
        workoutRepository.saveWorkout(session2)
        workoutRepository.saveWorkout(session3)
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
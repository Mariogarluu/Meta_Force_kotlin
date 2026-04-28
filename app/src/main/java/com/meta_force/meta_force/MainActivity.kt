package com.meta_force.meta_force

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meta_force.meta_force.ui.auth.login.LoginScreen
import com.meta_force.meta_force.ui.auth.register.RegisterScreen
import com.meta_force.meta_force.ui.dashboard.DashboardScreen
import com.meta_force.meta_force.ui.qr.QrScreen
import com.meta_force.meta_force.ui.theme.Meta_forceTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import com.meta_force.meta_force.worker.WaterReminderWorker
import com.meta_force.meta_force.utils.NotificationHelper
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupWorkManager()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this)
        
        // Check permissions and start WorkManager
        checkNotificationPermissionAndStartWork()
        setContent {
            Meta_forceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination by mainViewModel.startDestination.collectAsState()

                    // Note: In a real app, you might want to show a splash screen until startDestination is determined
                    // For now, we rely on the initial value "login" and the flow updating it.
                    
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                onNavigateToWorkouts = {
                                    navController.navigate("workouts")
                                },
                                onNavigateToDiets = {
                                    navController.navigate("diets")
                                },
                                onNavigateToClasses = {
                                    navController.navigate("classes")
                                },
                                onNavigateToAiChat = {
                                    navController.navigate("aichat")
                                },
                                onNavigateToQr = {
                                    navController.navigate("qr")
                                },
                                onNavigateToCenters = {
                                    navController.navigate("centers")
                                }
                            )
                        }
                        composable("profile") {
                            com.meta_force.meta_force.ui.profile.ProfileScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("workouts") {
                            com.meta_force.meta_force.ui.workouts.WorkoutsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { workoutId -> 
                                    navController.navigate("workout_detail/$workoutId")
                                },
                                onNavigateToCreate = {
                                    navController.navigate("workout_create")
                                }
                            )
                        }
                        composable("workout_create") {
                            com.meta_force.meta_force.ui.workouts.WorkoutCreationScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onWorkoutCreated = { workout ->
                                    // Regresar a la lista y actualizar
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("workout_detail/{workoutId}") { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
                            com.meta_force.meta_force.ui.workouts.WorkoutDetailScreen(
                                workoutId = workoutId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("diets") {
                            com.meta_force.meta_force.ui.diets.DietsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { dietId ->
                                    navController.navigate("diet_detail/$dietId")
                                },
                                onNavigateToCreate = {
                                    navController.navigate("diet_create")
                                }
                            )
                        }
                        composable("diet_create") {
                            com.meta_force.meta_force.ui.diets.DietCreationScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onDietCreated = { diet ->
                                    // Regresar a la lista y actualizar
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("diet_detail/{dietId}") { backStackEntry ->
                            val dietId = backStackEntry.arguments?.getString("dietId") ?: return@composable
                            com.meta_force.meta_force.ui.diets.DietDetailScreen(
                                dietId = dietId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("classes") {
                            com.meta_force.meta_force.ui.classes.ClassesScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("aichat") {
                            com.meta_force.meta_force.ui.aichat.AiChatScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("qr") {
                            QrScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("centers") {
                            com.meta_force.meta_force.ui.centers.CentersScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkNotificationPermissionAndStartWork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                setupWorkManager()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            setupWorkManager()
        }
    }

    private fun setupWorkManager() {
        // Encola un Worker periódico que se ejecute cada 2 horas
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(2, TimeUnit.HOURS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WaterReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
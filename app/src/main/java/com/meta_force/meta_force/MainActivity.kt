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
import com.meta_force.meta_force.ui.theme.Meta_forceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                                }
                            )
                        }
                        composable("profile") {
                            com.meta_force.meta_force.ui.profile.ProfileScreen()
                        }
                        composable("workouts") {
                            com.meta_force.meta_force.ui.workouts.WorkoutsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { workoutId -> 
                                    navController.navigate("workout_detail/$workoutId")
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
                    }
                }
            }
        }
    }
}
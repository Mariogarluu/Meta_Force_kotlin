package com.meta_force.meta_force.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.ui.diets.DayUtils
import kotlinx.coroutines.launch

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee)
private val DarkBg = Color(0xFF0f172a)
private val DarkSurface = Color(0xFF1e293b)
private val DarkSurfaceVariant = Color(0xFF334155)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDayIndex by viewModel.currentDayIndex.collectAsState()

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalle de Rutina",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is WorkoutDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryCyan
                        )
                    }
                }
                is WorkoutDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is WorkoutDetailUiState.Success -> {
                    WorkoutDetailContent(
                        workout = state.workout,
                        currentDayIndex = currentDayIndex,
                        onDayChanged = { viewModel.setDay(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutDetailContent(
    workout: Workout,
    currentDayIndex: Int,
    onDayChanged: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = currentDayIndex, pageCount = { 7 })
    val scope = rememberCoroutineScope()

    // Sincronizar el estado del pager con el ViewModel
    LaunchedEffect(pagerState.currentPage) {
        onDayChanged(pagerState.currentPage)
    }
    
    // Sincronizar el ViewModel con el pager (cuando se usan los botones de flecha)
    LaunchedEffect(currentDayIndex) {
        if (pagerState.currentPage != currentDayIndex) {
            pagerState.animateScrollToPage(currentDayIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera de la rutina
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (!workout.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )
            }
        }

        // Indicadores de día con navegación
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { 
                scope.launch { 
                    val prev = if (pagerState.currentPage == 0) 6 else pagerState.currentPage - 1
                    pagerState.animateScrollToPage(prev)
                }
            }) {
                Icon(Icons.Default.ArrowBack, "Día anterior", tint = PrimaryCyan)
            }

            Text(
                text = DayUtils.getDayName(pagerState.currentPage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = { 
                scope.launch { 
                    val next = if (pagerState.currentPage == 6) 0 else pagerState.currentPage + 1
                    pagerState.animateScrollToPage(next)
                }
            }) {
                Icon(Icons.Default.ArrowForward, "Día siguiente", tint = PrimaryCyan)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            DayWorkoutContent(workout = workout, dayIndex = page)
        }
    }
}

@Composable
fun DayWorkoutContent(workout: Workout, dayIndex: Int) {
    val exercisesByDay = workout.exercises.groupBy { it.dayOfWeek }
    val exercisesForCurrentDay = exercisesByDay[dayIndex] ?: emptyList()

    if (exercisesForCurrentDay.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay ejercicios programados para este día.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exercisesForCurrentDay) { exerciseRel ->
                ExerciseCard(exerciseRel = exerciseRel)
            }
        }
    }
}

@Composable
fun ExerciseCard(exerciseRel: com.meta_force.meta_force.data.model.WorkoutExercise) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = exerciseRel.exercise?.name ?: "Ejercicio desconocido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(label = "Series", value = exerciseRel.sets)
                InfoChip(label = "Reps", value = exerciseRel.reps)
                if (exerciseRel.weight != null) {
                    InfoChip(label = "Peso", value = "${exerciseRel.weight}kg")
                }
            }

            if (!exerciseRel.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💡 ${exerciseRel.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryCyan)
    }
}

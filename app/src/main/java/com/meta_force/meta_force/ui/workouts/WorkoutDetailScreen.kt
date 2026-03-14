package com.meta_force.meta_force.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Workout

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee)
private val PrimaryBlue = Color(0xFF3b82f6)
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

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Detalle de Rutina",
                        color = PrimaryCyan,
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is WorkoutDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryCyan
                    )
                }
                is WorkoutDetailUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WorkoutDetailUiState.Success -> {
                    WorkoutDetailContent(workout = state.workout)
                }
            }
        }
    }
}

@Composable
fun WorkoutDetailContent(workout: Workout) {
    val exercisesByDay = workout.exercises.groupBy { it.dayOfWeek }.toSortedMap()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
        
        Spacer(modifier = Modifier.height(24.dp))

        if (workout.exercises.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay ejercicios en esta rutina.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                exercisesByDay.forEach { (day, exercises) ->
                    item {
                        Text(
                            text = getDayName(day),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(exercises) { exerciseRel ->
                        ExerciseCard(exerciseRel = exerciseRel)
                    }
                }
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

fun getDayName(day: Int): String {
    return when(day) {
        0 -> "Domingo"
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        else -> "Día $day"
    }
}

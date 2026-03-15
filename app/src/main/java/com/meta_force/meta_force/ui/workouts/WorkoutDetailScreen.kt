package com.meta_force.meta_force.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meta_force.meta_force.ui.workouts.WorkoutDetailViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.ui.diets.DayUtils
import com.meta_force.meta_force.ui.theme.*
import kotlinx.coroutines.launch

// Brand Colors
private val PrimaryCyan = MF_Teal
private val DarkBg = MF_BlueDeep
private val DarkSurface = MF_BlueLight
private val DarkSurfaceVariant = Color(0xFF1D2D44)

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
                    containerColor = DarkSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is WorkoutDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                }
                is WorkoutDetailUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DarkBg),
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
                        onDayChanged = { viewModel.setDay(it) },
                        viewModel = viewModel
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
    onDayChanged: (Int) -> Unit,
    viewModel: WorkoutDetailViewModel
) {
    val pagerState = rememberPagerState(initialPage = currentDayIndex, pageCount = { 7 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onDayChanged(pagerState.currentPage)
    }
    
    LaunchedEffect(currentDayIndex) {
        if (pagerState.currentPage != currentDayIndex) {
            pagerState.animateScrollToPage(currentDayIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
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
            DayWorkoutContent(workout = workout, dayIndex = page, viewModel = viewModel)
        }
    }
}

@Composable
fun DayWorkoutContent(workout: Workout, dayIndex: Int, viewModel: WorkoutDetailViewModel) {
    val exercisesForDay = workout.exercises.filter { it.dayOfWeek == dayIndex }
        .sortedBy { it.order }

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedExerciseId by remember { mutableStateOf<String?>(null) }
    var selectedExerciseName by remember { mutableStateOf("") }
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()

    if (exercisesForDay.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay ejercicios programados para este día.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exercisesForDay) { item ->
                val exercise = item.exercise
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(
                            text = exercise?.name ?: "Ejercicio desconocido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoChip(label = "Series", value = item.sets?.toString() ?: "-")
                            InfoChip(label = "Reps", value = item.reps?.toString() ?: "-")
                            if (item.weight != null) {
                                InfoChip(label = "Peso", value = "${item.weight}kg")
                            }
                        }

                        if (!item.notes.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "💡 ${item.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedExerciseId = exercise?.id
                                selectedExerciseName = exercise?.name ?: ""
                                viewModel.loadExerciseHistory(exercise?.id ?: "")
                                showLogDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                        ) {
                            Text("Registrar Rendimiento", color = DarkBg)
                        }
                    }
                }
            }
        }
    }

    if (showLogDialog && selectedExerciseId != null) {
        var sets by remember { mutableStateOf("3") }
        var reps by remember { mutableStateOf("10") }
        var weight by remember { mutableStateOf("") }
        var logNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Registrar: $selectedExerciseName", color = Color.White) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            label = { Text("Series") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = logNotes,
                        onValueChange = { logNotes = it },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Último Historial", style = MaterialTheme.typography.labelMedium, color = PrimaryCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (exerciseHistory.isEmpty()) {
                        Text("Sin registros previos", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        exerciseHistory.take(3).forEach { log ->
                            Text(
                                text = "${log.date.take(10)}: ${log.weight}kg | ${log.sets}x${log.reps}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logPerformance(
                            selectedExerciseId!!,
                            sets.toIntOrNull(),
                            reps.toIntOrNull(),
                            weight.toDoubleOrNull(),
                            logNotes
                        ) {
                            showLogDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text("Guardar", color = DarkBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = DarkSurface,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
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

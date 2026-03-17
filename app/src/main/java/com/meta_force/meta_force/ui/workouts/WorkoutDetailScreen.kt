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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.R
import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.ui.diets.DayUtils
import com.meta_force.meta_force.ui.theme.MF_BlueDeep
import com.meta_force.meta_force.ui.theme.MF_BlueLight
import com.meta_force.meta_force.ui.theme.MF_Teal
import com.meta_force.meta_force.ui.workouts.WorkoutDetailViewModel
import com.meta_force.meta_force.ui.workouts.WorkoutDetailUiState
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.ExperimentalFoundationApi

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
    val isEditMode by viewModel.isEditMode.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()
    var showExerciseModal by remember { mutableStateOf(false) }

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedExerciseId by remember { mutableStateOf<String?>(null) }
    var selectedExerciseName by remember { mutableStateOf("") }
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.workout_detail_title),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    // Edit button removed as per user request
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
                        isEditMode = isEditMode,
                        onDayChanged = { viewModel.setDay(it) },
                        onLogPerformance = { id, name ->
                            selectedExerciseId = id
                            selectedExerciseName = name
                            viewModel.loadExerciseHistory(id)
                            showLogDialog = true
                        },
                        onRemoveExercise = { exerciseId -> viewModel.removeExerciseFromWorkout(state.workout.id!!, exerciseId) },
                        onAddExerciseClick = { showExerciseModal = true }
                    )

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
                                            label = { Text(stringResource(R.string.workout_sets)) },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = reps,
                                            onValueChange = { reps = it },
                                            label = { Text(stringResource(R.string.workout_reps)) },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = weight,
                                        onValueChange = { weight = it },
                                        label = { Text(stringResource(R.string.workout_weight)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = logNotes,
                                        onValueChange = { logNotes = it },
                                        label = { Text(stringResource(R.string.workout_notes)) },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(stringResource(R.string.workout_last_history), style = MaterialTheme.typography.labelMedium, color = PrimaryCyan)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (exerciseHistory.isEmpty()) {
                                        Text(stringResource(R.string.workout_no_history), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                                            sets,
                                            reps,
                                            weight.toDoubleOrNull(),
                                            logNotes
                                        ) {
                                            showLogDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                                ) {
                                    Text(stringResource(R.string.workout_save), color = DarkBg)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogDialog = false }) {
                                    Text(stringResource(R.string.workout_cancel), color = Color.White)
                                }
                            },
                            containerColor = DarkSurface,
                            titleContentColor = Color.White,
                            textContentColor = Color.LightGray
                        )
                    }

                    if (showExerciseModal) {
                        ExerciseSelectionModal(
                            exercises = availableExercises,
                            onDismiss = { showExerciseModal = false },
                            onExerciseSelected = { exerciseId ->
                                viewModel.addExerciseToWorkout(state.workout.id!!, exerciseId, currentDayIndex)
                                showExerciseModal = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutDetailContent(
    workout: Workout,
    currentDayIndex: Int,
    isEditMode: Boolean,
    onDayChanged: (Int) -> Unit,
    onLogPerformance: (String, String) -> Unit,
    onRemoveExercise: (String) -> Unit,
    onAddExerciseClick: () -> Unit
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
            modifier = Modifier.weight(1f)
        ) { page ->
            DayContent(
                workout = workout,
                dayIndex = page,
                isEditMode = isEditMode,
                onLogPerformance = onLogPerformance,
                onRemoveExercise = onRemoveExercise,
                onAddExerciseClick = onAddExerciseClick
            )
        }
    }
}

@Composable
fun DayContent(
    workout: Workout,
    dayIndex: Int,
    isEditMode: Boolean,
    onLogPerformance: (String, String) -> Unit,
    onRemoveExercise: (String) -> Unit,
    onAddExerciseClick: () -> Unit
) {
    // 0=Lun, 6=Dom en UI -> El backend usa 1..7 (enviado como dayIndex + 1)
    val beDayOfWeek = (dayIndex + 1) % 7
    val exercisesForDay = (workout.exercises ?: emptyList()).filter { it.dayOfWeek == beDayOfWeek }
        .sortedBy { it.order }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (exercisesForDay.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.workout_no_exercises), color = Color.Gray)
                }
            }
        } else {
            items(exercisesForDay) { item ->
                ExerciseCard(
                    item = item,
                    isEditMode = isEditMode,
                    onLogPerformance = { onLogPerformance(item.id!!, item.exercise?.name ?: "Ejercicio") },
                    onRemove = { onRemoveExercise(item.id!!) }
                )
            }
        }

        if (isEditMode) {
            item {
                Button(
                    onClick = onAddExerciseClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = DarkBg)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.workout_add_exercise), color = DarkBg)
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    item: com.meta_force.meta_force.data.model.WorkoutExercise,
    isEditMode: Boolean,
    onLogPerformance: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.exercise?.name ?: "Ejercicio desconocido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                if (isEditMode) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(label = stringResource(R.string.workout_sets), value = item.sets?.toString() ?: "-")
                InfoChip(label = stringResource(R.string.workout_reps), value = item.reps?.toString() ?: "-")
                if (item.weight != null) {
                    InfoChip(label = stringResource(R.string.workout_weight), value = "${item.weight}kg")
                }
            }

            if (!item.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
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

            if (!isEditMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogPerformance,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text(stringResource(R.string.workout_log_performance), color = DarkBg)
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

@Composable
fun ExerciseSelectionModal(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onExerciseSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Seleccionar Ejercicio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(exercises) { exercise ->
                        TextButton(
                            onClick = { onExerciseSelected(exercise.id!!) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(exercise.name ?: "Sin nombre", color = PrimaryCyan, style = MaterialTheme.typography.bodyLarge)
                                Text("${exercise.muscleGroup ?: ""}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        HorizontalDivider(color = DarkBg)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}

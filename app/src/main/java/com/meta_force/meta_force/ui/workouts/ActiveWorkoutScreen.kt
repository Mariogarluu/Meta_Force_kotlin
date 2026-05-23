package com.meta_force.meta_force.ui.workouts

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.ui.theme.MF_BlueDeep
import com.meta_force.meta_force.ui.theme.MF_BlueLight
import com.meta_force.meta_force.ui.theme.MF_Teal

// Brand Colors
private val PrimaryCyan = MF_Teal
private val DarkBg = MF_BlueDeep
private val DarkSurface = MF_BlueLight
private val DarkSurfaceVariant = Color(0xFF1D2D44)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    workoutId: String,
    dayOfWeek: Int,
    onNavigateBack: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uiState by viewModel.uiState.collectAsState()
    val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsState()
    val timerMaxSeconds by viewModel.timerMaxSeconds.collectAsState()
    val timerActive by viewModel.timerActive.collectAsState()
    val elapsedTimeSeconds by viewModel.elapsedTimeSeconds.collectAsState()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsState()

    LaunchedEffect(workoutId, dayOfWeek) {
        viewModel.loadActiveWorkout(workoutId, dayOfWeek)
    }

    // Sound and vibration feedback when rest timer completes
    LaunchedEffect(key1 = true) {
        viewModel.timerFinishedEvent.collect {
            // Play Beep Sound
            try {
                val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Vibrate
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(400)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Haptic too
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "⚡ Entrenamiento Activo",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    val durationText = formatElapsedTime(elapsedTimeSeconds)
                    Text(
                        text = durationText,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 16.dp),
                        fontSize = 16.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is ActiveWorkoutUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                }
                is ActiveWorkoutUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                        ) {
                            Text("Volver", color = DarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is ActiveWorkoutUiState.Success -> {
                    if (state.isFinished) {
                        WorkoutFinishedSummary(
                            workoutName = state.workout.name ?: "Rutina",
                            exercises = state.exercises,
                            elapsedTime = elapsedTimeSeconds,
                            onFinish = onNavigateBack
                        )
                    } else {
                        ActiveWorkoutContent(
                            exercises = state.exercises,
                            currentExerciseIndex = currentExerciseIndex,
                            onExerciseIndexChanged = { viewModel.setCurrentExercise(it) },
                            onSetToggle = { exerciseIdx, setIdx ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleSetCompletion(exerciseIdx, setIdx)
                            },
                            onFinishWorkout = { viewModel.finishWorkout() }
                        )
                    }
                }
            }

            // Rest Timer Overlay overlaying everything
            AnimatedVisibility(
                visible = timerActive && uiState is ActiveWorkoutUiState.Success && !(uiState as ActiveWorkoutUiState.Success).isFinished,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                RestTimerOverlay(
                    secondsLeft = timerSecondsLeft,
                    maxSeconds = timerMaxSeconds,
                    onSkip = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.skipRestTimer()
                    },
                    onAdd10 = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.addRestTime(10)
                    },
                    onSub10 = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.subtractRestTime(10)
                    }
                )
            }
        }
    }
}

@Composable
fun ActiveWorkoutContent(
    exercises: List<ActiveExerciseState>,
    currentExerciseIndex: Int,
    onExerciseIndexChanged: (Int) -> Unit,
    onSetToggle: (Int, Int) -> Unit,
    onFinishWorkout: () -> Unit
) {
    val currentExercise = exercises[currentExerciseIndex]

    // Calculate total progress
    val totalSets = exercises.sumOf { it.totalSets }
    val completedSets = exercises.sumOf { it.sets.count { set -> set.isCompleted } }
    val progress = if (totalSets > 0) completedSets.toFloat() / totalSets else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Linear Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progreso total:",
                color = Color.LightGray,
                fontSize = 13.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                color = PrimaryCyan,
                trackColor = DarkSurface,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = PrimaryCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exercise Navigator Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onExerciseIndexChanged(currentExerciseIndex - 1) },
                        enabled = currentExerciseIndex > 0
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Anterior",
                            tint = if (currentExerciseIndex > 0) PrimaryCyan else Color.Gray
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "EJERCICIO ${currentExerciseIndex + 1} DE ${exercises.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentExercise.exerciseName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!currentExercise.muscleGroup.isNullOrEmpty()) {
                            Text(
                                text = currentExercise.muscleGroup.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = { onExerciseIndexChanged(currentExerciseIndex + 1) },
                        enabled = currentExerciseIndex < exercises.size - 1
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Siguiente",
                            tint = if (currentExerciseIndex < exercises.size - 1) PrimaryCyan else Color.Gray
                        )
                    }
                }

                if (!currentExercise.notes.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "💡 Nota técnica: ${currentExercise.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Set List for current exercise
        Text(
            text = "Series",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(currentExercise.sets) { setIdx, setItem ->
                SetCompletionItem(
                    setItem = setItem,
                    repsText = currentExercise.reps,
                    weight = currentExercise.weight,
                    onToggle = { onSetToggle(currentExerciseIndex, setIdx) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Complete Workout Button
        Button(
            onClick = onFinishWorkout,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "🏆 Terminar Entrenamiento",
                color = DarkBg,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SetCompletionItem(
    setItem: ActiveSetState,
    repsText: String,
    weight: Double?,
    onToggle: () -> Unit
) {
    val isCompleted = setItem.isCompleted
    val containerBg = if (isCompleted) Color(0xFF0F2C2C) else DarkSurface
    val borderCyan = if (isCompleted) PrimaryCyan else Color.Transparent

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circle with Set number
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) PrimaryCyan else DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${setItem.index}",
                        color = if (isCompleted) DarkBg else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Serie ${setItem.index}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = buildString {
                            append(repsText)
                            append(" repeticiones")
                            if (weight != null && weight > 0) {
                                append(" • ")
                                append(weight)
                                append(" kg")
                            }
                        },
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Custom dynamic checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCompleted) PrimaryCyan else DarkSurfaceVariant)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Hecho",
                        tint = DarkBg,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RestTimerOverlay(
    secondsLeft: Int,
    maxSeconds: Int,
    onSkip: () -> Unit,
    onAdd10: () -> Unit,
    onSub10: () -> Unit
) {
    val progress = if (maxSeconds > 0) secondsLeft.toFloat() / maxSeconds else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "¡TIEMPO DE DESCANSO!",
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Circular progress indicator containing remaining seconds
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    color = PrimaryCyan,
                    strokeWidth = 12.dp,
                    trackColor = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxSize()
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$secondsLeft",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "segundos",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Control row (+10s, Skip, -10s)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSub10,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("-10s")
                }

                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Omitir",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Omitir", color = Color.White)
                }

                OutlinedButton(
                    onClick = onAdd10,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryCyan),
                    border = ButtonDefaults.outlinedButtonBorder(true),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+10s")
                }
            }
        }
    }
}

@Composable
fun WorkoutFinishedSummary(
    workoutName: String,
    exercises: List<ActiveExerciseState>,
    elapsedTime: Int,
    onFinish: () -> Unit
) {
    val totalSetsCount = exercises.sumOf { it.totalSets }
    val completedSetsCount = exercises.sumOf { it.sets.count { s -> s.isCompleted } }
    val completedExercisesCount = exercises.count { ex -> ex.sets.all { s -> s.isCompleted } }

    // Estimate training volume: sum(weight * completed sets)
    val estimatedVolume = exercises.sumOf { ex ->
        val weight = ex.weight ?: 0.0
        val compSets = ex.sets.count { s -> s.isCompleted }
        weight * compSets
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Triumphant Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎉",
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡BUEN TRABAJO!",
                color = PrimaryCyan,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineMedium,
                letterSpacing = 1.sp
            )

            Text(
                text = "Has completado tu rutina con éxito",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stat card grid
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = workoutName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    HorizontalDivider(color = DarkBg, modifier = Modifier.padding(bottom = 16.dp))

                    SummaryRow(label = "⏱️ Tiempo total", value = formatElapsedTime(elapsedTime))
                    SummaryRow(label = "💪 Ejercicios listos", value = "$completedExercisesCount de ${exercises.size}")
                    SummaryRow(label = "🏋️ Series marcadas", value = "$completedSetsCount de $totalSetsCount")
                    if (estimatedVolume > 0) {
                        SummaryRow(label = "📈 Volumen total", value = "${estimatedVolume.toInt()} kg")
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Volver a Rutinas",
                    color = DarkBg,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

private fun formatElapsedTime(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}

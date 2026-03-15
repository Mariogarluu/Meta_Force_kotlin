package com.meta_force.meta_force.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Workout

/**
 * Pantalla para crear un nuevo entrenamiento.
 * Permite al usuario ingresar el nombre, descripción, objetivo, nivel y días por semana.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCreationScreen(
    onNavigateBack: () -> Unit,
    onWorkoutCreated: (Workout) -> Unit,
    viewModel: WorkoutCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado del formulario usando rememberSaveable para sobrevivir a configuraciones
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var goal by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable { mutableStateOf("") }
    var daysPerWeek by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Crear Entrenamiento",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
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
        if (uiState is WorkoutCreationViewModel.CreationUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Nombre del entrenamiento",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Ejemplo: Rutina de fuerza superior") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = "Descripción (opcional)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Detalla el enfoque de este entrenamiento") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                    )
                }

                Column {
                    Text(
                        text = "Objetivo (opcional)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        placeholder = { Text("Ejemplo: Ganancia muscular, Pérdida de grasa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = "Nivel de dificultad (opcional)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = level,
                        onValueChange = { level = it },
                        placeholder = { Text("Ejemplo: Principiante, Intermedio, Avanzado") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = "Días por semana (opcional)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        placeholder = { Text("Ejemplo: 3, 4, 5") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    is WorkoutCreationViewModel.CreationUiState.Error -> {
                        Text(
                            text = state.message ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    is WorkoutCreationViewModel.CreationUiState.Success -> {
                        LaunchedEffect(state) {
                            onWorkoutCreated(state.createdWorkout)
                        }
                    }
                    else -> Unit
                }

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        viewModel.createWorkout(
                            name = name,
                            description = if (description.isBlank()) null else description,
                            goal = if (goal.isBlank()) null else goal,
                            level = if (level.isBlank()) null else level,
                            daysPerWeek = daysPerWeek.toIntOrNull()
                        )
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Crear Entrenamiento",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
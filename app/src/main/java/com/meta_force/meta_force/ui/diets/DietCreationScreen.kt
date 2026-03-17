package com.meta_force.meta_force.ui.diets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.R
import com.meta_force.meta_force.data.model.Diet

/**
 * Pantalla para crear una nueva dieta.
 * Permite al usuario ingresar el nombre, descripción y objetivo de calorías.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietCreationScreen(
    onNavigateBack: () -> Unit,
    onDietCreated: (Diet) -> Unit,
    viewModel: DietCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState(initial = DietCreationViewModel.CreationUiState.Idle)

    // Estado del formulario usando rememberSaveable para sobrevivir a configuraciones
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var caloriesTarget by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.diet_create_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back),
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
        if (uiState is DietCreationViewModel.CreationUiState.Loading) {
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
                        text = stringResource(R.string.diet_name_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text(stringResource(R.string.placeholder_diet_name)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.description_label_optional),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text(stringResource(R.string.placeholder_diet_desc)) },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.diet_calories_label_optional),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = caloriesTarget,
                        onValueChange = { caloriesTarget = it },
                        placeholder = { Text(stringResource(R.string.placeholder_calories)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    is DietCreationViewModel.CreationUiState.Error -> {
                        Text(
                            text = state.message ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    is DietCreationViewModel.CreationUiState.Success -> {
                        LaunchedEffect(state) {
                            onDietCreated(state.createdDiet)
                        }
                    }
                    else -> Unit
                }

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        viewModel.createDiet(
                            name = name,
                            description = if (description.isBlank()) null else description,
                            caloriesTarget = caloriesTarget.toIntOrNull()
                        )
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = stringResource(R.string.diet_create_title),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
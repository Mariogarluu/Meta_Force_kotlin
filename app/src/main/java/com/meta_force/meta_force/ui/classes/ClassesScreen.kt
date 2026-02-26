package com.meta_force.meta_force.ui.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ClassesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clases Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ClassesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ClassesUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ClassesUiState.Success -> {
                    if (state.classes.isEmpty()) {
                        Text(
                            text = "No hay clases disponibles en este momento.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.classes ?: emptyList()) { gymClass ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = gymClass.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Inicio: ${formatDate(gymClass.startTime)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Duración: ${gymClass.durationMinutes} min",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // Simple logic to check if joined (needs user ID context, skipping for now or assuming false)
                                        // In real app, we check if current userId is in gymClass.participants
                                        Button(
                                            onClick = { viewModel.joinClass(gymClass.id) },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Unirse")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(isoString: String): String {
    return try {
        // Simple substring for brevity, ideally use DateTimeFormatter
        isoString.replace("T", " ").substring(0, 16)
    } catch (e: Exception) {
        isoString
    }
}

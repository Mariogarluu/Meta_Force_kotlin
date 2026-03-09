package com.meta_force.meta_force.ui.diets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee) // cyan-400
private val DarkBg = Color(0xFF0f172a) // slate-900
private val DarkSurface = Color(0xFF1e293b) // slate-800
private val DarkSurfaceVariant = Color(0xFF334155) // slate-700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: DietsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Mis Dietas", 
                        color = PrimaryCyan,
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Create Diet */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Diet", tint = PrimaryCyan)
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
                is DietsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryCyan
                    )
                }
                is DietsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DietsUiState.Success -> {
                    if (state.diets.isEmpty()) {
                        Text(
                            text = "No tienes dietas asignadas.",
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.diets) { diet ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToDetail(diet.id) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = DarkSurface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = diet.name,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (diet.caloriesTarget != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${diet.caloriesTarget} kcal",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = PrimaryCyan,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                        IconButton(onClick = { viewModel.deleteDiet(diet.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFef4444))
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

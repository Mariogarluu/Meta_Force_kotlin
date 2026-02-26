package com.meta_force.meta_force.ui.diets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Diet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietDetailScreen(
    dietId: String,
    onNavigateBack: () -> Unit,
    viewModel: DietDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dietId) {
        viewModel.loadDiet(dietId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Dieta") },
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
                is DietDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DietDetailUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DietDetailUiState.Success -> {
                    DietDetailContent(diet = state.diet)
                }
            }
        }
    }
}

@Composable
fun DietDetailContent(diet: Diet) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = diet.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        if (diet.caloriesTarget != null) {
            Text(
                text = "Objetivo: ${diet.caloriesTarget} kcal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Comidas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (diet.meals.isNullOrEmpty()) {
            Text("No hay comidas en esta dieta.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(diet.meals ?: emptyList()) { meal ->
                    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = meal.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (meal.time != null) {
                                    Text(
                                        text = "(${meal.time})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            if (meal.foods.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                meal.foods.forEach { food ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("- ${food.name}")
                                        Text("${food.quantity} ${food.unit ?: "g"}")
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Total: ${meal.calories ?: 0} kcal",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.meta_force.meta_force.ui.diets

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
import com.meta_force.meta_force.data.model.Diet

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee)
private val DarkBg = Color(0xFF0f172a)
private val DarkSurface = Color(0xFF1e293b)
private val DarkSurfaceVariant = Color(0xFF334155)

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
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Detalle de Dieta",
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
                is DietDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryCyan
                    )
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
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        if (diet.caloriesTarget != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryCyan.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🎯 Objetivo: ${diet.caloriesTarget} kcal",
                    style = MaterialTheme.typography.titleSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Comidas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryCyan
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (diet.meals.isNullOrEmpty()) {
            Text("No hay comidas en esta dieta.", color = Color.Gray)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(diet.meals ?: emptyList()) { meal ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = meal.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (meal.time != null) {
                                        Text(
                                            text = "(${meal.time})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                            
                            if (meal.foods.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkBg)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    meal.foods.forEach { food ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(PrimaryCyan)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = food.name, color = Color.White)
                                            }
                                            Text(
                                                text = "${food.quantity} ${food.unit ?: "g"}",
                                                color = Color.LightGray,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        HorizontalDivider(color = DarkSurface, thickness = 1.dp)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Total: ${meal.calories ?: 0} kcal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

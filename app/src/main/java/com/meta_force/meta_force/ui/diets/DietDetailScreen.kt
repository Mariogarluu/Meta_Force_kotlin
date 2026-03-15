package com.meta_force.meta_force.ui.diets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.ui.diets.DayUtils
import kotlinx.coroutines.launch

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
    val currentDayIndex by viewModel.currentDayIndex.collectAsState()

    LaunchedEffect(dietId) {
        viewModel.loadDiet(dietId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalle de Dieta",
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
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DietDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryCyan
                        )
                    }
                }
                is DietDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is DietDetailUiState.Success -> {
                    DietDetailContent(
                        diet = state.diet,
                        currentDayIndex = currentDayIndex,
                        onDayChanged = { viewModel.setDay(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun DietDetailContent(
    diet: Diet,
    currentDayIndex: Int,
    onDayChanged: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = currentDayIndex, pageCount = { 7 })
    val scope = rememberCoroutineScope()

    // Sincronizar el estado del pager con el ViewModel
    LaunchedEffect(pagerState.currentPage) {
        onDayChanged(pagerState.currentPage)
    }
    
    // Sincronizar el ViewModel con el pager (cuando se usan los botones de flecha)
    LaunchedEffect(currentDayIndex) {
        if (pagerState.currentPage != currentDayIndex) {
            pagerState.animateScrollToPage(currentDayIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera de la dieta
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = diet.name ?: "Sin nombre",
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
        }

        // Indicadores de día con navegación
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
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
            DayContent(diet = diet, dayIndex = page)
        }
    }
}

@Composable
fun DayContent(diet: Diet, dayIndex: Int) {
    // Mapeo: 0=Lun, 1=Mar, 2=Mié, 3=Jue, 4=Vie, 5=Sáb, 6=Dom
    // Si el backend usa otra convención, habría que ajustar aquí.
    val mealsByDay = (diet.meals ?: emptyList()).groupBy { it.dayOfWeek ?: 0 }
    val mealsForCurrentDay = mealsByDay[dayIndex] ?: emptyList()

    if (mealsForCurrentDay.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay comidas programadas para este día.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mealsForCurrentDay) { meal ->
                MealCard(meal = meal)
            }
        }
    }
}

@Composable
fun MealCard(meal: com.meta_force.meta_force.data.model.DietMeal) {
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
                        text = meal.meal?.name ?: meal.mealType?.replaceFirstChar { it.uppercase() } ?: "Comida",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (meal.time != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${meal.time})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }
            
            if (!meal.notes.isNullOrEmpty()) {
                Text(
                    text = meal.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            val foods = meal.foods ?: emptyList()
            if (foods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    foods.forEach { food ->
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
                                Text(text = food.name ?: "Ingrediente", color = Color.White)
                            }
                            Text(
                                text = "${food.quantity ?: 0.0} ${food.unit ?: "g"}",
                                color = Color.LightGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (foods.last() != food) {
                            HorizontalDivider(color = DarkSurface, thickness = 1.dp)
                        }
                    }
                }
            }
            
            val calculatedCalories = if (foods.isNotEmpty()) {
                foods.sumBy { it.calories ?: 0 }
            } else {
                ((meal.meal?.calories ?: 0.0) * (meal.quantity ?: 1.0)).toInt()
            }

            val calculatedProtein = if (foods.isNotEmpty()) foods.sumOf { it.protein ?: 0.0 } else (meal.meal?.protein ?: 0.0)
            val calculatedCarbs = if (foods.isNotEmpty()) foods.sumOf { it.carbs ?: 0.0 } else (meal.meal?.carbs ?: 0.0)
            val calculatedFats = if (foods.isNotEmpty()) foods.sumOf { it.fats ?: 0.0 } else (meal.meal?.fats ?: 0.0)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MacroInfo("P", "${calculatedProtein.toInt()}g", Color(0xFFE57373))
                    MacroInfo("C", "${calculatedCarbs.toInt()}g", Color(0xFF81C784))
                    MacroInfo("G", "${calculatedFats.toInt()}g", Color(0xFFFFB74D))
                }

                Text(
                    text = "Total: $calculatedCalories kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
            }
        }
    }
}

@Composable
fun MacroInfo(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )
    }
}

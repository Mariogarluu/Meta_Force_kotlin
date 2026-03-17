package com.meta_force.meta_force.ui.diets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.R
import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.ui.diets.DayUtils
import com.meta_force.meta_force.ui.diets.DietDetailViewModel
import com.meta_force.meta_force.ui.diets.DietDetailUiState
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
    val isEditMode by viewModel.isEditMode.collectAsState()
    val availableMeals by viewModel.availableMeals.collectAsState()
    var showMealModal by remember { mutableStateOf(false) }

    LaunchedEffect(dietId) {
        viewModel.loadDiet(dietId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (val state = uiState) {
                        is DietDetailUiState.Success -> state.diet.name ?: stringResource(R.string.diet_detail_title)
                        else -> stringResource(R.string.diet_detail_title)
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DietDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryCyan
                    )
                }
                is DietDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DietDetailUiState.Success -> {
                    DietContent(
                        diet = state.diet,
                        currentDayIndex = currentDayIndex,
                        isEditMode = isEditMode,
                        onDaySelected = { viewModel.setDay(it) },
                        onRemoveMeal = { viewModel.removeMealFromDiet(state.diet.id ?: "", it) },
                        onAddMealClick = { showMealModal = true }
                    )
                }
            }
        }
    }

    if (showMealModal) {
        MealSelectionModal(
            meals = availableMeals,
            onDismiss = { showMealModal = false },
            onMealSelected = { mealId ->
                                viewModel.addMealToDiet(dietId, mealId ?: "", currentDayIndex)
                showMealModal = false
            }
        )
    }
}

@Composable
fun DietContent(
    diet: Diet,
    currentDayIndex: Int,
    isEditMode: Boolean,
    onDaySelected: (Int) -> Unit,
    onRemoveMeal: (String) -> Unit,
    onAddMealClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 7 }, initialPage = currentDayIndex)
    val coroutineScope = rememberCoroutineScope()

    // Sync pager with ViewModel
    LaunchedEffect(currentDayIndex) {
        if (pagerState.currentPage != currentDayIndex) {
            pagerState.animateScrollToPage(currentDayIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onDaySelected(pagerState.currentPage)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Day Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { index ->
                val isSelected = index == currentDayIndex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryCyan else DarkSurface)
                        .clickable { onDaySelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = DayUtils.getDayNameShort(index),
                        color = if (isSelected) DarkBg else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { dayIndex ->
            // Use (dayIndex + 1) % 7 to match backend (0=Sun, 1=Mon, ..., 6=Sat)
            val beDayOfWeek = (dayIndex + 1) % 7
            val mealsForDay = (diet.meals ?: emptyList()).filter { it.dayOfWeek == beDayOfWeek }
                .sortedBy { it.order ?: 0 }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (mealsForDay.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.diet_no_meals), color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(mealsForDay) { meal ->
                        MealCard(
                            meal = meal,
                            isEditMode = isEditMode,
                            onRemove = { onRemoveMeal(meal.id ?: "") }
                        )
                    }
                }

                if (isEditMode) {
                    item {
                        Button(
                            onClick = onAddMealClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DarkBg)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.diet_add_meal), color = DarkBg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(
    meal: DietMeal,
    isEditMode: Boolean,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PrimaryCyan, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = meal.meal?.name ?: meal.name ?: "Comida",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                if (isEditMode) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                } else if (meal.time != null) {
                    Text(
                        text = meal.time,
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ingredients / Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val foods = meal.foods ?: emptyList()
                if (foods.isNotEmpty()) {
                    foods.forEach { food ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(food.name ?: stringResource(R.string.diet_ingredient), color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            Text("${food.quantity ?: 0.0} ${food.unit ?: ""}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else if (!meal.notes.isNullOrEmpty()) {
                    Text(meal.notes, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(stringResource(R.string.diet_no_details), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun MealSelectionModal(
    meals: List<MealInfo>,
    onDismiss: () -> Unit,
    onMealSelected: (String) -> Unit
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
                    "Seleccionar Alimento",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(meals) { meal ->
                        TextButton(
                            onClick = { onMealSelected(meal.id!!) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(meal.name ?: "Sin nombre", color = PrimaryCyan, style = MaterialTheme.typography.bodyLarge)
                                if (!meal.description.isNullOrEmpty()) {
                                    Text(meal.description, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
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

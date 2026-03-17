package com.meta_force.meta_force.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToDiets: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToAiChat: () -> Unit, // Added
    onNavigateToQr: () -> Unit,
    onNavigateToCenters: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("METAFORCE", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Favorites */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Heart", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        DashboardGrid(
            modifier = Modifier.padding(innerPadding),
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToWorkouts = onNavigateToWorkouts,
            onNavigateToDiets = onNavigateToDiets,
            onNavigateToClasses = onNavigateToClasses,
            onNavigateToAiChat = onNavigateToAiChat,
            onNavigateToQr = onNavigateToQr,
            onNavigateToCenters = onNavigateToCenters
        )
    }
}

@Composable
fun DashboardGrid(
    modifier: Modifier = Modifier,
    onNavigateToProfile: () -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToDiets: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToQr: () -> Unit,
    onNavigateToCenters: () -> Unit
) {
    val dashboardItems = listOf(
        DashboardItem("Centro", Icons.Default.LocationOn),
        DashboardItem("Mi QR", Icons.Default.QrCode),
        DashboardItem("Mi Rutina", Icons.Default.FitnessCenter),
        DashboardItem("Dietas", Icons.Default.Restaurant),
        DashboardItem("Horarios", Icons.Default.Schedule),
        DashboardItem("MetaCoach AI", Icons.Default.SmartToy),
        DashboardItem("Perfil", Icons.Default.Person)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Aquí pasamos la lista renombrada
        items(dashboardItems) { item ->
            DashboardCard(
                item = item,
                onClick = {
                    if (item.title == "Perfil") {
                        onNavigateToProfile()
                    } else if (item.title == "Mi Rutina") {
                        onNavigateToWorkouts()
                    } else if (item.title == "Dietas") {
                        onNavigateToDiets()
                    } else if (item.title == "Horarios") {
                        onNavigateToClasses()
                    } else if (item.title == "MetaCoach AI") {
                        onNavigateToAiChat()
                    } else if (item.title == "Mi QR") {
                        onNavigateToQr()
                    } else if (item.title == "Centro") {
                        onNavigateToCenters()
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class DashboardItem(val title: String, val icon: ImageVector)
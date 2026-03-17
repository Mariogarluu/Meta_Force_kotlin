package com.meta_force.meta_force.ui.centers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.MachineTypeModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.meta_force.meta_force.R

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee) // cyan-400
private val DarkBg = Color(0xFF0f172a) // slate-900
private val DarkSurface = Color(0xFF1e293b) // slate-800
private val DarkSurfaceVariant = Color(0xFF334155) // slate-700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentersScreen(
    onNavigateBack: () -> Unit,
    viewModel: CentersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val machinesState by viewModel.machinesState.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Centros", 
                        color = PrimaryCyan,
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
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
                is CentersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryCyan
                    )
                }
                is CentersUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CentersUiState.Success -> {
                    if (state.centers.isEmpty()) {
                        Text(
                            text = "No hay centros disponibles.",
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.centers) { center ->
                                CenterCard(
                                    center = center,
                                    isAdmin = isAdmin,
                                    machines = center.id?.let { machinesState[it] },
                                    onLoadMachines = { center.id?.let { viewModel.loadMachinesForCenter(it) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenterCard(
    center: Center,
    isAdmin: Boolean,
    machines: List<MachineTypeModel>?,
    onLoadMachines: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded && machines == null) {
            onLoadMachines()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = center.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (!center.description.isNullOrEmpty()) {
                Text(
                    text = center.description, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (!center.city.isNullOrEmpty() || !center.country.isNullOrEmpty()) {
                Text(
                    text = "${center.city ?: ""} ${center.country ?: ""}".trim(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp), color = DarkSurfaceVariant)
                    Text(
                        text = "Máquinas Activas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (machines == null) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(24.dp),
                            color = PrimaryCyan,
                            strokeWidth = 2.dp
                        )
                    } else if (machines.isEmpty()) {
                        Text(
                            text = "No hay máquinas registradas en este centro.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    } else {
                        val activeMachines = machines.filter { type -> 
                            type.instances?.any { it.centerId == center.id } == true
                        }
                        
                        if (activeMachines.isEmpty()) {
                            Text(
                                text = "Este centro no tiene instancias de máquinas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        } else {
                            activeMachines.forEach { machineType ->
                                val instances = machineType.instances?.filter { it.centerId == center.id } ?: emptyList()
                                Text(
                                    text = "${machineType.name} (${machineType.type.name})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                instances.forEach { instance ->
                                    val statusColor = when (instance.status.name) {
                                        "OPERATIVA" -> Color(0xFF4CAF50)
                                        "MANTENIMIENTO" -> Color(0xFFFF9800)
                                        else -> Color(0xFFF44336)
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(statusColor, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Instancia ${instance.instanceNumber} - ${instance.status.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )
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


package com.meta_force.meta_force.ui.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.GymClass

// Theme Colors
private val PrimaryCyan = Color(0xFF22d3ee) // cyan-400
private val DarkBg = Color(0xFF0f172a) // slate-900
private val DarkSurface = Color(0xFF1e293b) // slate-800
private val DarkSurfaceVariant = Color(0xFF334155) // slate-700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ClassesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val centers by viewModel.centers.collectAsState()
    var selectedCenterId by remember { mutableStateOf<String?>(null) }
    
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Clases Disponibles",
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
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = PrimaryCyan,
                    contentColor = DarkBg
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Clase")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // Selector de centro
            if (centers.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = if (selectedCenterId == null) 0 else (centers.indexOfFirst { it.id == selectedCenterId } + 1).coerceAtLeast(0),
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = DarkBg,
                    contentColor = PrimaryCyan
                ) {
                    Tab(
                        selected = selectedCenterId == null,
                        onClick = { 
                            selectedCenterId = null
                            viewModel.loadClasses(null)
                        },
                        text = { Text("Todos", color = if (selectedCenterId == null) PrimaryCyan else Color.White) }
                    )
                    centers.forEachIndexed { index, center ->
                        Tab(
                            selected = selectedCenterId == center.id,
                            onClick = { 
                                selectedCenterId = center.id
                                viewModel.loadClasses(center.id)
                            },
                            text = { Text(center.name, color = if (selectedCenterId == center.id) PrimaryCyan else Color.White) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                when (val state = uiState) {
                    is ClassesUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryCyan)
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
                                text = "No hay clases en este centro.",
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.classes) { gymClass ->
                                    ClassCard(
                                        gymClass = gymClass,
                                        isAdmin = isAdmin,
                                        onJoin = { viewModel.joinClass(gymClass.id) },
                                        onDelete = { viewModel.deleteClass(gymClass.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            ClassFormDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, desc ->
                    viewModel.createClass(name, desc)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun ClassCard(
    gymClass: GymClass,
    isAdmin: Boolean,
    onJoin: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = gymClass.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isAdmin) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color(0xFFef4444))
                    }
                }
            }
            if (!gymClass.description.isNullOrEmpty()) {
                Text(text = gymClass.description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Render schedules simplified
            if (!gymClass.schedules.isNullOrEmpty()) {
                gymClass.schedules.forEach { sc ->
                    Text(
                        text = "${getDayName(sc.dayOfWeek)}: ${sc.startTime} - ${sc.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onJoin,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = DarkBg)
            ) {
                Text("Unirse", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassFormDialog(
    initialClass: GymClass? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialClass?.name ?: "") }
    var description by remember { mutableStateOf(initialClass?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = PrimaryCyan,
        textContentColor = Color.White,
        title = { Text(if (initialClass == null) "Nueva Clase" else "Editar Clase", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nombre", color = Color.LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryCyan,
                        cursorColor = PrimaryCyan
                    )
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = { Text("Descripción", color = Color.LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryCyan,
                        cursorColor = PrimaryCyan
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = DarkBg)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = PrimaryCyan)
            }
        }
    )
}

fun getDayName(day: Int): String {
    return when(day) {
        0 -> "Domingo"
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        else -> "Día"
    }
}

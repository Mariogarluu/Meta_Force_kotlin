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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.data.model.GymClass

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
        topBar = {
            TopAppBar(
                title = { Text("Clases Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Clase")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // Selector de centro
            if (centers.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = centers.indexOfFirst { it.id == selectedCenterId }.coerceAtLeast(0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedCenterId == null,
                        onClick = { 
                            selectedCenterId = null
                            viewModel.loadClasses(null)
                        },
                        text = { Text("Todos") }
                    )
                    centers.forEachIndexed { index, center ->
                        Tab(
                            selected = selectedCenterId == center.id,
                            onClick = { 
                                selectedCenterId = center.id
                                viewModel.loadClasses(center.id)
                            },
                            text = { Text(center.name) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
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
                                text = "No hay clases en este centro.",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.classes) { gymClass ->
                                    ClassCard(
                                        gymClass = gymClass,
                                        isAdmin = isAdmin,
                                        onJoin = { viewModel.joinClass(gymClass.id) },
                                        onEdit = { /* Open edit dialog */ },
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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gymClass.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isAdmin) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            if (!gymClass.description.isNullOrEmpty()) {
                Text(text = gymClass.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Render schedules simplified
            if (!gymClass.schedules.isNullOrEmpty()) {
                gymClass.schedules.forEach { sc ->
                    Text(
                        text = "${getDayName(sc.dayOfWeek)}: ${sc.startTime} - ${sc.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onJoin,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Unirse")
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
        title = { Text(if (initialClass == null) "Nueva Clase" else "Editar Clase") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
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

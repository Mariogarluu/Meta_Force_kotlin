package com.meta_force.meta_force.ui.centers

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
import com.meta_force.meta_force.data.model.Center

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentersScreen(
    onNavigateBack: () -> Unit,
    viewModel: CentersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centros") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Centro")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is CentersUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                                    onEdit = { /* show edit dialog */ },
                                    onDelete = { viewModel.deleteCenter(center.id ?: "") }
                                )
                            }
                        }
                    }
                }
            }

            if (showCreateDialog) {
                CenterFormDialog(
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { name, desc, address, city, country, phone, email ->
                        viewModel.createCenter(name, desc, address, city, country, phone, email)
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CenterCard(
    center: Center,
    isAdmin: Boolean,
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
                    text = center.name,
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
            if (!center.description.isNullOrEmpty()) {
                Text(text = center.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (!center.city.isNullOrEmpty() || !center.country.isNullOrEmpty()) {
                Text(
                    text = "${center.city ?: ""} ${center.country ?: ""}".trim(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterFormDialog(
    initialCenter: Center? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialCenter?.name ?: "") }
    var description by remember { mutableStateOf(initialCenter?.description ?: "") }
    var address by remember { mutableStateOf(initialCenter?.address ?: "") }
    var city by remember { mutableStateOf(initialCenter?.city ?: "") }
    var country by remember { mutableStateOf(initialCenter?.country ?: "") }
    var phone by remember { mutableStateOf(initialCenter?.phone ?: "") }
    var email by remember { mutableStateOf(initialCenter?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialCenter == null) "Nuevo Centro" else "Editar Centro") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") })
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") })
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("País") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description, address, city, country, phone, email) },
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

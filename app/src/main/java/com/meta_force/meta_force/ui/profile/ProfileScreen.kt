package com.meta_force.meta_force.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showBigImageDialog by remember { mutableStateOf(false) }

    // Helper to get file from URI with proper extension and type
    fun uriToFile(uri: Uri): File? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = android.webkit.MimeTypeMap.getSingleton()
        val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
        
        val tempFile = File.createTempFile("avatar", ".$extension", context.cacheDir)
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(it)
            if (file != null) {
                viewModel.uploadAvatar(file)
                showBigImageDialog = false // Close dialog after picking
            } else {
                Toast.makeText(context, "Error reading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp)) // balance the icon
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileUiState.Error -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.loadProfile() }) {
                    Text("Retry")
                }
            }
            is ProfileUiState.Success -> {
                val user = state.user
                
                // State for editable fields
                var name by remember { mutableStateOf(user.name) }
                var height by remember { mutableStateOf(user.height?.toString() ?: "") }
                var weight by remember { mutableStateOf(user.currentWeight?.toString() ?: "") }
                var gender by remember { mutableStateOf(user.gender ?: "other") }
                var birthDate by remember { mutableStateOf(user.birthDate ?: "") }
                var medicalNotes by remember { mutableStateOf(user.medicalNotes ?: "") }

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { showBigImageDialog = true }
                    ) {
                        if (user.profileImageUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.profileImageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Details
                    OutlinedTextField(
                        value = name ?: "",
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = user.email ?: "",
                        onValueChange = {},
                        label = { Text("Email (No editable)") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Datos Físicos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), gap = 8.dp) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Altura (cm)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Peso (kg)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Fecha Nacimiento (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("1990-01-01") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Género", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("male", "female", "other").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g.capitalize()) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = medicalNotes,
                        onValueChange = { medicalNotes = it },
                        label = { Text("Notas Médicas / Alergias") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        multiline = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                UpdateProfileRequest(
                                    name = name,
                                    height = height.toDoubleOrNull(),
                                    currentWeight = weight.toDoubleOrNull(),
                                    birthDate = if (birthDate.isEmpty()) null else birthDate,
                                    gender = gender,
                                    medicalNotes = medicalNotes,
                                    activityLevel = user.activityLevel,
                                    goal = user.goal
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("GUARDAR PERFIL")
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }

                // Dialog for Big Image & Edit
                if (showBigImageDialog) {
                    Dialog(onDismissRequest = { showBigImageDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (user.profileImageUrl != null) {
                                    AsyncImage(
                                        model = user.profileImageUrl,
                                        contentDescription = "Full Profile",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Default Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(32.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Edit Button overlay
                                FloatingActionButton(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp),
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Photo")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.meta_force.meta_force.ui.profile

import androidx.compose.ui.graphics.Color
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import coil.request.ImageRequest

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.meta_force.meta_force.data.model.UpdateProfileRequest
import androidx.compose.ui.res.stringResource
import com.meta_force.meta_force.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.meta_force.meta_force.ui.profile.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.meta_force.meta_force.ui.profile.CameraCaptureView

// Theme Colors
private val DarkBg = Color(0xFF0f172a)
private val DarkSurface = Color(0xFF1e293b)
private val DarkSurfaceVariant = Color(0xFF334155)
private val PrimaryCyan = Color(0xFF22d3ee)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onPasswordChanged: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val changePasswordStatus by viewModel.changePasswordStatus.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(updateStatus) {
        updateStatus?.let { result ->
            when (result) {
                is com.meta_force.meta_force.data.network.NetworkResult.Success -> {
                    Toast.makeText(context, "Perfil guardado con éxito", Toast.LENGTH_SHORT).show()
                    viewModel.clearUpdateStatus()
                }
                is com.meta_force.meta_force.data.network.NetworkResult.Error -> {
                    Toast.makeText(context, "Error al guardar: ${result.message}", Toast.LENGTH_SHORT).show()
                    viewModel.clearUpdateStatus()
                }
                is com.meta_force.meta_force.data.network.NetworkResult.Exception -> {
                    Toast.makeText(context, "Error de red: ${result.e.message ?: "Conexión fallida"}", Toast.LENGTH_SHORT).show()
                    viewModel.clearUpdateStatus()
                }
            }
        }
    }

    LaunchedEffect(changePasswordStatus) {
        changePasswordStatus?.let { result ->
            when (result) {
                is com.meta_force.meta_force.data.network.NetworkResult.Success -> {
                    Toast.makeText(context, "Contraseña cambiada con éxito. Cerrando sesión...", Toast.LENGTH_LONG).show()
                    viewModel.clearChangePasswordStatus()
                    viewModel.logout()
                    onPasswordChanged()
                }
                is com.meta_force.meta_force.data.network.NetworkResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.clearChangePasswordStatus()
                }
                is com.meta_force.meta_force.data.network.NetworkResult.Exception -> {
                    Toast.makeText(context, "Error de red: ${result.e.message ?: "Conexión fallida"}", Toast.LENGTH_SHORT).show()
                    viewModel.clearChangePasswordStatus()
                }
            }
        }
    }
    var showBigImageDialog by remember { mutableStateOf(false) }
    var showCameraView by remember { mutableStateOf(false) }
    var showSelectionDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showCameraView = true
            showBigImageDialog = false
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

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
                Toast.makeText(context, context.getString(R.string.profile_error_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileUiState.Error -> {
                if (com.meta_force.meta_force.ui.theme.isNetworkError(state.message)) {
                    com.meta_force.meta_force.ui.theme.NoInternetPlaceholder(
                        onRetry = { viewModel.loadProfile() }
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.profile_retry) + ": ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text(stringResource(R.string.profile_retry))
                        }
                    }
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

                // State for Change Password
                var showChangePasswordDialog by remember { mutableStateOf(false) }
                var currentPassword by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }
                var showCurrentPass by remember { mutableStateOf(false) }
                var showNewPass by remember { mutableStateOf(false) }

                // Validation Errors
                var heightError by remember { mutableStateOf<String?>(null) }
                var weightError by remember { mutableStateOf<String?>(null) }

                // Date Picker State
                val datePickerState = rememberDatePickerState()
                var showDatePicker by remember { mutableStateOf(false) }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val calendar = Calendar.getInstance().apply {
                                        timeInMillis = millis
                                    }
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    birthDate = sdf.format(calendar.time)
                                }
                                 showDatePicker = false
                            }) {
                                Text(stringResource(R.string.profile_date_accept))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(R.string.profile_date_cancel))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

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
                                contentDescription = stringResource(R.string.profile_edit_photo),
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
                         onValueChange = { newValue: String -> name = newValue },
                         label = { Text(stringResource(R.string.profile_name)) },
                         modifier = Modifier.fillMaxWidth()
                     )

                    Spacer(modifier = Modifier.height(16.dp))

                     OutlinedTextField(
                         value = user.email ?: "",
                         onValueChange = {},
                         label = { Text(stringResource(R.string.profile_email_readonly)) },
                         readOnly = true,
                         modifier = Modifier.fillMaxWidth()
                     )

                     Spacer(modifier = Modifier.height(16.dp))

                     Card(
                         modifier = Modifier.fillMaxWidth(),
                         colors = CardDefaults.cardColors(containerColor = DarkSurface),
                         border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceVariant),
                         shape = MaterialTheme.shapes.medium
                     ) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text(
                                 text = "Seguridad",
                                 style = MaterialTheme.typography.titleMedium,
                                 color = MaterialTheme.colorScheme.secondary,
                                 fontWeight = FontWeight.Bold
                             )
                             Spacer(modifier = Modifier.height(8.dp))
                             Button(
                                 onClick = {
                                     currentPassword = ""
                                     newPassword = ""
                                     showChangePasswordDialog = true
                                 },
                                 modifier = Modifier.fillMaxWidth(),
                                 colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                             ) {
                                 Text("Cambiar contraseña", color = DarkBg, fontWeight = FontWeight.Bold)
                             }
                         }
                     }

                     Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(stringResource(R.string.profile_physical_data), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))

                     Row(
                         modifier = Modifier.fillMaxWidth(),
                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                     ) {
                      OutlinedTextField(
                          value = height,
                          onValueChange = { 
                              if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                  height = it
                                  val h = it.toDoubleOrNull()
                                  heightError = if (h != null && (h < 50 || h > 250)) "50-250 cm" else null
                              }
                          },
                          label = { Text(stringResource(R.string.profile_height)) },
                          isError = heightError != null,
                          supportingText = { if (heightError != null) Text(heightError!!) },
                          modifier = Modifier.weight(1f),
                          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                      )
                          OutlinedTextField(
                              value = weight,
                              onValueChange = { 
                                  if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                      weight = it
                                      val w = it.toDoubleOrNull()
                                      weightError = if (w != null && (w < 30 || w > 300)) "30-300 kg" else null
                                  }
                              },
                              label = { Text(stringResource(R.string.profile_weight)) },
                              isError = weightError != null,
                              supportingText = { if (weightError != null) Text(weightError!!) },
                              modifier = Modifier.weight(1f),
                              keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                          )
                     }

                    Spacer(modifier = Modifier.height(16.dp))

                    val displayDate = try {
                        if (birthDate.isNotEmpty()) {
                            val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            val date = inputSdf.parse(birthDate)
                            val outputSdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-ES"))
                            outputSdf.format(date!!)
                        } else ""
                    } catch (e: Exception) {
                        birthDate
                    }

                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.profile_birth_date)) },
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        enabled = false,
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.profile_birth_date), Modifier.clickable { showDatePicker = true })
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.profile_gender), style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        val genderOptions = listOf(
                            "male" to stringResource(R.string.gender_male),
                            "female" to stringResource(R.string.gender_female),
                            "other" to stringResource(R.string.gender_other)
                        )
                        genderOptions.forEach { (value, label) ->
                            FilterChip(
                                selected = gender == value,
                                onClick = { gender = value },
                                label = { Text(label) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                      OutlinedTextField(
                          value = medicalNotes,
                          onValueChange = { newValue: String -> medicalNotes = newValue },
                          label = { Text(stringResource(R.string.profile_medical_notes)) },
                          modifier = Modifier.fillMaxWidth().height(120.dp),
                          maxLines = 5
                      )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (heightError == null && weightError == null) {
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
                            }
                        },
                        enabled = heightError == null && weightError == null,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.profile_save))
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }

                if (showChangePasswordDialog) {
                    Dialog(onDismissRequest = { showChangePasswordDialog = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryCyan)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Cambiar contraseña",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = currentPassword,
                                    onValueChange = { currentPassword = it },
                                    label = { Text("Contraseña actual") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showCurrentPass) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    trailingIcon = {
                                        IconButton(onClick = { showCurrentPass = !showCurrentPass }) {
                                            Icon(
                                                imageVector = if (showCurrentPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    label = { Text("Nueva contraseña") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    trailingIcon = {
                                        IconButton(onClick = { showNewPass = !showNewPass }) {
                                            Icon(
                                                imageVector = if (showNewPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = { showChangePasswordDialog = false },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancelar", color = Color.White)
                                    }
                                    Button(
                                        onClick = {
                                            if (currentPassword.isNotBlank() && newPassword.length >= 6) {
                                                viewModel.changePassword(currentPassword, newPassword)
                                            } else if (newPassword.length < 6) {
                                                Toast.makeText(context, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        enabled = currentPassword.isNotBlank() && newPassword.isNotBlank(),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Guardar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
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
                                    onClick = { showSelectionDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                         .padding(16.dp),
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DarkBg)
                                }
                            }
                        }
                    }
                }

                if (showSelectionDialog) {
                    AlertDialog(
                        onDismissRequest = { showSelectionDialog = false },
                        title = { Text("Editar Foto de Perfil") },
                        text = { Text("Selecciona cómo quieres actualizar tu foto de perfil:") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showSelectionDialog = false
                                    val permissionCheck = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    )
                                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                        showCameraView = true
                                        showBigImageDialog = false
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            ) {
                                Text("Cámara")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showSelectionDialog = false
                                    photoPickerLauncher.launch("image/*")
                                }
                            ) {
                                Text("Galería")
                            }
                        }
                    )
                }

                if (showCameraView) {
                    Dialog(
                        onDismissRequest = { showCameraView = false },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = false,
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Black
                        ) {
                            CameraCaptureView(
                                onImageCaptured = { uri ->
                                    showCameraView = false
                                    val file = uriToFile(uri)
                                    if (file != null) {
                                        viewModel.uploadAvatar(file)
                                    } else {
                                        Toast.makeText(context, "Error al procesar la imagen capturada", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onDismiss = {
                                    showCameraView = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

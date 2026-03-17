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
import androidx.compose.material3.*
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
import com.meta_force.meta_force.ui.profile.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

// Theme Colors
private val DarkBg = Color(0xFF0f172a)
private val DarkSurface = Color(0xFF1e293b)
private val DarkSurfaceVariant = Color(0xFF334155)
private val PrimaryCyan = Color(0xFF22d3ee)

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
                Text(stringResource(R.string.profile_retry) + ": ${state.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.loadProfile() }) {
                    Text(stringResource(R.string.profile_retry))
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
                            val outputSdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
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
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DarkBg)
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

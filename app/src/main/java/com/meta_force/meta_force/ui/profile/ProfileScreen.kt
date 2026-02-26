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
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showBigImageDialog by remember { mutableStateOf(false) }

    // Helper to get file from URI
    fun uriToFile(uri: Uri): File? {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("avatar", ".jpg", context.cacheDir)
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
        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

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
                    value = user.name ?: "Unknown",
                    onValueChange = { /* TODO Implementation for instant edit or save button */ },
                    label = { Text("Name") },
                    readOnly = true, // Readonly for now, can implement edit mode later
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = user.email ?: "",
                    onValueChange = {},
                    label = { Text("Email") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

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

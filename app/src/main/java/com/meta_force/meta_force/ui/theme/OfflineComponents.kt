package com.meta_force.meta_force.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val PrimaryCyan = Color(0xFF22d3ee)

/**
 * A beautiful, highly-styled offline placeholder for Metaforce screens when no connection is available.
 */
@Composable
fun NoInternetPlaceholder(
    modifier: Modifier = Modifier,
    message: String = "No puedes acceder a este apartado sin conexión a Internet.",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = "Sin conexión",
            tint = PrimaryCyan,
            modifier = Modifier.size(76.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin Conexión",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan,
                    contentColor = Color(0xFF0f172a) // Dark slate background text
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Reintentar",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Detects if a PostgREST error message corresponds to a network resolution exception
 * and returns a user-friendly, descriptive Spanish error instead of a raw stack trace.
 */
fun mapNetworkErrorMessage(message: String?): String {
    if (message == null) return "Ha ocurrido un error inesperado."
    val isNetwork = message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("No address associated", ignoreCase = true) ||
            message.contains("connect", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ||
            message.contains("Failed to connect", ignoreCase = true)
            
    return if (isNetwork) {
        "No puedes acceder a este apartado sin conexión a Internet."
    } else {
        message
    }
}

/**
 * Checks if a given error message represents a network/internet issue.
 */
fun isNetworkError(message: String?): Boolean {
    if (message == null) return false
    return message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("No address associated", ignoreCase = true) ||
            message.contains("connect", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ||
            message.contains("Failed to connect", ignoreCase = true)
}

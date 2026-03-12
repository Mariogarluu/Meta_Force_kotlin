package com.meta_force.meta_force.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * UI State for the QR access screen.
 *
 * @property user The current user information.
 * @property timestamp ISO timestamp that updates periodically for security.
 */
sealed class QrUiState {
    object Loading : QrUiState()
    data class Success(val user: User, val timestamp: String) : QrUiState()
    data class Error(val message: String) : QrUiState()
}

/**
 * ViewModel for generating and updating the QR access code.
 * Fetches user profile and starts a periodic timestamp update.
 *
 * @property repository Repository for user information.
 */
@HiltViewModel
class QrViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QrUiState>(QrUiState.Loading)
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = QrUiState.Loading
            when (val userResult = repository.getProfile()) {
                is NetworkResult.Success -> {
                    startTimestampUpdates(userResult.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = QrUiState.Error(userResult.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = QrUiState.Error(userResult.e.message ?: "Error loading profile")
                }
            }
        }
    }

    private fun startTimestampUpdates(user: User) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            while (true) {
                // Format ISO timestamp matching front-end 'new Date().toISOString()'
                val currentTimestamp = sdf.format(Date())
                
                _uiState.value = QrUiState.Success(user, currentTimestamp)
                
                // Update every 20 minutes (frontend logic)
                delay(20L * 60L * 1000L)
            }
        }
    }
}

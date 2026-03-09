package com.meta_force.meta_force.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.meta_force.meta_force.data.network.NetworkResult
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            // LIMPIEZA: Quitamos espacios y saltos de línea accidentales
            // CORRECCIÓN: Usamos 'password' para que coincida con RegisterRequest
            val request = RegisterRequest(
                name = name.trim(),
                email = email.trim(),
                password = password.trim()
            )

            when (val result = authRepository.register(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = RegisterUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = RegisterUiState.Error(result.e.message ?: "Registration failed")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
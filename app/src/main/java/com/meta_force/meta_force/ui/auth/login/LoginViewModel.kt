package com.meta_force.meta_force.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.meta_force.meta_force.data.network.NetworkResult
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Cambiamos 'pass' por 'password' para ser coherentes con el modelo
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // LIMPIEZA: Aplicamos .trim() y usamos el nombre de parámetro correcto
            val request = LoginRequest(
                email = email.trim(),
                password = password.trim() // 'password' coincide con AuthModels.kt
            )

            when (val result = authRepository.login(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = LoginUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = LoginUiState.Error(result.e.message ?: "Login failed")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
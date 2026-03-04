package com.meta_force.meta_force.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QrUiState {
    object Loading : QrUiState()
    data class Success(val user: User) : QrUiState()
    data class Error(val message: String) : QrUiState()
}

@HiltViewModel
class QrViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QrUiState>(QrUiState.Loading)
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = QrUiState.Loading
            repository.getProfile()
                .onSuccess { user ->
                    _uiState.value = QrUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = QrUiState.Error(e.message ?: "Failed to load profile data")
                }
        }
    }
}

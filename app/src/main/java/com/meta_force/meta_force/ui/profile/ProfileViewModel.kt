package com.meta_force.meta_force.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.getProfile()
                .onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Error loading profile")
                }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            // Keep current state but maybe show loading indicator overlay
            // For simplicity, we stick to main state or could add a separate loading channel
            repository.updateProfile(newName)
                .onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                }
                .onFailure { e ->
                    // Show error but keep current data if possible, or transition to error
                    // Ideally we use a UI Event for one-shot errors
                }
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.uploadAvatar(file)
                .onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to upload avatar")
                }
        }
    }
}

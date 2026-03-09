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
import com.meta_force.meta_force.data.network.NetworkResult

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
            when (val userResult = repository.getProfile()) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(userResult.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProfileUiState.Error(userResult.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = ProfileUiState.Error(userResult.e.message ?: "Error loading profile")
                }
            }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            // Keep current state but maybe show loading indicator overlay
            // For simplicity, we stick to main state or could add a separate loading channel
            when (val userResult = repository.updateProfile(newName)) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(userResult.data)
                }
                is NetworkResult.Error -> {
                    // Show error but keep current data if possible, or transition to error
                    // Ideally we use a UI Event for one-shot errors
                }
                is NetworkResult.Exception -> {
                    // Same as above
                }
            }
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            when (val userResult = repository.uploadAvatar(file)) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(userResult.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProfileUiState.Error(userResult.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = ProfileUiState.Error(userResult.e.message ?: "Failed to upload avatar")
                }
            }
        }
    }
}

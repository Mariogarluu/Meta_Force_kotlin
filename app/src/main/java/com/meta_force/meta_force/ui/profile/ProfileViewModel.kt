package com.meta_force.meta_force.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.UserProfile
import com.meta_force.meta_force.data.model.UpdateProfileRequest
import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * UI State for the User Profile screen.
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

/**
 * ViewModel for the User Profile screen.
 * Handles displaying and updating user information, including profile image uploads.
 */
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
            when (val result = repository.getProfile()) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProfileUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = ProfileUiState.Error(result.e.message ?: "Error loading profile")
                }
            }
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            // Optimistic update or show loading overlay? 
            // For now, let's just perform the call and update on success
            when (val result = repository.updateProfile(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    // In a real app, we might want to pulse an error message without clearing current success data
                    _uiState.value = ProfileUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = ProfileUiState.Error(result.e.message ?: "Update failed")
                }
            }
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            when (val result = repository.uploadAvatar(file)) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProfileUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = ProfileUiState.Error(result.e.message ?: "Failed to upload avatar")
                }
            }
        }
    }
}

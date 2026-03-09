package com.meta_force.meta_force.ui.centers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.repository.CenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CentersUiState {
    object Loading : CentersUiState()
    data class Success(val centers: List<Center>) : CentersUiState()
    data class Error(val message: String) : CentersUiState()
}

@HiltViewModel
class CentersViewModel @Inject constructor(
    private val centerRepository: CenterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CentersUiState>(CentersUiState.Loading)
    val uiState: StateFlow<CentersUiState> = _uiState.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        checkUserRole()
        loadCenters()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            val userResult = authRepository.getProfile()
            if (userResult is com.meta_force.meta_force.data.network.NetworkResult.Success) {
                val user = userResult.data
                _isAdmin.value = user.role == "SUPERADMIN" || user.role == "ADMIN_CENTER"
            } else {
                _isAdmin.value = false
            }
        }
    }

    fun loadCenters() {
        _uiState.value = CentersUiState.Loading
        viewModelScope.launch {
            centerRepository.getCenters().fold(
                onSuccess = { centers ->
                    _uiState.value = CentersUiState.Success(centers)
                },
                onFailure = { error ->
                    _uiState.value = CentersUiState.Error(error.message ?: "Error al cargar los centros")
                }
            )
        }
    }

    fun createCenter(
        name: String, description: String?, address: String?,
        city: String?, country: String?, phone: String?, email: String?
    ) {
        if (!_isAdmin.value) return
        _uiState.value = CentersUiState.Loading
        viewModelScope.launch {
            val input = CreateCenterInput(name, description, address, city, country, phone, email)
            centerRepository.createCenter(input).fold(
                onSuccess = { loadCenters() },
                onFailure = { _uiState.value = CentersUiState.Error(it.message ?: "Error al crear el centro") }
            )
        }
    }

    fun updateCenter(
        id: String, name: String?, description: String?, address: String?,
        city: String?, country: String?, phone: String?, email: String?
    ) {
        if (!_isAdmin.value) return
        _uiState.value = CentersUiState.Loading
        viewModelScope.launch {
            val input = UpdateCenterInput(name, description, address, city, country, phone, email)
            centerRepository.updateCenter(id, input).fold(
                onSuccess = { loadCenters() },
                onFailure = { _uiState.value = CentersUiState.Error(it.message ?: "Error al actualizar el centro") }
            )
        }
    }

    fun deleteCenter(id: String) {
        if (!_isAdmin.value) return
        _uiState.value = CentersUiState.Loading
        viewModelScope.launch {
            centerRepository.deleteCenter(id).fold(
                onSuccess = { loadCenters() },
                onFailure = { _uiState.value = CentersUiState.Error(it.message ?: "Error al eliminar el centro") }
            )
        }
    }
}

package com.meta_force.meta_force.ui.diets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.repository.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de creación de dieta.
 * Maneja el estado de creación y la lógica de negocio.
 */
@HiltViewModel
class DietCreationViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {

    // Estados de la UI
    private val _uiState = MutableStateFlow<CreationUiState>(CreationUiState.Idle)
    val uiState: StateFlow<CreationUiState> = _uiState.asStateFlow()

    /**
     * Crea una nueva dieta.
     * @param name Nombre de la dieta
     * @param description Descripción opcional
     * @param caloriesTarget Objetivo de calorías diario opcional
     */
    fun createDiet(
        name: String,
        description: String?,
        caloriesTarget: Int?
    ) {
        viewModelScope.launch {
            _uiState.value = CreationUiState.Loading
            try {
                val diet = dietRepository.createDiet(
                    name = name,
                    description = description,
                    caloriesTarget = caloriesTarget,
                    userId = "" // TODO: Obtener del usuario actual
                ).first()
                _uiState.value = CreationUiState.Success(diet)
            } catch (e: Exception) {
                _uiState.value = CreationUiState.Error(e.localizedMessage ?: "Error al crear dieta")
            }
        }
    }

    sealed class CreationUiState {
        object Idle : CreationUiState()
        object Loading : CreationUiState()
        data class Success(val createdDiet: Diet) : CreationUiState()
        data class Error(val message: String) : CreationUiState()
    }
}

/**
 * Factory para crear el ViewModel (necesario para Hilt).
 */
class DietCreationViewModelFactory @Inject constructor(
    private val repository: DietRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DietCreationViewModel(repository) as T
    }
}
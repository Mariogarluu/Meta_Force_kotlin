package com.meta_force.meta_force.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de creación de entrenamiento.
 * Maneja el estado de creación y la lógica de negocio.
 */
@HiltViewModel
class WorkoutCreationViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // Estados de la UI
    private val _uiState = MutableStateFlow<CreationUiState>(CreationUiState.Idle)
    val uiState: StateFlow<CreationUiState> = _uiState.asStateFlow()

    /**
     * Crea un nuevo entrenamiento.
     * @param name Nombre del entrenamiento
     * @param description Descripción opcional
     * @param goal Objetivo del entrenamiento (opcional)
     * @param level Nivel de dificultad (opcional)
     * @param daysPerWeek Días por semana (opcional)
     */
    fun createWorkout(
        name: String,
        description: String?,
        goal: String?,
        level: String?,
        daysPerWeek: Int?
    ) {
        viewModelScope.launch {
            _uiState.value = CreationUiState.Loading
            try {
                val workout = workoutRepository.createWorkout(
                    name = name,
                    description = description,
                    goal = goal,
                    level = level,
                    daysPerWeek = daysPerWeek,
                    userId = "" // TODO: Obtener del usuario actual
                ).first()
                _uiState.value = CreationUiState.Success(workout)
            } catch (e: Exception) {
                _uiState.value = CreationUiState.Error(e.localizedMessage ?: "Error al crear entrenamiento")
            }
        }
    }

    sealed class CreationUiState {
        object Idle : CreationUiState()
        object Loading : CreationUiState()
        data class Success(val createdWorkout: Workout) : CreationUiState()
        data class Error(val message: String) : CreationUiState()
    }
}

/**
 * Factory para crear el ViewModel (necesario para Hilt).
 */
class WorkoutCreationViewModelFactory @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutCreationViewModel(repository) as T
    }
}
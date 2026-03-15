package com.meta_force.meta_force.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.repository.WorkoutRepository
import com.meta_force.meta_force.ui.diets.DayUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Workout Detail screen.
 */
sealed class WorkoutDetailUiState {
    object Loading : WorkoutDetailUiState()
    data class Success(val workout: Workout) : WorkoutDetailUiState()
    data class Error(val message: String) : WorkoutDetailUiState()
}

/**
 * ViewModel for viewing details of a specific workout plan.
 *
 * @property workoutRepository Repository for workout data.
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    // Estado para rastrear el día actual que se está viendo (0 = Domingo, 6 = Sábado)
    private val _currentDayIndex = MutableStateFlow(0)
    val currentDayIndex: StateFlow<Int> = _currentDayIndex.asStateFlow()

    fun loadWorkout(id: String) {
        viewModelScope.launch {
            workoutRepository.getWorkout(id)
                .onStart { _uiState.value = WorkoutDetailUiState.Loading }
                .catch { e -> _uiState.value = WorkoutDetailUiState.Error(e.message ?: "Unknown error") }
                .collect { workout ->
                    _uiState.value = WorkoutDetailUiState.Success(workout)
                }
        }
    }

    /** Cambia al día siguiente, con bucle de sábado a domingo */
    fun nextDay() {
        _currentDayIndex.value = DayUtils.getNextDay(_currentDayIndex.value)
    }

    /** Cambia al día anterior, con bucle de domingo a sábado */
    fun previousDay() {
        _currentDayIndex.value = DayUtils.getPreviousDay(_currentDayIndex.value)
    }

    /** Establece un día específico */
    fun setDay(dayOfWeek: Int) {
        _currentDayIndex.value = dayOfWeek
    }
}

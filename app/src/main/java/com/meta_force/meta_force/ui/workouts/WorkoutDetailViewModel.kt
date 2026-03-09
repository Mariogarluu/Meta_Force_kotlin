package com.meta_force.meta_force.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WorkoutDetailUiState {
    object Loading : WorkoutDetailUiState()
    data class Success(val workout: Workout) : WorkoutDetailUiState()
    data class Error(val message: String) : WorkoutDetailUiState()
}

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

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
}

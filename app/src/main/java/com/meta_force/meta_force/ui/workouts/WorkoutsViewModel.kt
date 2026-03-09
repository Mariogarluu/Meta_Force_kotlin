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

sealed class WorkoutsUiState {
    object Loading : WorkoutsUiState()
    data class Success(val workouts: List<Workout>) : WorkoutsUiState()
    data class Error(val message: String) : WorkoutsUiState()
}

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            workoutRepository.getWorkouts()
                .onStart { _uiState.value = WorkoutsUiState.Loading }
                .catch { e -> _uiState.value = WorkoutsUiState.Error(e.message ?: "Unknown error") }
                .collect { workouts ->
                    _uiState.value = WorkoutsUiState.Success(workouts)
                }
        }
    }
    
    fun deleteWorkout(id: String) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(id)
                .catch { /* Handle error, maybe show toast */ }
                .collect {
                    loadWorkouts() // Refresh list
                }
        }
    }
}

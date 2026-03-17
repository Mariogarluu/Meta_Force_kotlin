package com.meta_force.meta_force.ui.workouts

import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.repository.ProgressRepository
import com.meta_force.meta_force.data.repository.WorkoutRepository
import com.meta_force.meta_force.data.repository.ExerciseRepository
import com.meta_force.meta_force.ui.diets.DayUtils
import com.meta_force.meta_force.data.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
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
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val progressRepository: ProgressRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    private val _currentDayIndex = MutableStateFlow(0)
    val currentDayIndex: StateFlow<Int> = _currentDayIndex.asStateFlow()

    // Historial del ejercicio seleccionado
    private val _exerciseHistory = MutableStateFlow<List<ExercisePerformanceLog>>(emptyList())
    val exerciseHistory: StateFlow<List<ExercisePerformanceLog>> = _exerciseHistory.asStateFlow()

    private val _isLogging = MutableStateFlow(false)
    val isLogging: StateFlow<Boolean> = _isLogging.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _availableExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val availableExercises: StateFlow<List<Exercise>> = _availableExercises.asStateFlow()

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

    fun loadExerciseHistory(exerciseId: String) {
        viewModelScope.launch {
            when (val result = progressRepository.getExerciseHistory(exerciseId)) {
                is NetworkResult.Success -> {
                    _exerciseHistory.value = result.data
                }
                else -> {
                    // Fail silently or handle error
                }
            }
        }
    }

    fun logPerformance(
        exerciseId: String,
        sets: String?,
        reps: String?,
        weight: Double?,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLogging.value = true
            val request = LogPerformanceRequest(exerciseId, sets, reps, weight, notes)
            when (val result = progressRepository.logPerformance(request)) {
                is NetworkResult.Success -> {
                    onSuccess()
                    loadExerciseHistory(exerciseId) // Refresh history
                }
                else -> {
                    // Handle error
                }
            }
            _isLogging.value = false
        }
    }

    /** Cambia al día siguiente, con bucle */
    fun nextDay() {
        _currentDayIndex.value = DayUtils.getNextDay(_currentDayIndex.value)
    }

    /** Cambia al día anterior, con bucle */
    fun previousDay() {
        _currentDayIndex.value = DayUtils.getPreviousDay(_currentDayIndex.value)
    }

    /** Establece un día específico */
    fun setDay(dayOfWeek: Int) {
        _currentDayIndex.value = dayOfWeek
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
        if (_isEditMode.value && _availableExercises.value.isEmpty()) {
            fetchAvailableExercises()
        }
    }

    fun fetchAvailableExercises() {
        viewModelScope.launch {
            exerciseRepository.getExercises()
                .catch { /* Handle error */ }
                .collect { exercises ->
                    _availableExercises.value = exercises
                }
        }
    }

    fun addExerciseToWorkout(workoutId: String, exerciseId: String, dayOfWeek: Int) {
        val currentWorkout = (uiState.value as? WorkoutDetailUiState.Success)?.workout
        val beDayOfWeek = (dayOfWeek + 1) % 7
        val nextOrder = ((currentWorkout?.exercises ?: emptyList()).filter { it.dayOfWeek == beDayOfWeek }.maxOfOrNull { it.order ?: 0 } ?: 0) + 1

        viewModelScope.launch {
            val request = AddExerciseToWorkoutRequest(
                exerciseId = exerciseId,
                dayOfWeek = beDayOfWeek,
                order = nextOrder,
                sets = "3", // Default values
                reps = "10"
            )
            workoutRepository.addExerciseToWorkout(workoutId, request)
                .catch { /* Handle error */ }
                .collect {
                    loadWorkout(workoutId) // Refresh
                }
        }
    }

    fun removeExerciseFromWorkout(workoutId: String, exerciseId: String) {
        viewModelScope.launch {
            workoutRepository.removeExerciseFromWorkout(exerciseId)
                .catch { /* Handle error */ }
                .collect {
                    loadWorkout(workoutId) // Refresh
                }
        }
    }
}

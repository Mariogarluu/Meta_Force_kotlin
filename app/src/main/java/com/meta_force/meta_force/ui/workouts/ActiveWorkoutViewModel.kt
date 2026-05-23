package com.meta_force.meta_force.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActiveWorkoutUiState {
    object Loading : ActiveWorkoutUiState()
    data class Success(
        val workout: Workout,
        val exercises: List<ActiveExerciseState>,
        val isFinished: Boolean = false
    ) : ActiveWorkoutUiState()
    data class Error(val message: String) : ActiveWorkoutUiState()
}

data class ActiveExerciseState(
    val id: String,
    val exerciseName: String,
    val muscleGroup: String?,
    val notes: String?,
    val totalSets: Int,
    val reps: String,
    val weight: Double?,
    val restSeconds: Int,
    val sets: List<ActiveSetState>
)

data class ActiveSetState(
    val index: Int,
    val isCompleted: Boolean = false
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private val _timerSecondsLeft = MutableStateFlow(0)
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _timerMaxSeconds = MutableStateFlow(0)
    val timerMaxSeconds: StateFlow<Int> = _timerMaxSeconds.asStateFlow()

    private val _timerActive = MutableStateFlow(false)
    val timerActive: StateFlow<Boolean> = _timerActive.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0)
    val elapsedTimeSeconds: StateFlow<Int> = _elapsedTimeSeconds.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    // Trigger state to let the UI know it needs to play alert (sound/vibe)
    private val _timerFinishedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val timerFinishedEvent: SharedFlow<Unit> = _timerFinishedEvent.asSharedFlow()

    private var restTimerJob: Job? = null
    private var elapsedTimerJob: Job? = null

    fun loadActiveWorkout(workoutId: String, dayOfWeek: Int) {
        viewModelScope.launch {
            _uiState.value = ActiveWorkoutUiState.Loading
            workoutRepository.getWorkout(workoutId)
                .catch { e ->
                    _uiState.value = ActiveWorkoutUiState.Error(e.message ?: "Error al cargar rutina")
                }
                .collect { workout ->
                    // 0=Lun, 6=Dom en UI -> El backend usa 1..7 (enviado como dayIndex + 1)
                    val beDayOfWeek = (dayOfWeek + 1) % 7
                    val workoutExercises = (workout.exercises ?: emptyList())
                        .filter { it.dayOfWeek == beDayOfWeek }
                        .sortedBy { it.order ?: 0 }

                    if (workoutExercises.isEmpty()) {
                        _uiState.value = ActiveWorkoutUiState.Error("No hay ejercicios planificados para hoy en esta rutina.")
                        return@collect
                    }

                    val activeExercises = workoutExercises.mapIndexed { idx, item ->
                        val setsCount = item.sets?.toIntOrNull() ?: 3
                        val activeSets = (1..setsCount).map { setIdx ->
                            ActiveSetState(index = setIdx, isCompleted = false)
                        }

                        ActiveExerciseState(
                            id = item.id ?: "ex_$idx",
                            exerciseName = item.exercise?.name ?: "Ejercicio",
                            muscleGroup = item.exercise?.muscleGroup,
                            notes = item.notes,
                            totalSets = setsCount,
                            reps = item.reps ?: "10",
                            weight = item.weight,
                            restSeconds = item.restSeconds ?: 60,
                            sets = activeSets
                        )
                    }

                    _uiState.value = ActiveWorkoutUiState.Success(
                        workout = workout,
                        exercises = activeExercises,
                        isFinished = false
                    )

                    startElapsedTimeTracker()
                }
        }
    }

    private fun startElapsedTimeTracker() {
        elapsedTimerJob?.cancel()
        elapsedTimerJob = viewModelScope.launch {
            _elapsedTimeSeconds.value = 0
            while (true) {
                delay(1000)
                _elapsedTimeSeconds.value += 1
            }
        }
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        val currentState = _uiState.value as? ActiveWorkoutUiState.Success ?: return
        val exercises = currentState.exercises.toMutableList()
        val targetExercise = exercises[exerciseIndex]
        val sets = targetExercise.sets.toMutableList()
        val targetSet = sets[setIndex]

        val newCompletedState = !targetSet.isCompleted
        sets[setIndex] = targetSet.copy(isCompleted = newCompletedState)
        exercises[exerciseIndex] = targetExercise.copy(sets = sets)

        _uiState.value = currentState.copy(exercises = exercises)

        // Si se completó una serie (cambió a true), iniciar el temporizador
        if (newCompletedState) {
            startRestTimer(targetExercise.restSeconds)
        }
    }

    fun setCurrentExercise(index: Int) {
        val currentState = _uiState.value as? ActiveWorkoutUiState.Success ?: return
        if (index in currentState.exercises.indices) {
            _currentExerciseIndex.value = index
        }
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _timerMaxSeconds.value = seconds
        _timerSecondsLeft.value = seconds
        _timerActive.value = true

        restTimerJob = viewModelScope.launch {
            while (_timerSecondsLeft.value > 0) {
                delay(1000)
                _timerSecondsLeft.value -= 1
            }
            _timerActive.value = false
            _timerFinishedEvent.tryEmit(Unit)
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _timerActive.value = false
        _timerSecondsLeft.value = 0
    }

    fun addRestTime(seconds: Int) {
        if (!_timerActive.value) return
        _timerSecondsLeft.value += seconds
        _timerMaxSeconds.value += seconds
    }

    fun subtractRestTime(seconds: Int) {
        if (!_timerActive.value) return
        val current = _timerSecondsLeft.value
        if (current <= seconds) {
            skipRestTimer()
        } else {
            _timerSecondsLeft.value -= seconds
            // No cambiamos el maxSeconds para conservar el porcentaje de progreso original visualmente
        }
    }

    fun finishWorkout() {
        val currentState = _uiState.value as? ActiveWorkoutUiState.Success ?: return
        elapsedTimerJob?.cancel()
        restTimerJob?.cancel()
        _timerActive.value = false
        _uiState.value = currentState.copy(isFinished = true)
    }

    override fun onCleared() {
        super.onCleared()
        elapsedTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}

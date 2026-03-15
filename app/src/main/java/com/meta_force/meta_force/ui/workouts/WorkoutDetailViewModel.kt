import com.meta_force.meta_force.data.model.ExercisePerformanceLog
import com.meta_force.meta_force.data.model.LogPerformanceRequest
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.ProgressRepository
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
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val progressRepository: ProgressRepository
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
        sets: Int?,
        reps: Int?,
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
}

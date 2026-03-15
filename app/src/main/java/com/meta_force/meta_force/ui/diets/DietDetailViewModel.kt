package com.meta_force.meta_force.ui.diets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.repository.DietRepository
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
 * UI State for the Diet Detail screen.
 */
sealed class DietDetailUiState {
    object Loading : DietDetailUiState()
    data class Success(val diet: Diet) : DietDetailUiState()
    data class Error(val message: String) : DietDetailUiState()
}

/**
 * ViewModel for viewing details of a specific dietary plan.
 *
 * @property dietRepository Repository for diet data.
 */
@HiltViewModel
class DietDetailViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DietDetailUiState>(DietDetailUiState.Loading)
    val uiState: StateFlow<DietDetailUiState> = _uiState.asStateFlow()

    // Estado para rastrear el día actual que se está viendo (0 = Domingo, 6 = Sábado)
    private val _currentDayIndex = MutableStateFlow(0)
    val currentDayIndex: StateFlow<Int> = _currentDayIndex.asStateFlow()

    fun loadDiet(id: String) {
        viewModelScope.launch {
            dietRepository.getDiet(id)
                .onStart { _uiState.value = DietDetailUiState.Loading }
                .catch { e -> _uiState.value = DietDetailUiState.Error(e.message ?: "Unknown error") }
                .collect { diet ->
                    _uiState.value = DietDetailUiState.Success(diet)
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

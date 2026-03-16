package com.meta_force.meta_force.ui.diets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.repository.DietRepository
import com.meta_force.meta_force.data.repository.MealRepository
import com.meta_force.meta_force.ui.diets.DayUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val dietRepository: DietRepository,
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DietDetailUiState>(DietDetailUiState.Loading)
    val uiState: StateFlow<DietDetailUiState> = _uiState.asStateFlow()

    // Estado para rastrear el día actual que se está viendo (0 = Lunes, 6 = Domingo)
    private val _currentDayIndex = MutableStateFlow(0)
    val currentDayIndex: StateFlow<Int> = _currentDayIndex.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _availableMeals = MutableStateFlow<List<MealInfo>>(emptyList())
    val availableMeals: StateFlow<List<MealInfo>> = _availableMeals.asStateFlow()

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

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
        if (_isEditMode.value && _availableMeals.value.isEmpty()) {
            fetchAvailableMeals()
        }
    }

    fun fetchAvailableMeals() {
        viewModelScope.launch {
            mealRepository.getMeals()
                .catch { /* Handle error */ }
                .collect { meals ->
                    _availableMeals.value = meals
                }
        }
    }

    fun addMealToDiet(dietId: String, mealId: String, dayOfWeek: Int) {
        val currentDiet = (uiState.value as? DietDetailUiState.Success)?.diet
        val beDayOfWeek = (dayOfWeek + 1) % 7
        val nextOrder = (currentDiet?.meals?.filter { it.dayOfWeek == beDayOfWeek }?.maxOfOrNull { it.order } ?: 0) + 1

        viewModelScope.launch {
            val request = AddMealToDietRequest(
                mealId = mealId,
                dayOfWeek = beDayOfWeek,
                mealType = "comida", // Valid type: desayuno, almuerzo, comida, merienda, cena
                order = nextOrder
            )
            dietRepository.addMealToDiet(dietId, request)
                .catch { /* Handle error, maybe update error state */ }
                .collect {
                    loadDiet(dietId) // Refresh
                }
        }
    }

    fun removeMealFromDiet(dietId: String, mealId: String) {
        viewModelScope.launch {
            dietRepository.removeMealFromDiet(mealId)
                .catch { /* Handle error, notify UI if needed */ }
                .collect {
                    loadDiet(dietId) // Refresh
                }
        }
    }
}

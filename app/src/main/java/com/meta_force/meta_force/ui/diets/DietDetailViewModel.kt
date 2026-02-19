package com.meta_force.meta_force.ui.diets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.repository.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DietDetailUiState {
    object Loading : DietDetailUiState()
    data class Success(val diet: Diet) : DietDetailUiState()
    data class Error(val message: String) : DietDetailUiState()
}

@HiltViewModel
class DietDetailViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DietDetailUiState>(DietDetailUiState.Loading)
    val uiState: StateFlow<DietDetailUiState> = _uiState.asStateFlow()

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
}

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

sealed class DietsUiState {
    object Loading : DietsUiState()
    data class Success(val diets: List<Diet>) : DietsUiState()
    data class Error(val message: String) : DietsUiState()
}

@HiltViewModel
class DietsViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DietsUiState>(DietsUiState.Loading)
    val uiState: StateFlow<DietsUiState> = _uiState.asStateFlow()

    init {
        loadDiets()
    }

    fun loadDiets() {
        viewModelScope.launch {
            dietRepository.getDiets()
                .onStart { _uiState.value = DietsUiState.Loading }
                .catch { e -> _uiState.value = DietsUiState.Error(e.message ?: "Unknown error") }
                .collect { diets ->
                    _uiState.value = DietsUiState.Success(diets)
                }
        }
    }
    
    fun deleteDiet(id: String) {
        viewModelScope.launch {
            dietRepository.deleteDiet(id)
                .catch { /* Handle error */ }
                .collect {
                    loadDiets()
                }
        }
    }
}

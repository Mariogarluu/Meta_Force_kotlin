package com.meta_force.meta_force.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassesUiState {
    object Loading : ClassesUiState()
    data class Success(val classes: List<GymClass>) : ClassesUiState()
    data class Error(val message: String) : ClassesUiState()
}

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassesUiState>(ClassesUiState.Loading)
    val uiState: StateFlow<ClassesUiState> = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    fun loadClasses() {
        viewModelScope.launch {
            classRepository.getClasses()
                .onStart { _uiState.value = ClassesUiState.Loading }
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Unknown error") }
                .collect { classes ->
                    _uiState.value = ClassesUiState.Success(classes)
                }
        }
    }

    fun joinClass(id: String) {
        viewModelScope.launch {
            classRepository.joinClass(id)
                .catch { /* Handle error */ }
                .collect { loadClasses() }
        }
    }

    fun leaveClass(id: String) {
        viewModelScope.launch {
            classRepository.leaveClass(id)
                .catch { /* Handle error */ }
                .collect { loadClasses() }
        }
    }
}

package com.meta_force.meta_force.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.repository.CenterRepository
import com.meta_force.meta_force.data.repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassesUiState {
    object Loading : ClassesUiState()
    data class Success(val classes: List<GymClass>) : ClassesUiState()
    data class Error(val message: String) : ClassesUiState()
}

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val centerRepository: CenterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassesUiState>(ClassesUiState.Loading)
    val uiState: StateFlow<ClassesUiState> = _uiState.asStateFlow()

    private val _centers = MutableStateFlow<List<Center>>(emptyList())
    val centers: StateFlow<List<Center>> = _centers.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // For simplicity, we define a trainer flow here or they could come from AuthRepository if there's a specific route
    // Since we don't have a specific `UserRepository` provided in the list for Trainers, we might fake it or assume basic role check.
    // In actual implementation, we'd need a way to get list of trainers.

    init {
        checkUserRole()
        loadCenters()
        loadClasses()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            val userResult = authRepository.getProfile()
            if (userResult is com.meta_force.meta_force.data.network.NetworkResult.Success) {
                val user = userResult.data
                _isAdmin.value = user.role == "SUPERADMIN" || user.role == "ADMIN_CENTER"
            } else {
                _isAdmin.value = false
            }
        }
    }

    fun loadCenters() {
        viewModelScope.launch {
            centerRepository.getCenters().fold(
                onSuccess = { _centers.value = it },
                onFailure = { /* Handle error getting centers */ }
            )
        }
    }

    fun loadClasses(centerId: String? = null) {
        viewModelScope.launch {
            classRepository.getClasses(centerId)
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

    // --- Admin Operations ---
    
    fun createClass(name: String, description: String?) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            classRepository.createClass(CreateClassInput(name, description))
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Error creating class") }
                .collect { loadClasses() }
        }
    }

    fun updateClass(id: String, name: String?, description: String?) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            classRepository.updateClass(id, UpdateClassInput(name, description))
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Error updating class") }
                .collect { loadClasses() }
        }
    }

    fun deleteClass(id: String) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            classRepository.deleteClass(id)
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Error deleting class") }
                .collect { loadClasses() }
        }
    }

    fun addCenterToClass(classId: String, centerId: String, trainerIds: List<String>, schedules: List<ScheduleInput>) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            classRepository.addCenterToClass(classId, AddCenterToClassInput(centerId, trainerIds, schedules))
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Error adding center to class") }
                .collect { loadClasses() }
        }
    }

    fun removeCenterFromClass(classId: String, centerId: String) {
        if (!_isAdmin.value) return
        viewModelScope.launch {
            classRepository.removeCenterFromClass(classId, centerId)
                .catch { e -> _uiState.value = ClassesUiState.Error(e.message ?: "Error removing center") }
                .collect { loadClasses() }
        }
    }
}

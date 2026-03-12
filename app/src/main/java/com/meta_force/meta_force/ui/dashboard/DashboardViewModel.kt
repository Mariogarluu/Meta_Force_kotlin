package com.meta_force.meta_force.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main Dashboard screen.
 * Currently primarily handles global session actions like logout.
 *
 * @property authRepository Repository for session management.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout() // Llama a la limpieza en el repositorio [cite: 70, 127]
            onLogoutSuccess() // Navega de vuelta al Login
        }
    }
}
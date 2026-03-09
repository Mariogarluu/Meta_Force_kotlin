package com.meta_force.meta_force

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: AuthRepository
) : ViewModel() {

    val startDestination: StateFlow<String> = repository.getAuthToken()
        .map { token ->
            if (token.isNullOrEmpty()) "login" else "dashboard"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "login" // Default to login while loading
        )
}

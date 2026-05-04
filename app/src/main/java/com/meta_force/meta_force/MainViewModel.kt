package com.meta_force.meta_force

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: AuthRepository
) : ViewModel() {

    val startDestination: StateFlow<String> = repository.getAuthToken()
        .map { token ->
            if (!token.isNullOrEmpty()) {
                // Restore session in Supabase client globally so native calls work
                try {
                    val supabase = SupabaseProvider.client
                    if (supabase.auth.currentAccessTokenOrNull() != token) {
                        // Pass an empty string for refresh token if we don't have one
                        supabase.auth.importAuthToken(token, "") 
                    }
                } catch (e: Exception) {
                    // Ignore errors during token import
                }
                "dashboard"
            } else {
                "login"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "login" // Default to login while loading
        )
}

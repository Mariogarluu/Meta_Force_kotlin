package com.meta_force.meta_force

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.repository.AuthRepository
import com.meta_force.meta_force.data.network.AccessApi
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val accessApi: AccessApi
) : ViewModel() {

    /**
     * Determina la pantalla inicial combinando:
     * - existencia de sesión
     * - resultado de `has_active_access` (suscripción activa o staff)
     */
    val startDestination: StateFlow<String> = repository.getAuthToken()
        .flatMapLatest { token -> computeStartDestination(token) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "login" // Default mientras se calcula
        )

    private fun computeStartDestination(token: String?): Flow<String> = flow {
        if (token.isNullOrEmpty()) {
            emit("login")
            return@flow
        }

        // Restaurar sesión en Supabase para que el cliente nativo funcione
        try {
            val supabase = SupabaseProvider.client
            val currentSession = supabase.auth.currentSessionOrNull()
            if (currentSession == null || currentSession.accessToken != token) {
                val refreshToken = repository.getRefreshToken().firstOrNull()
                supabase.auth.importAuthToken(token, refreshToken ?: "")
            }
        } catch (_: Exception) {
            // Ignoramos errores al importar el token; se forzará re-login si hace falta
        }

        // Comprobar acceso activo vía RPC.
        // Si falla la llamada, por resiliencia mandamos al dashboard.
        val destination = try {
            val hasAccess = accessApi.hasActiveAccess()
            if (hasAccess) "dashboard" else "access_locked"
        } catch (_: Exception) {
            "dashboard"
        }

        emit(destination)
    }
}

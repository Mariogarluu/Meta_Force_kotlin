package com.meta_force.meta_force.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.SignedQr
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.QrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * UI State for the QR access screen.
 *
 * @property qr Información firmada de la suscripción activa y token JWT.
 */
sealed class QrUiState {
    object Loading : QrUiState()
    data class Success(val qr: SignedQr) : QrUiState()
    data class Error(val message: String) : QrUiState()
}

/**
 * ViewModel for generating and updating the QR access code.
 * Llama a la Edge Function `qr-sign` para obtener un JWT firmado con la
 * suscripción activa del usuario.
 *
 * @property repository Repositorio de QR firmado.
 */
@HiltViewModel
class QrViewModel @Inject constructor(
    private val repository: QrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QrUiState>(QrUiState.Loading)
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = QrUiState.Loading
            when (val result = repository.getSignedQr()) {
                is NetworkResult.Success -> {
                    _uiState.value = QrUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = QrUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = QrUiState.Error(result.e.message ?: "Error loading QR")
                }
            }
        }
    }
}

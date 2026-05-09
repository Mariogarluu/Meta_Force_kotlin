package com.meta_force.meta_force.ui.billing.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.Subscription
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.MeSubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MySubscriptionUiState {
    object Loading : MySubscriptionUiState()
    data class Success(
        val subscriptions: List<Subscription>,
        val downloadingInvoiceId: String? = null
    ) : MySubscriptionUiState()

    data class Error(val message: String) : MySubscriptionUiState()
}

@HiltViewModel
class MySubscriptionViewModel @Inject constructor(
    private val repository: MeSubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MySubscriptionUiState>(MySubscriptionUiState.Loading)
    val uiState: StateFlow<MySubscriptionUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions() {
        viewModelScope.launch {
            _uiState.value = MySubscriptionUiState.Loading
            when (val result = repository.getMySubscriptions()) {
                is NetworkResult.Success -> {
                    _uiState.value = MySubscriptionUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = MySubscriptionUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = MySubscriptionUiState.Error(
                        result.e.message ?: "Error loading subscriptions"
                    )
                }
            }
        }
    }

    fun downloadInvoice(invoiceId: String) {
        val current = _uiState.value
        if (current !is MySubscriptionUiState.Success) return

        viewModelScope.launch {
            _uiState.value = current.copy(downloadingInvoiceId = invoiceId)
            when (val result = repository.getInvoiceSignedUrl(invoiceId)) {
                is NetworkResult.Success -> {
                    // En Android real abriríamos un CustomTabs/Intent; aquí dejamos el hook.
                    // La UI puede reaccionar a este resultado mediante efectos o navegación externa.
                    _uiState.value = current.copy(downloadingInvoiceId = null)
                }
                is NetworkResult.Error -> {
                    _uiState.value = MySubscriptionUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = MySubscriptionUiState.Error(
                        result.e.message ?: "Error downloading invoice"
                    )
                }
            }
        }
    }
}


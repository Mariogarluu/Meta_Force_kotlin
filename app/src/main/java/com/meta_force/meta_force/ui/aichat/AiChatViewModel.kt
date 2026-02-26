package com.meta_force.meta_force.ui.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta_force.meta_force.data.model.ChatMessage
import com.meta_force.meta_force.data.model.ChatMessageRequest
import com.meta_force.meta_force.data.model.ChatSession
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiChatUiState(
    val isLoading: Boolean = false,
    val messages: List<UiMessage> = emptyList(),
    val currentSessionId: String? = null,
    val sessions: List<ChatSession> = emptyList(),
    val error: String? = null
)

data class UiMessage(
    val role: String,
    val content: String
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val repository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        loadSessionsAndStart()
    }

    private fun loadSessionsAndStart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getSessions()) {
                is NetworkResult.Success -> {
                    val sessions = result.data
                    if (sessions.isNotEmpty()) {
                        val sortedSessions = sessions.sortedByDescending { it.createdAt }
                        val latest = sortedSessions.first()
                        val uiMessages = latest.messages.map { UiMessage(it.role, it.content) }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentSessionId = latest.id,
                            messages = uiMessages,
                            sessions = sortedSessions
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, sessions = emptyList())
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.e.message)
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(UiMessage("user", text))

        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val request = ChatMessageRequest(
                message = text,
                sessionId = _uiState.value.currentSessionId
            )
            
            when (val result = repository.sendMessage(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    val updatedMessages = _uiState.value.messages.toMutableList()
                    
                    updatedMessages.add(UiMessage("model", response.response.message))
                    
                    // Simple text representation if plan exists
                    response.response.plan?.let { plan ->
                       updatedMessages.add(UiMessage("model", "Plan generado: ${plan.name} (${plan.type})\n${plan.description}"))
                    }

                    _uiState.value = _uiState.value.copy(
                        currentSessionId = response.sessionId,
                        messages = updatedMessages,
                        isLoading = false
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.e.message)
                }
            }
        }
    }
    
    fun loadSession(sessionId: String) {
        val session = _uiState.value.sessions.find { it.id == sessionId }
        if (session != null) {
            val uiMessages = session.messages.map { UiMessage(it.role, it.content) }
            _uiState.value = _uiState.value.copy(
                currentSessionId = session.id,
                messages = uiMessages
            )
        }
    }

    fun startNewSession() {
        _uiState.value = _uiState.value.copy(
            currentSessionId = null,
            messages = emptyList()
        )
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteSession(sessionId)) {
                is NetworkResult.Success -> {
                    // Remove from list
                    val currentSessions = _uiState.value.sessions.filter { it.id != sessionId }
                    
                    // If deleted current, start new or pick next
                    if (_uiState.value.currentSessionId == sessionId) {
                        if (currentSessions.isNotEmpty()) {
                            val next = currentSessions.first()
                            val uiMsgs = next.messages.map { UiMessage(it.role, it.content) }
                            _uiState.value = _uiState.value.copy(
                                sessions = currentSessions,
                                currentSessionId = next.id,
                                messages = uiMsgs
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                sessions = emptyList(),
                                currentSessionId = null,
                                messages = emptyList()
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(sessions = currentSessions)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = _uiState.value.copy(error = result.e.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

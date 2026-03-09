package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.ChatResponse
import com.meta_force.meta_force.data.model.ChatMessageRequest
import com.meta_force.meta_force.data.model.ChatSession
import com.meta_force.meta_force.data.model.SavePlanRequest
import com.meta_force.meta_force.data.network.NetworkResult

interface AiRepository {
    suspend fun sendMessage(request: ChatMessageRequest): NetworkResult<ChatResponse>
    suspend fun getSessions(): NetworkResult<List<ChatSession>>
    suspend fun savePlan(request: SavePlanRequest): NetworkResult<Unit>
    suspend fun deleteSession(sessionId: String): NetworkResult<Unit>
}

package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.ChatResponse
import com.meta_force.meta_force.data.model.ChatMessageRequest
import com.meta_force.meta_force.data.model.ChatSession
import com.meta_force.meta_force.data.model.SavePlanRequest
import com.meta_force.meta_force.data.network.AiApi
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val api: AiApi
) : AiRepository {

    override suspend fun sendMessage(request: ChatMessageRequest): NetworkResult<ChatResponse> {
        return safeApiCall { api.sendMessage(request) }
    }

    override suspend fun getSessions(): NetworkResult<List<ChatSession>> {
        return safeApiCall { api.getSessions() }
    }

    override suspend fun savePlan(request: SavePlanRequest): NetworkResult<Unit> {
        return safeApiCall { api.savePlan(request) }
    }

    override suspend fun deleteSession(sessionId: String): NetworkResult<Unit> {
        return safeApiCall { api.deleteSession(sessionId) }
    }
}

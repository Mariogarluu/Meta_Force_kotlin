package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.ChatResponse
import com.meta_force.meta_force.data.model.ChatMessageRequest
import com.meta_force.meta_force.data.model.ChatSession
import com.meta_force.meta_force.data.model.SavePlanRequest
import com.meta_force.meta_force.data.network.NetworkResult

/**
 * Repository interface for AI-related operations.
 * Acts as a clean API for the UI layer to interact with AI features.
 */
interface AiRepository {
    /**
     * Sends a message to the AI.
     * @return [NetworkResult] with the [ChatResponse].
     */
    suspend fun sendMessage(request: ChatMessageRequest): NetworkResult<ChatResponse>

    /**
     * Retrieves all chat sessions.
     * @return [NetworkResult] with the list of [ChatSession].
     */
    suspend fun getSessions(): NetworkResult<List<ChatSession>>

    /**
     * Saves an AI-generated plan.
     * @return [NetworkResult] indicating success or failure.
     */
    suspend fun savePlan(request: SavePlanRequest): NetworkResult<Unit>

    /**
     * Deletes a chat session.
     * @param sessionId ID of the session to delete.
     * @return [NetworkResult] indicating success or failure.
     */
    suspend fun deleteSession(sessionId: String): NetworkResult<Unit>
}

package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.ChatResponse
import com.meta_force.meta_force.data.model.ChatMessageRequest
import com.meta_force.meta_force.data.model.ChatSession
import com.meta_force.meta_force.data.model.SavePlanRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AiApi {

    @POST("ai/chat")
    suspend fun sendMessage(@Body request: ChatMessageRequest): ChatResponse

    @GET("ai/sessions")
    suspend fun getSessions(): List<ChatSession>

    @POST("ai/save-plan")
    suspend fun savePlan(@Body request: SavePlanRequest)

    @DELETE("ai/sessions/{sessionId}")
    suspend fun deleteSession(@Path("sessionId") sessionId: String)
}

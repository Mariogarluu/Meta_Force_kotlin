package com.meta_force.meta_force.data.model

data class ChatMessageRequest(
    val message: String,
    val sessionId: String? = null
)

data class ChatResponse(
    val sessionId: String,
    val response: AiResponseContent
)

data class AiResponseContent(
    val message: String,
    val plan: AiGeneratedPlan? = null
)

data class AiGeneratedPlan(
    val type: String, // "WORKOUT" | "DIET"
    val name: String,
    val description: String,
    val days: List<AiPlanDay>
)

data class AiPlanDay(
    val dayOfWeek: Int,
    val items: List<AiPlanItem> // represents both exercises and meals
)

data class AiPlanItem(
    val name: String,
    val sets: Int? = null,
    val reps: Int? = null,
    val quantity: String? = null,
    val notes: String? = null
)

data class ChatSession(
    val id: String,
    val title: String?,
    val createdAt: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String,
    val content: String,
    val createdAt: String
)

data class SavePlanRequest(
    val plan: AiGeneratedPlan
)

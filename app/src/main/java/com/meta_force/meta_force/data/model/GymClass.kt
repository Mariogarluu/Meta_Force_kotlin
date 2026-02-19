package com.meta_force.meta_force.data.model

data class GymClass(
    val id: String,
    val name: String,
    val description: String?,
    val trainerId: String?,
    val centerId: String,
    val capacity: Int,
    val startTime: String, // ISO String
    val durationMinutes: Int,
    val participants: List<ClassParticipant> = emptyList()
)

data class ClassParticipant(
    val id: String,
    val userId: String,
    val joinedAt: String
)

data class JoinClassRequest(
    val classId: String
)

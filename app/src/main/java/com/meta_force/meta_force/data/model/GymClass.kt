package com.meta_force.meta_force.data.model

data class ClassCenterSchedule(
    val id: String,
    val classId: String,
    val centerId: String,
    val dayOfWeek: Int, // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    val startTime: String, // format HH:mm
    val endTime: String, // format HH:mm
    val center: CenterBasicInfo? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CenterBasicInfo(
    val id: String,
    val name: String
)

data class ClassTrainer(
    val id: String,
    val classId: String,
    val trainerId: String,
    val trainer: TrainerBasicInfo? = null,
    val createdAt: String? = null
)

data class TrainerBasicInfo(
    val id: String,
    val name: String,
    val profileImageUrl: String? = null
)

data class GymClass(
    val id: String,
    val name: String,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val trainers: List<ClassTrainer>? = emptyList(),
    val schedules: List<ClassCenterSchedule>? = emptyList(),
    val centers: List<CenterBasicInfo>? = emptyList(),
    // Keep backward compatible ones for existing views if needed temporarily
    val trainerId: String? = null,
    val centerId: String? = null,
    val capacity: Int? = null,
    val startTime: String? = null,
    val durationMinutes: Int? = null,
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

// DTOs para Creación y Actualización
data class CreateClassInput(
    val name: String,
    val description: String? = null
)

data class AddCenterToClassInput(
    val centerId: String,
    val trainerIds: List<String>,
    val schedules: List<ScheduleInput>
)

data class ScheduleInput(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

data class UpdateClassInput(
    val name: String? = null,
    val description: String? = null,
    val trainerIds: List<String>? = null,
    val schedules: List<UpdateScheduleInput>? = null
)

data class UpdateScheduleInput(
    val id: String? = null, // if exists update, else create
    val centerId: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

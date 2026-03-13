package com.meta_force.meta_force.data.model

/**
 * Represents a scheduled session of a gym class at a specific center.
 *
 * @property id Unique identifier for the schedule entry.
 * @property classId The ID of the gym class.
 * @property centerId The ID of the center where the class is held.
 * @property dayOfWeek The day of the week (0 = Sunday, 1 = Monday, ..., 6 = Saturday).
 * @property startTime Start time of the class (format: HH:mm).
 * @property endTime End time of the class (format: HH:mm).
 * @property center Basic information about the center.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Timestamp of last update.
 */
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

/**
 * Basic information about a fitness center.
 *
 * @property id Unique identifier for the center.
 * @property name Name of the center.
 */
data class CenterBasicInfo(
    val id: String,
    val name: String
)

/**
 * Represents a trainer assigned to a gym class.
 *
 * @property id Unique identifier for the assignment record.
 * @property classId The ID of the gym class.
 * @property trainerId The ID of the trainer.
 * @property trainer Detailed information about the trainer.
 * @property createdAt Timestamp of assignment.
 */
data class ClassTrainer(
    val id: String,
    val classId: String,
    val trainerId: String,
    val trainer: TrainerBasicInfo? = null,
    val createdAt: String? = null
)

/**
 * Basic information about a trainer.
 *
 * @property id Unique identifier for the trainer.
 * @property name Name of the trainer.
 * @property profileImageUrl Optional URL for the trainer's profile picture.
 */
data class TrainerBasicInfo(
    val id: String,
    val name: String,
    val profileImageUrl: String? = null
)

/**
 * Represents a gym class (e.g., Yoga, HIIT).
 *
 * @property id Unique identifier for the class.
 * @property name Name of the class.
 * @property description Detailed description of the class and its level.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last update timestamp.
 * @property trainers List of trainers associated with this class.
 * @property schedules List of schedule sessions for this class.
 * @property centers List of centers where this class is offered.
 * @property trainerId Deprecated: use [trainers] list instead.
 * @property centerId Deprecated: use [centers] or [schedules] instead.
 * @property capacity Maximum number of participants.
 * @property startTime Generic start time if applicable.
 * @property durationMinutes Duration of the class in minutes.
 * @property participants List of users currently joined in this class.
 */
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

/**
 * Represents a user who is participating in a gym class.
 *
 * @property id Unique identifier for the participation record.
 * @property userId The ID of the participant user.
 * @property joinedAt Timestamp when the user joined the class.
 */
data class ClassParticipant(
    val id: String,
    val userId: String,
    val joinedAt: String
)

/**
 * Request object for a user to join a gym class.
 *
 * @property classId The ID of the class the user wants to join.
 */
data class JoinClassRequest(
    val classId: String
)

/**
 * Input format for creating a new gym class.
 *
 * @property name Name of the new class.
 * @property description Optional description.
 */
data class CreateClassInput(
    val name: String,
    val description: String? = null
)

/**
 * Input format for adding a center and its schedules to a class.
 *
 * @property centerId ID of the center to add.
 * @property trainerIds List of trainer IDs assigned to this center for this class.
 * @property schedules List of scheduled sessions at this center.
 */
data class AddCenterToClassInput(
    val centerId: String,
    val trainerIds: List<String>,
    val schedules: List<ScheduleInput>
)

/**
 * Represents a schedule entry in class creation/update input.
 *
 * @property dayOfWeek Day of the week (0-6).
 * @property startTime Start time (HH:mm).
 * @property endTime End time (HH:mm).
 */
data class ScheduleInput(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

/**
 * Input format for updating an existing gym class.
 *
 * @property name Optional new name.
 * @property description Optional new description.
 * @property trainerIds Optional list of trainer IDs to associate globally or as default.
 * @property schedules Optional list of schedule updates.
 */
data class UpdateClassInput(
    val name: String? = null,
    val description: String? = null,
    val trainerIds: List<String>? = null,
    val schedules: List<UpdateScheduleInput>? = null
)

/**
 * Represents an update to a specific schedule entry.
 *
 * @property id Optional ID of the existing schedule entry to update. If null, a new entry is created.
 * @property centerId The center ID for this schedule.
 * @property dayOfWeek Day of the week.
 * @property startTime Start time.
 * @property endTime End time.
 */
data class UpdateScheduleInput(
    val id: String? = null, // if exists update, else create
    val centerId: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

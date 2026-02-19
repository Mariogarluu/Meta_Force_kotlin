package com.meta_force.meta_force.data.model

data class Workout(
    val id: String,
    val name: String,
    val description: String?,
    val userId: String,
    val exercises: List<WorkoutExercise> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

data class WorkoutExercise(
    val id: String,
    val workoutId: String,
    val exerciseId: String,
    val exercise: Exercise?, // Nested object often returned
    val dayOfWeek: Int,
    val order: Int,
    val sets: Int,
    val reps: Int,
    val weight: Double?,
    val restSeconds: Int?,
    val notes: String?
)

data class Exercise(
    val id: String,
    val name: String,
    val description: String?,
    val muscleGroup: String,
    val videoUrl: String?,
    val imageUrl: String?
)

data class CreateWorkoutRequest(
    val name: String,
    val description: String?,
    val goal: String?,
    val level: String?,
    val daysPerWeek: Int?
)

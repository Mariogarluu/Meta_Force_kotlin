package com.meta_force.meta_force.data.model

/**
 * Represents a workout plan consisting of multiple exercises.
 *
 * @property id Unique identifier for the workout.
 * @property name Name of the workout.
 * @property description Optional description of the workout's focus.
 * @property userId ID of the user who owns this workout.
 * @property exercises List of [WorkoutExercise] included in the workout.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last update timestamp.
 */
data class Workout(
    val id: String,
    val name: String,
    val description: String?,
    val userId: String,
    val exercises: List<WorkoutExercise> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

/**
 * Represents a specific exercise entry within a [Workout].
 *
 * @property id Unique identifier for the workout-exercise link.
 * @property workoutId ID of the parent workout.
 * @property exerciseId ID of the base [Exercise].
 * @property exercise Optional nested [Exercise] details.
 * @property dayOfWeek Day of the week this exercise is performed (e.g., 1 for Monday).
 * @property order The sequence position of this exercise in the workout day.
 * @property sets Number of sets to perform.
 * @property reps Number of repetitions or duration per set.
 * @property weight Recommended weight to use.
 * @property restSeconds Time to rest between sets in seconds.
 * @property notes Additional instructions for this specific exercise instance.
 */
data class WorkoutExercise(
    val id: String,
    val workoutId: String,
    val exerciseId: String,
    val exercise: Exercise?, // Nested object often returned
    val dayOfWeek: Int,
    val order: Int,
    val sets: String?,
    val reps: String?,
    val weight: Double?,
    val restSeconds: Int?,
    val notes: String?
)

/**
 * Represents a general exercise template (e.g., "Bench Press").
 *
 * @property id Unique identifier for the exercise.
 * @property name Name of the exercise.
 * @property description Details about form and execution.
 * @property muscleGroup The primary muscle group targeted.
 * @property videoUrl Optional URL to a demonstration video.
 * @property imageUrl Optional URL to a demonstration image.
 */
data class Exercise(
    val id: String,
    val name: String,
    val description: String?,
    val muscleGroup: String,
    val videoUrl: String?,
    val imageUrl: String?
)

/**
 * Request object for creating a new workout plan.
 *
 * @property name Name given to the workout.
 * @property description Optional description.
 * @property goal The target goal (e.g., "Weight Loss", "Muscle Gain").
 * @property level The difficulty level (e.g., "Beginner", "Advanced").
 * @property daysPerWeek Intended frequency of workouts per week.
 */
data class CreateWorkoutRequest(
    val name: String,
    val description: String?,
    val goal: String?,
    val level: String?,
    val daysPerWeek: Int?
)

/**
 * Request object for updating a workout plan.
 */
data class UpdateWorkoutRequest(
    val name: String? = null,
    val description: String? = null,
    val goal: String? = null,
    val level: String? = null,
    val daysPerWeek: Int? = null
)

/**
 * Request object for adding an exercise to a workout.
 */
data class AddExerciseToWorkoutRequest(
    val exerciseId: String,
    val dayOfWeek: Int,
    val order: Int? = null,
    val sets: String? = null,
    val reps: String? = null,
    val weight: Double? = null,
    val restSeconds: Int? = null,
    val notes: String? = null
)

/**
 * Request object for updating an exercise in a workout.
 */
data class UpdateWorkoutExerciseRequest(
    val dayOfWeek: Int? = null,
    val order: Int? = null,
    val sets: String? = null,
    val reps: String? = null,
    val weight: Double? = null,
    val restSeconds: Int? = null,
    val notes: String? = null
)

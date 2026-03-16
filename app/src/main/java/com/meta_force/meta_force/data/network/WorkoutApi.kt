package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.*
import retrofit2.http.*

/**
 * Retrofit interface for workout plan management API endpoints.
 */
interface WorkoutApi {
    /**
     * Retrieves all workout plans for the current user.
     *
     * @return A list of [Workout] objects.
     */
    @GET("workouts")
    suspend fun getWorkouts(): List<Workout>

    /**
     * Fetches details for a specific workout plan.
     *
     * @param id The ID of the workout plan.
     * @return The [Workout] details.
     */
    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") id: String): Workout

    /**
     * Creates a new workout plan.
     *
     * @param request The [CreateWorkoutRequest] data.
     * @return The newly created [Workout].
     */
    @POST("workouts")
    suspend fun createWorkout(@Body request: CreateWorkoutRequest): Workout

    /**
     * Updates an existing workout plan.
     */
    @PATCH("workouts/{id}")
    suspend fun updateWorkout(@Path("id") id: String, @Body request: UpdateWorkoutRequest): Workout

    /**
     * Adds an exercise to a workout.
     */
    @POST("workouts/{id}/exercises")
    suspend fun addExerciseToWorkout(@Path("id") id: String, @Body request: AddExerciseToWorkoutRequest): WorkoutExercise

    /**
     * Updates an exercise within a workout.
     */
    @PATCH("workouts/exercises/{exerciseId}")
    suspend fun updateWorkoutExercise(@Path("exerciseId") exerciseId: String, @Body request: UpdateWorkoutExerciseRequest): WorkoutExercise

    /**
     * Removes an exercise from a workout.
     */
    @DELETE("workouts/exercises/{exerciseId}")
    suspend fun removeExerciseFromWorkout(@Path("exerciseId") exerciseId: String)

    /**
     * Deletes a workout plan.
     */
    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: String)
}

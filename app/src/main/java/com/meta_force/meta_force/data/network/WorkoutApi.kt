package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.CreateWorkoutRequest
import com.meta_force.meta_force.data.model.Workout
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
     * Deletes a specific workout plan.
     *
     * @param id The ID of the workout to delete.
     */
    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: String)
}

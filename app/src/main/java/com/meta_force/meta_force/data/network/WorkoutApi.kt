package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.CreateWorkoutRequest
import com.meta_force.meta_force.data.model.Workout
import retrofit2.http.*

interface WorkoutApi {
    @GET("workouts")
    suspend fun getWorkouts(): List<Workout>

    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") id: String): Workout

    @POST("workouts")
    suspend fun createWorkout(@Body request: CreateWorkoutRequest): Workout

    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: String): Unit
}

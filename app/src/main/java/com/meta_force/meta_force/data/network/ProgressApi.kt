package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.ExercisePerformanceLog
import com.meta_force.meta_force.data.model.LogPerformanceRequest
import com.meta_force.meta_force.data.model.UserMeasurement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for progress and performance tracking API endpoints.
 */
interface ProgressApi {
    
    /**
     * Logs a new exercise performance.
     */
    @POST("progress/exercise")
    suspend fun logPerformance(@Body request: LogPerformanceRequest): ExercisePerformanceLog

    /**
     * Fetches the performance history for a specific exercise.
     */
    @GET("progress/exercise/{exerciseId}")
    suspend fun getExerciseHistory(@Path("exerciseId") exerciseId: String): List<ExercisePerformanceLog>

    /**
     * Fetches all measurement logs for the current user.
     */
    @GET("progress/measurements")
    suspend fun getMeasurements(): List<UserMeasurement>
}

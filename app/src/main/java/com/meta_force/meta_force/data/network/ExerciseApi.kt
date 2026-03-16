package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.Exercise
import retrofit2.http.GET

/**
 * Retrofit interface for the global exercise bank API.
 */
interface ExerciseApi {
    /**
     * Lists all available exercises from the bank.
     */
    @GET("exercises")
    suspend fun getExercises(): List<Exercise>
}

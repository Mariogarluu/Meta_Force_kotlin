package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.CreateDietRequest
import com.meta_force.meta_force.data.model.Diet
import retrofit2.http.*

/**
 * Retrofit interface for diet plan management API endpoints.
 */
interface DietApi {
    /**
     * Retrieves all diet plans for the current user.
     *
     * @return A list of [Diet] objects.
     */
    @GET("diets")
    suspend fun getDiets(): List<Diet>

    /**
     * Fetches details for a specific diet plan.
     *
     * @param id The ID of the diet plan.
     * @return The [Diet] details.
     */
    @GET("diets/{id}")
    suspend fun getDiet(@Path("id") id: String): Diet

    /**
     * Creates a new diet plan.
     *
     * @param request The [CreateDietRequest] data.
     * @return The newly created [Diet].
     */
    @POST("diets")
    suspend fun createDiet(@Body request: CreateDietRequest): Diet

    /**
     * Deletes a specific diet plan.
     *
     * @param id The ID of the diet to delete.
     */
    @DELETE("diets/{id}")
    suspend fun deleteDiet(@Path("id") id: String)
}

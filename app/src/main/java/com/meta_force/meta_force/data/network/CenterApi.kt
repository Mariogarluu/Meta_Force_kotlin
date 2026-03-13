package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import retrofit2.http.*

/**
 * Retrofit interface for fitness center management API endpoints.
 */
interface CenterApi {
    /**
     * Retrieves a list of all fitness centers.
     *
     * @return A list of [Center] objects representing the available centers.
     */
    @GET("centers")
    suspend fun getCenters(): List<Center>

    /**
     * Fetches details for a specific fitness center.
     *
     * @param id The unique identifier of the center.
     * @return The [Center] details.
     */
    @GET("centers/{id}")
    suspend fun getCenter(@Path("id") id: String): Center

    /**
     * Creates a new fitness center entry. (Admin only)
     *
     * @param input The [CreateCenterInput] data for the new center.
     * @return The newly created [Center] object.
     */
    @POST("centers")
    suspend fun createCenter(@Body input: CreateCenterInput): Center

    /**
     * Updates an existing fitness center's information. (Admin only)
     *
     * @param id The ID of the center to update.
     * @param input The [UpdateCenterInput] data.
     * @return The updated [Center] object.
     */
    @PUT("centers/{id}")
    suspend fun updateCenter(@Path("id") id: String, @Body input: UpdateCenterInput): Center

    /**
     * Deletes a fitness center from the system. (Admin only)
     *
     * @param id The ID of the center to delete.
     */
    @DELETE("centers/{id}")
    suspend fun deleteCenter(@Path("id") id: String)
}

package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import kotlin.Result

/**
 * Repository interface for managing fitness center data.
 */
interface CenterRepository {
    /**
     * Retrieves all available fitness centers.
     * @return [Result] containing a list of [Center]s.
     */
    suspend fun getCenters(): Result<List<Center>>

    /**
     * Fetches details for a specific fitness center.
     * @param id The ID of the center.
     * @return [Result] containing the [Center] details.
     */
    suspend fun getCenter(id: String): Result<Center>

    /**
     * Creates a new fitness center.
     * @param input Data for the new center.
     * @return [Result] containing the created [Center].
     */
    suspend fun createCenter(input: CreateCenterInput): Result<Center>

    /**
     * Updates an existing fitness center's information.
     * @param id ID of the center to update.
     * @param input Updated data.
     * @return [Result] containing the updated [Center].
     */
    suspend fun updateCenter(id: String, input: UpdateCenterInput): Result<Center>

    /**
     * Deletes a fitness center.
     * @param id ID of the center to delete.
     * @return [Result] indicating success or failure.
     */
    suspend fun deleteCenter(id: String): Result<Unit>
}

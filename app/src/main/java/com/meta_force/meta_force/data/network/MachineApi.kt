package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.MachineTypeModel
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for gym equipment and machine types API endpoints.
 */
interface MachineApi {
    /**
     * Retrieves the available machine types, optionally filtering by center.
     *
     * @param centerId Optional ID of a center to see machines available there.
     * @return A list of [MachineTypeModel] objects.
     */
    @GET("machines/types")
    suspend fun getMachineTypes(@Query("centerId") centerId: String? = null): List<MachineTypeModel>
}

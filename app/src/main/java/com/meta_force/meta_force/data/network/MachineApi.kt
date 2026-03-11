package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.MachineTypeModel
import retrofit2.http.GET
import retrofit2.http.Query

interface MachineApi {
    @GET("machines/types")
    suspend fun getMachineTypes(@Query("centerId") centerId: String? = null): List<MachineTypeModel>
}

package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import retrofit2.http.*

interface CenterApi {
    @GET("centers")
    suspend fun getCenters(): List<Center>

    @GET("centers/{id}")
    suspend fun getCenter(@Path("id") id: String): Center

    @POST("centers")
    suspend fun createCenter(@Body input: CreateCenterInput): Center

    @PUT("centers/{id}")
    suspend fun updateCenter(@Path("id") id: String, @Body input: UpdateCenterInput): Center

    @DELETE("centers/{id}")
    suspend fun deleteCenter(@Path("id") id: String)
}

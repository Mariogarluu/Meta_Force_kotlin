package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.GymClass
import retrofit2.http.*

interface ClassApi {
    @GET("classes")
    suspend fun getClasses(): List<GymClass>

    @POST("classes/{id}/join")
    suspend fun joinClass(@Path("id") id: String)

    @DELETE("classes/{id}/join")
    suspend fun leaveClass(@Path("id") id: String)
}

package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.GymClass
import com.meta_force.meta_force.data.model.CreateClassInput
import com.meta_force.meta_force.data.model.AddCenterToClassInput
import com.meta_force.meta_force.data.model.UpdateClassInput
import retrofit2.http.*

interface ClassApi {
    @GET("classes")
    suspend fun getClasses(@Query("centerId") centerId: String? = null): List<GymClass>

    @POST("classes/{id}/join")
    suspend fun joinClass(@Path("id") id: String)

    @DELETE("classes/{id}/join")
    suspend fun leaveClass(@Path("id") id: String)

    // Admin endpoints
    @POST("classes")
    suspend fun createClass(@Body input: CreateClassInput): GymClass

    @PUT("classes/{id}")
    suspend fun updateClass(@Path("id") id: String, @Body input: UpdateClassInput): GymClass

    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") id: String)

    @POST("classes/{id}/centers")
    suspend fun addCenterToClass(@Path("id") id: String, @Body input: AddCenterToClassInput): GymClass

    @PUT("classes/{classId}/centers/{centerId}")
    suspend fun updateCenterInClass(
        @Path("classId") classId: String, 
        @Path("centerId") centerId: String, 
        @Body input: UpdateClassInput // Using the same input layout or specific if needed
    ): GymClass

    @DELETE("classes/{classId}/centers/{centerId}")
    suspend fun removeCenterFromClass(
        @Path("classId") classId: String, 
        @Path("centerId") centerId: String
    ): GymClass
}

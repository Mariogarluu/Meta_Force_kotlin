package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.CreateDietRequest
import com.meta_force.meta_force.data.model.Diet
import retrofit2.http.*

interface DietApi {
    @GET("diets")
    suspend fun getDiets(): List<Diet>

    @GET("diets/{id}")
    suspend fun getDiet(@Path("id") id: String): Diet

    @POST("diets")
    suspend fun createDiet(@Body request: CreateDietRequest): Diet

    @DELETE("diets/{id}")
    suspend fun deleteDiet(@Path("id") id: String): Unit
}

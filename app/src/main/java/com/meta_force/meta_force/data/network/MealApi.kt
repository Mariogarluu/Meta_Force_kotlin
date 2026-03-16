package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.MealInfo
import retrofit2.http.GET

/**
 * Retrofit interface for the global meal bank API.
 */
interface MealApi {
    /**
     * Lists all available meals from the bank.
     */
    @GET("meals")
    suspend fun getMeals(): List<MealInfo>
}

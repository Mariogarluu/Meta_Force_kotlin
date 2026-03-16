package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.*
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
     * Updates an existing diet plan.
     */
    @PATCH("diets/{id}")
    suspend fun updateDiet(@Path("id") id: String, @Body request: UpdateDietRequest): Diet

    /**
     * Adds a meal to a diet.
     */
    @POST("diets/{id}/meals")
    suspend fun addMealToDiet(@Path("id") id: String, @Body request: AddMealToDietRequest): DietMeal

    /**
     * Updates a meal within a diet.
     */
    @PATCH("diets/meals/{mealId}")
    suspend fun updateDietMeal(@Path("mealId") mealId: String, @Body request: UpdateDietMealRequest): DietMeal

    /**
     * Removes a meal from a diet.
     */
    @DELETE("diets/meals/{mealId}")
    suspend fun removeMealFromDiet(@Path("mealId") mealId: String)

    /**
     * Deletes a diet plan.
     */
    @DELETE("diets/{id}")
    suspend fun deleteDiet(@Path("id") id: String)
}

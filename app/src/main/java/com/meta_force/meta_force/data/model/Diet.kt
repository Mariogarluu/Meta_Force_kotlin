package com.meta_force.meta_force.data.model

data class Diet(
    val id: String,
    val name: String,
    val description: String?,
    val userId: String,
    val caloriesTarget: Int?,
    val active: Boolean,
    val meals: List<DietMeal> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

data class DietMeal(
    val id: String,
    val dietId: String,
    val name: String, // e.g., "Breakfast", "Lunch"
    val dayOfWeek: Int,
    val order: Int,
    val time: String?, // "08:00"
    val calories: Int?,
    val protein: Double?,
    val carbs: Double?,
    val fats: Double?,
    val foods: List<DietFood> = emptyList()
)

data class DietFood(
    val id: String,
    val mealId: String,
    val name: String,
    val quantity: Double, // grams or logic unit
    val unit: String?,
    val calories: Int?,
    val protein: Double?,
    val carbs: Double?,
    val fats: Double?
)

data class CreateDietRequest(
    val name: String,
    val description: String?,
    val caloriesTarget: Int?
)

package com.meta_force.meta_force.data.model

/**
 * Represents a dietary plan for a user.
 *
 * @property id Unique identifier for the diet.
 * @property name The name of the diet plan.
 * @property description An optional description of the diet's goal or nature.
 * @property userId The ID of the user this diet belongs to.
 * @property caloriesTarget The daily calorie target for this diet.
 * @property active Whether this is currently the active diet for the user.
 * @property meals List of [DietMeal] associated with this diet.
 * @property createdAt Timestamp when the diet was created.
 * @property updatedAt Timestamp when the diet was last modified.
 */
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

/**
 * Represents a specific meal within a [Diet].
 *
 * @property id Unique identifier for the meal.
 * @property dietId The ID of the parent diet.
 * @property name Name of the meal (e.g., "Breakfast", "Lunch").
 * @property dayOfWeek Day of the week this meal is scheduled for (e.g., 1 for Monday).
 * @property order The order of the meal during the day.
 * @property time Scheduled time for the meal (format: "HH:mm").
 * @property calories Estimated calories for this meal.
 * @property protein Estimated protein content in grams.
 * @property carbs Estimated carbohydrate content in grams.
 * @property fats Estimated fat content in grams.
 * @property foods List of [DietFood] items included in this meal.
 */
data class DietMeal(
    val id: String,
    val dietId: String,
    val mealId: String,
    val name: String?, // Deprecated en favor de meal.name
    val dayOfWeek: Int?,
    val order: Int,
    val time: String?, // "08:00"
    val mealType: String?, // "desayuno", "almuerzo", etc.
    val quantity: Double?,
    val notes: String?,
    val meal: MealInfo? = null,
    val foods: List<DietFood> = emptyList()
)

data class MealInfo(
    val id: String,
    val name: String,
    val description: String?,
    val calories: String?,
    val protein: String?,
    val carbs: String?,
    val fats: String?,
    val imageUrl: String? = null
)

/**
 * Represents an individual food item within a [DietMeal].
 *
 * @property id Unique identifier for the food item entry.
 * @property mealId The ID of the parent meal.
 * @property name Name of the food item.
 * @property quantity Numerical quantity of the food.
 * @property unit Unit of measurement (e.g., "grams", "pieces").
 * @property calories Calories for this specific quantity.
 * @property protein Protein content for this specific quantity.
 * @property carbs Carbohydrate content for this specific quantity.
 * @property fats Fat content for this specific quantity.
 */
data class DietFood(
    val id: String,
    val mealId: String,
    val name: String,
    val quantity: Double, // grams or logic unit
    val unit: String?,
    val calories: String?,
    val protein: String?,
    val carbs: String?,
    val fats: String?
)

/**
 * Request object for creating a new dietary plan.
 */
data class CreateDietRequest(
    val name: String,
    val description: String? = null,
    val caloriesTarget: Int? = null
)

/**
 * Request object for updating a dietary plan.
 */
data class UpdateDietRequest(
    val name: String? = null,
    val description: String? = null,
    val caloriesTarget: Int? = null,
    val active: Boolean? = null
)

/**
 * Request object for adding a meal to a diet.
 */
data class AddMealToDietRequest(
    val mealId: String,
    val dayOfWeek: Int,
    val order: Int? = null,
    val time: String? = null,
    val mealType: String? = null,
    val quantity: Double? = null,
    val notes: String? = null
)

/**
 * Request object for updating a meal in a diet.
 */
data class UpdateDietMealRequest(
    val dayOfWeek: Int? = null,
    val order: Int? = null,
    val time: String? = null,
    val mealType: String? = null,
    val quantity: Double? = null,
    val notes: String? = null
)

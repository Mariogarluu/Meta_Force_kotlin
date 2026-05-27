package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll
import com.meta_force.meta_force.data.local.DietLocalDataSource
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.gotrue.auth
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import kotlinx.serialization.json.*

/**
 * Repository interface for managing user dietary plans.
 */
interface DietRepository {
    /**
     * Fetches all diet plans for the user.
     */
    fun getDiets(): Flow<List<Diet>>

    /**
     * Fetches details of a specific diet plan.
     */
    fun getDiet(id: String): Flow<Diet>

    /**
     * Creates a new dietary plan.
     */
    fun createDiet(
        name: String,
        description: String?,
        caloriesTarget: Int?,
        userId: String
    ): Flow<Diet>

    /**
     * Deletes a dietary plan.
     */
    fun deleteDiet(id: String): Flow<Unit>

    /**
     * Updates an existing dietary plan.
     */
    fun updateDiet(id: String, request: UpdateDietRequest): Flow<Diet>

    /**
     * Adds a meal to a specific diet.
     */
    fun addMealToDiet(id: String, request: AddMealToDietRequest): Flow<DietMeal>

    /**
     * Updates a meal within a diet.
     */
    fun updateDietMeal(mealId: String, request: UpdateDietMealRequest): Flow<DietMeal>

    /**
     * Removes a meal from a diet.
     */
    fun removeMealFromDiet(mealId: String): Flow<Unit>
}

/**
 * Implementation of [DietRepository] using Supabase PostgREST and [DietLocalDataSource].
 */
class DietRepositoryImpl @Inject constructor(
    private val localDataSource: DietLocalDataSource
) : DietRepository {
    private val supabase = SupabaseProvider.client

    override fun getDiets(): Flow<List<Diet>> = flow {
        try {
            val user = supabase.auth.currentUserOrNull() ?: throw Exception("Usuario no autenticado")
            val remoteJsonList = supabase.postgrest["Diet"]
                .select(columns = Columns.raw("*, meals:DietMeal(*, meal:Meal(*))")) {
                    filter { eq("userId", user.id) }
                }
                .decodeList<JsonObject>()
            val remoteDiets = remoteJsonList.map { it.toDiet() }
            localDataSource.saveDiets(remoteDiets)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignorar en caso de no tener internet, usará local
        }
        emitAll(localDataSource.getDiets())
    }

    override fun getDiet(id: String): Flow<Diet> = flow {
        try {
            val remoteJson = supabase.postgrest["Diet"]
                .select(columns = Columns.raw("*, meals:DietMeal(*, meal:Meal(*))")) {
                    filter { eq("id", id) }
                }
                .decodeSingle<JsonObject>()
            val remoteDiet = remoteJson.toDiet()
            localDataSource.saveDiet(remoteDiet)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignorar en caso de no tener internet
        }
        val localDiet = localDataSource.getDietById(id).getOrNull()
        if (localDiet != null) {
            emit(localDiet)
        } else {
            throw Exception("Diet no encontrada")
        }
    }

    override fun createDiet(
        name: String,
        description: String?,
        caloriesTarget: Int?,
        userId: String
    ): Flow<Diet> = flow {
        val finalUserId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Usuario no autenticado")
        val dietId = java.util.UUID.randomUUID().toString()
        val requestObject = buildJsonObject {
            put("id", dietId)
            put("name", name)
            put("description", description)
            put("userId", finalUserId)
        }
        val insertedJson = supabase.postgrest["Diet"]
            .insert(requestObject)
            .decodeSingle<JsonObject>()
        
        val newDiet = insertedJson.toDiet()
        localDataSource.saveDiet(newDiet)
        emit(newDiet)
    }

    override fun deleteDiet(id: String): Flow<Unit> = flow {
        supabase.postgrest["Diet"].delete {
            filter { eq("id", id) }
        }
        localDataSource.deleteDiet(id)
        emit(Unit)
    }

    override fun updateDiet(id: String, request: UpdateDietRequest): Flow<Diet> = flow {
        val updateObject = buildJsonObject {
            request.name?.let { put("name", it) }
            request.description?.let { put("description", it) }
            request.caloriesTarget?.let { put("caloriesTarget", it) }
        }
        val updatedJson = supabase.postgrest["Diet"]
            .update(updateObject) {
                filter { eq("id", id) }
            }
            .decodeSingle<JsonObject>()
        val updatedDiet = updatedJson.toDiet()
        localDataSource.saveDiet(updatedDiet)
        emit(updatedDiet)
    }

    override fun addMealToDiet(id: String, request: AddMealToDietRequest): Flow<DietMeal> = flow {
        val mealLinkId = java.util.UUID.randomUUID().toString()
        val requestObject = buildJsonObject {
            put("id", mealLinkId)
            put("dietId", id)
            put("mealId", request.mealId)
            put("dayOfWeek", request.dayOfWeek)
            request.order?.let { put("order", it) }
            request.mealType?.let { put("mealType", it) }
            request.quantity?.let { put("quantity", it) }
            request.notes?.let { put("notes", it) }
        }
        val insertedJson = supabase.postgrest["DietMeal"]
            .insert(requestObject)
            .decodeSingle<JsonObject>()
        
        val dietMealId = insertedJson["id"]?.jsonPrimitive?.contentOrNull ?: ""
        val populatedJson = supabase.postgrest["DietMeal"]
            .select(columns = Columns.raw("*, meal:Meal(*)")) {
                filter { eq("id", dietMealId) }
            }
            .decodeSingle<JsonObject>()
        val newMeal = populatedJson.toDietMeal()
        
        val diet = localDataSource.getDietById(id).getOrNull()
        if (diet != null) {
            val updatedMeals = diet.meals.orEmpty().toMutableList().apply { add(newMeal) }
            localDataSource.saveDiet(diet.copy(meals = updatedMeals))
        }
        emit(newMeal)
    }

    override fun updateDietMeal(mealId: String, request: UpdateDietMealRequest): Flow<DietMeal> = flow {
        val updateObject = buildJsonObject {
            request.dayOfWeek?.let { put("dayOfWeek", it) }
            request.order?.let { put("order", it) }
            request.mealType?.let { put("mealType", it) }
            request.quantity?.let { put("quantity", it) }
            request.notes?.let { put("notes", it) }
        }
        supabase.postgrest["DietMeal"]
            .update(updateObject) {
                filter { eq("id", mealId) }
            }
            
        val populatedJson = supabase.postgrest["DietMeal"]
            .select(columns = Columns.raw("*, meal:Meal(*)")) {
                filter { eq("id", mealId) }
            }
            .decodeSingle<JsonObject>()
        val updatedMeal = populatedJson.toDietMeal()
        emit(updatedMeal)
    }

    override fun removeMealFromDiet(mealId: String): Flow<Unit> = flow {
        supabase.postgrest["DietMeal"].delete {
            filter { eq("id", mealId) }
        }
        emit(Unit)
    }
}

// Extensión mappers para decodificar JSON robustamente evitando errores de coerción
private fun JsonObject.toMealInfo(): MealInfo {
    return MealInfo(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull,
        description = this["description"]?.jsonPrimitive?.contentOrNull,
        calories = this["calories"]?.jsonPrimitive?.contentOrNull ?: this["calories"]?.jsonPrimitive?.doubleOrNull?.toString(),
        protein = this["protein"]?.jsonPrimitive?.contentOrNull ?: this["protein"]?.jsonPrimitive?.doubleOrNull?.toString(),
        carbs = this["carbs"]?.jsonPrimitive?.contentOrNull ?: this["carbs"]?.jsonPrimitive?.doubleOrNull?.toString(),
        fats = this["fats"]?.jsonPrimitive?.contentOrNull ?: this["fats"]?.jsonPrimitive?.doubleOrNull?.toString(),
        imageUrl = this["imageUrl"]?.jsonPrimitive?.contentOrNull
    )
}

private fun JsonObject.toDietFood(): DietFood {
    return DietFood(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        mealId = this["mealId"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull,
        quantity = this["quantity"]?.jsonPrimitive?.doubleOrNull,
        unit = this["unit"]?.jsonPrimitive?.contentOrNull,
        calories = this["calories"]?.jsonPrimitive?.contentOrNull ?: this["calories"]?.jsonPrimitive?.doubleOrNull?.toString(),
        protein = this["protein"]?.jsonPrimitive?.contentOrNull ?: this["protein"]?.jsonPrimitive?.doubleOrNull?.toString(),
        carbs = this["carbs"]?.jsonPrimitive?.contentOrNull ?: this["carbs"]?.jsonPrimitive?.doubleOrNull?.toString(),
        fats = this["fats"]?.jsonPrimitive?.contentOrNull ?: this["fats"]?.jsonPrimitive?.doubleOrNull?.toString()
    )
}

private fun JsonObject.toDietMeal(): DietMeal {
    val mealObj = this["meal"]?.jsonObject?.toMealInfo()
    val foodsList = this["foods"]?.jsonArray?.map { it.jsonObject.toDietFood() } ?: emptyList()
    return DietMeal(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        dietId = this["dietId"]?.jsonPrimitive?.contentOrNull,
        mealId = this["mealId"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull ?: mealObj?.name,
        dayOfWeek = this["dayOfWeek"]?.jsonPrimitive?.intOrNull,
        order = this["order"]?.jsonPrimitive?.intOrNull,
        time = this["time"]?.jsonPrimitive?.contentOrNull,
        mealType = this["mealType"]?.jsonPrimitive?.contentOrNull,
        quantity = this["quantity"]?.jsonPrimitive?.doubleOrNull,
        notes = this["notes"]?.jsonPrimitive?.contentOrNull,
        meal = mealObj,
        foods = foodsList
    )
}

private fun JsonObject.toDiet(): Diet {
    val mealsList = this["meals"]?.jsonArray?.map { it.jsonObject.toDietMeal() } ?: emptyList()
    return Diet(
        id = this["id"]?.jsonPrimitive?.contentOrNull,
        name = this["name"]?.jsonPrimitive?.contentOrNull,
        description = this["description"]?.jsonPrimitive?.contentOrNull,
        userId = this["userId"]?.jsonPrimitive?.contentOrNull,
        caloriesTarget = this["caloriesTarget"]?.jsonPrimitive?.intOrNull,
        active = this["active"]?.jsonPrimitive?.booleanOrNull,
        meals = mealsList,
        createdAt = this["createdAt"]?.jsonPrimitive?.contentOrNull,
        updatedAt = this["updatedAt"]?.jsonPrimitive?.contentOrNull
    )
}

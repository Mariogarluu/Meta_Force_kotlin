package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.network.DietApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll
import com.meta_force.meta_force.data.local.DietLocalDataSource

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
 * Implementation of [DietRepository] using [DietApi] and [DietLocalDataSource].
 */
class DietRepositoryImpl @Inject constructor(
    private val api: DietApi,
    private val localDataSource: DietLocalDataSource
) : DietRepository {
    override fun getDiets(): Flow<List<Diet>> = flow {
        try {
            val remoteDiets = api.getDiets()
            localDataSource.saveDiets(remoteDiets)
        } catch (e: Exception) {
            // Ignorar en caso de no tener internet, usará local
        }
        emitAll(localDataSource.getDiets())
    }

    override fun getDiet(id: String): Flow<Diet> = flow {
        try {
            val remoteDiet = api.getDiet(id)
            localDataSource.saveDiet(remoteDiet)
        } catch (e: Exception) {
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
        val newDiet = api.createDiet(
            CreateDietRequest(
                name = name,
                description = description,
                caloriesTarget = caloriesTarget
            )
        )
        localDataSource.saveDiet(newDiet)
        emit(newDiet)
    }

    override fun deleteDiet(id: String): Flow<Unit> = flow {
        api.deleteDiet(id)
        localDataSource.deleteDiet(id)
        emit(Unit)
    }

    override fun updateDiet(id: String, request: UpdateDietRequest): Flow<Diet> = flow {
        val updatedDiet = api.updateDiet(id, request)
        localDataSource.saveDiet(updatedDiet)
        emit(updatedDiet)
    }

    override fun addMealToDiet(id: String, request: AddMealToDietRequest): Flow<DietMeal> = flow {
        val newMeal = api.addMealToDiet(id, request)
        val diet = localDataSource.getDietById(id).getOrNull()
        if (diet != null) {
            val updatedMeals = diet.meals.orEmpty().toMutableList().apply { add(newMeal) }
            localDataSource.saveDiet(diet.copy(meals = updatedMeals))
        }
        emit(newMeal)
    }

    override fun updateDietMeal(mealId: String, request: UpdateDietMealRequest): Flow<DietMeal> = flow {
        val updatedMeal = api.updateDietMeal(mealId, request)
        // La actualización de dietas completas se gestionará mayormente al volver a consultar
        emit(updatedMeal)
    }

    override fun removeMealFromDiet(mealId: String): Flow<Unit> = flow {
        api.removeMealFromDiet(mealId)
        emit(Unit)
    }
}

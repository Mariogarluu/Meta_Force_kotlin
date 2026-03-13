package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.CreateDietRequest
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.network.DietApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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
    fun createDiet(request: CreateDietRequest): Flow<Diet>

    /**
     * Deletes a dietary plan.
     */
    fun deleteDiet(id: String): Flow<Unit>
}

/**
 * Implementation of [DietRepository] using [DietApi].
 */
class DietRepositoryImpl @Inject constructor(
    private val api: DietApi
) : DietRepository {
    override fun getDiets(): Flow<List<Diet>> = flow {
        emit(api.getDiets())
    }

    override fun getDiet(id: String): Flow<Diet> = flow {
        emit(api.getDiet(id))
    }

    override fun createDiet(request: CreateDietRequest): Flow<Diet> = flow {
        emit(api.createDiet(request))
    }

    override fun deleteDiet(id: String): Flow<Unit> = flow {
        emit(api.deleteDiet(id))
    }
}

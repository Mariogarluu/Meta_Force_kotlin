package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.CreateDietRequest
import com.meta_force.meta_force.data.model.Diet
import com.meta_force.meta_force.data.network.DietApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface DietRepository {
    fun getDiets(): Flow<List<Diet>>
    fun getDiet(id: String): Flow<Diet>
    fun createDiet(request: CreateDietRequest): Flow<Diet>
    fun deleteDiet(id: String): Flow<Unit>
}

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

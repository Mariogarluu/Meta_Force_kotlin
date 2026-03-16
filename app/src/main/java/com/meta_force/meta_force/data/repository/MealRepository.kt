package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.MealInfo
import com.meta_force.meta_force.data.network.MealApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MealRepository {
    fun getMeals(): Flow<List<MealInfo>>
}

class MealRepositoryImpl @Inject constructor(
    private val api: MealApi
) : MealRepository {
    override fun getMeals(): Flow<List<MealInfo>> = flow {
        emit(api.getMeals())
    }
}

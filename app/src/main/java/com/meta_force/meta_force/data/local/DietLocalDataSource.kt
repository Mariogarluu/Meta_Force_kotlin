package com.meta_force.meta_force.data.local

import com.meta_force.meta_force.data.model.Diet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DietLocalDataSource @Inject constructor(
    private val dietDao: DietDao
) {
    fun getDiets(): Flow<List<Diet>> {
        return dietDao.getDiets().map { it.toModelList() }
    }

    suspend fun getDietById(id: String): Result<Diet> {
        val entity = dietDao.getDietById(id)
        return if (entity != null) {
            Result.success(entity.toModel())
        } else {
            Result.failure(Exception("Diet not found in local cache"))
        }
    }

    suspend fun saveDiet(diet: Diet) {
        dietDao.insertDiet(diet.toEntity())
    }

    suspend fun saveDiets(diets: List<Diet>) {
        dietDao.insertDiets(diets.toEntityList())
    }

    suspend fun deleteDiet(id: String) {
        dietDao.deleteDiet(id)
    }
}

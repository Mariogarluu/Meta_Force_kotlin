package com.meta_force.meta_force.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DietDao {
    @Query("SELECT * FROM diets")
    fun getDiets(): Flow<List<DietEntity>>

    @Query("SELECT * FROM diets WHERE id = :id")
    suspend fun getDietById(id: String): DietEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiet(diet: DietEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiets(diets: List<DietEntity>)

    @Query("DELETE FROM diets WHERE id = :id")
    suspend fun deleteDiet(id: String)
}

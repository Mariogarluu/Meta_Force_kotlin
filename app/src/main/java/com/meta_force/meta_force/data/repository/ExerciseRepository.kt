package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Exercise
import com.meta_force.meta_force.data.network.ExerciseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
}

class ExerciseRepositoryImpl @Inject constructor(
    private val api: ExerciseApi
) : ExerciseRepository {
    override fun getExercises(): Flow<List<Exercise>> = flow {
        emit(api.getExercises())
    }
}

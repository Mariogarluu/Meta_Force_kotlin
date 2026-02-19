package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.CreateWorkoutRequest
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.network.WorkoutApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface WorkoutRepository {
    fun getWorkouts(): Flow<List<Workout>>
    fun getWorkout(id: String): Flow<Workout>
    fun createWorkout(request: CreateWorkoutRequest): Flow<Workout>
    fun deleteWorkout(id: String): Flow<Unit>
}

class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutApi
) : WorkoutRepository {
    override fun getWorkouts(): Flow<List<Workout>> = flow {
        emit(api.getWorkouts())
    }

    override fun getWorkout(id: String): Flow<Workout> = flow {
        emit(api.getWorkout(id))
    }

    override fun createWorkout(request: CreateWorkoutRequest): Flow<Workout> = flow {
        emit(api.createWorkout(request))
    }

    override fun deleteWorkout(id: String): Flow<Unit> = flow {
        emit(api.deleteWorkout(id))
    }
}

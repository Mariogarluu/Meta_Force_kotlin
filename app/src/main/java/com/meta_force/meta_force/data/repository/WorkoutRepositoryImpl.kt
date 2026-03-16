package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.network.WorkoutApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [WorkoutRepository] using [WorkoutApi].
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutApi
) : WorkoutRepository {
    override fun getWorkouts(): Flow<List<Workout>> = flow {
        emit(api.getWorkouts())
    }

    override fun getWorkout(id: String): Flow<Workout> = flow {
        emit(api.getWorkout(id))
    }

    override fun createWorkout(
        name: String,
        description: String?,
        goal: String?,
        level: String?,
        daysPerWeek: Int?,
        userId: String
    ): Flow<Workout> = flow {
        emit(api.createWorkout(
            CreateWorkoutRequest(
                name = name,
                description = description,
                goal = goal,
                level = level,
                daysPerWeek = daysPerWeek
            )
        ))
    }

    override fun deleteWorkout(id: String): Flow<Unit> = flow {
        emit(api.deleteWorkout(id))
    }

    override fun updateWorkout(id: String, request: UpdateWorkoutRequest): Flow<Workout> = flow {
        emit(api.updateWorkout(id, request))
    }

    override fun addExerciseToWorkout(id: String, request: AddExerciseToWorkoutRequest): Flow<WorkoutExercise> = flow {
        emit(api.addExerciseToWorkout(id, request))
    }

    override fun updateWorkoutExercise(exerciseId: String, request: UpdateWorkoutExerciseRequest): Flow<WorkoutExercise> = flow {
        emit(api.updateWorkoutExercise(exerciseId, request))
    }

    override fun removeExerciseFromWorkout(exerciseId: String): Flow<Unit> = flow {
        emit(api.removeExerciseFromWorkout(exerciseId))
    }
}
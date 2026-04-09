package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.*
import com.meta_force.meta_force.data.network.WorkoutApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

import kotlinx.coroutines.flow.emitAll
import com.meta_force.meta_force.data.local.WorkoutLocalDataSource

/**
 * Implementation of [WorkoutRepository] using [WorkoutApi] and [WorkoutLocalDataSource].
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutApi,
    private val localDataSource: WorkoutLocalDataSource
) : WorkoutRepository {
    override fun getWorkouts(): Flow<List<Workout>> = flow {
        try {
            val remoteWorkouts = api.getWorkouts()
            localDataSource.saveWorkouts(remoteWorkouts)
        } catch (e: Exception) {
            // Ignorar en caso de no tener internet, usará local
        }
        emitAll(localDataSource.getWorkouts())
    }

    override fun getWorkout(id: String): Flow<Workout> = flow {
        try {
            val remoteWorkout = api.getWorkout(id)
            localDataSource.saveWorkout(remoteWorkout)
        } catch (e: Exception) {
            // Ignorar en caso de no tener internet
        }
        val localWorkout = localDataSource.getWorkoutById(id).getOrNull()
        if (localWorkout != null) {
            emit(localWorkout)
        } else {
            throw Exception("Workout no encontrado")
        }
    }

    override fun createWorkout(
        name: String,
        description: String?,
        goal: String?,
        level: String?,
        daysPerWeek: Int?,
        userId: String
    ): Flow<Workout> = flow {
        val newWorkout = api.createWorkout(
            CreateWorkoutRequest(
                name = name,
                description = description,
                goal = goal,
                level = level,
                daysPerWeek = daysPerWeek
            )
        )
        localDataSource.saveWorkout(newWorkout)
        emit(newWorkout)
    }

    override fun deleteWorkout(id: String): Flow<Unit> = flow {
        api.deleteWorkout(id)
        localDataSource.deleteWorkout(id)
        emit(Unit)
    }

    override fun updateWorkout(id: String, request: UpdateWorkoutRequest): Flow<Workout> = flow {
        val updatedWorkout = api.updateWorkout(id, request)
        localDataSource.saveWorkout(updatedWorkout)
        emit(updatedWorkout)
    }

    override fun addExerciseToWorkout(id: String, request: AddExerciseToWorkoutRequest): Flow<WorkoutExercise> = flow {
        val newExercise = api.addExerciseToWorkout(id, request)
        val workout = localDataSource.getWorkoutById(id).getOrNull()
        if (workout != null) {
            val updatedExercises = (workout.exercises ?: emptyList()).toMutableList().apply { add(newExercise) }
            localDataSource.saveWorkout(workout.copy(exercises = updatedExercises))
        }
        emit(newExercise)
    }

    override fun updateWorkoutExercise(exerciseId: String, request: UpdateWorkoutExerciseRequest): Flow<WorkoutExercise> = flow {
        val updatedExercise = api.updateWorkoutExercise(exerciseId, request)
        emit(updatedExercise)
    }

    override fun removeExerciseFromWorkout(exerciseId: String): Flow<Unit> = flow {
        api.removeExerciseFromWorkout(exerciseId)
        emit(Unit)
    }
}
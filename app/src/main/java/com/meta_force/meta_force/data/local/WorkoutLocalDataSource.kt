package com.meta_force.meta_force.data.local

import com.meta_force.meta_force.data.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutLocalDataSource @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    fun getWorkouts(): Flow<List<Workout>> {
        return workoutDao.getWorkouts().map { it.toModelList() }
    }

    suspend fun getWorkoutById(id: String): Result<Workout> {
        val entity = workoutDao.getWorkoutById(id)
        return if (entity != null) {
            Result.success(entity.toModel())
        } else {
            Result.failure(Exception("Workout not found in local cache"))
        }
    }

    suspend fun saveWorkout(workout: Workout) {
        workoutDao.insertWorkout(workout.toEntity())
    }

    suspend fun saveWorkouts(workouts: List<Workout>) {
        workoutDao.insertWorkouts(workouts.toEntityList())
    }

    suspend fun deleteWorkout(id: String) {
        workoutDao.deleteWorkout(id)
    }
}

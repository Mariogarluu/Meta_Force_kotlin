package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.CreateWorkoutRequest
import com.meta_force.meta_force.data.model.Workout
import com.meta_force.meta_force.data.network.WorkoutApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository interface for managing workout plans.
 */
interface WorkoutRepository {
    /**
     * Fetches all workouts for the user.
     */
    fun getWorkouts(): Flow<List<Workout>>

    /**
     * Fetches details of a specific workout.
     */
    fun getWorkout(id: String): Flow<Workout>

    /**
     * Creates a new workout plan.
     */
    fun createWorkout(
        name: String,
        description: String?,
        goal: String?,
        level: String?,
        daysPerWeek: Int?,
        userId: String
    ): Flow<Workout>

    /**
     * Deletes a workout plan.
     */
    fun deleteWorkout(id: String): Flow<Unit>
}

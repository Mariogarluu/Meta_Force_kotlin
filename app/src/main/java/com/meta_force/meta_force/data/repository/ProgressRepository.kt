package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.ExercisePerformanceLog
import com.meta_force.meta_force.data.model.LogPerformanceRequest
import com.meta_force.meta_force.data.model.UserMeasurement
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.ProgressApi
import com.meta_force.meta_force.data.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface ProgressRepository {
    suspend fun logPerformance(request: LogPerformanceRequest): NetworkResult<ExercisePerformanceLog>
    suspend fun getExerciseHistory(exerciseId: String): NetworkResult<List<ExercisePerformanceLog>>
    suspend fun getMeasurements(): NetworkResult<List<UserMeasurement>>
}

class ProgressRepositoryImpl @Inject constructor(
    private val api: ProgressApi
) : ProgressRepository {

    override suspend fun logPerformance(request: LogPerformanceRequest): NetworkResult<ExercisePerformanceLog> = safeApiCall {
        api.logPerformance(request)
    }

    override suspend fun getExerciseHistory(exerciseId: String): NetworkResult<List<ExercisePerformanceLog>> = safeApiCall {
        api.getExerciseHistory(exerciseId)
    }

    override suspend fun getMeasurements(): NetworkResult<List<UserMeasurement>> = safeApiCall {
        api.getMeasurements()
    }
}

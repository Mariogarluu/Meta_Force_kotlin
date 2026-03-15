package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.ExercisePerformanceLog
import com.meta_force.meta_force.data.model.LogPerformanceRequest
import com.meta_force.meta_force.data.model.UserMeasurement
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.ProgressApi
import com.meta_force.meta_force.data.network.SafeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val api: ProgressApi
) : SafeApiCall() {

    suspend fun logPerformance(request: LogPerformanceRequest): NetworkResult<ExercisePerformanceLog> = safeApiCall {
        api.logPerformance(request)
    }

    suspend fun getExerciseHistory(exerciseId: String): NetworkResult<List<ExercisePerformanceLog>> = safeApiCall {
        api.getExerciseHistory(exerciseId)
    }

    suspend fun getMeasurements(): NetworkResult<List<UserMeasurement>> = safeApiCall {
        api.getMeasurements()
    }
}

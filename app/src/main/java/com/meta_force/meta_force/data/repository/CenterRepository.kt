package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput

interface CenterRepository {
    suspend fun getCenters(): Result<List<Center>>
    suspend fun getCenter(id: String): Result<Center>
    suspend fun createCenter(input: CreateCenterInput): Result<Center>
    suspend fun updateCenter(id: String, input: UpdateCenterInput): Result<Center>
    suspend fun deleteCenter(id: String): Result<Unit>
}

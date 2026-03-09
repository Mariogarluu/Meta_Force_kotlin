package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import com.meta_force.meta_force.data.network.CenterApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CenterRepositoryImpl @Inject constructor(
    private val centerApi: CenterApi
) : CenterRepository {

    override suspend fun getCenters(): Result<List<Center>> = runCatching {
        centerApi.getCenters()
    }

    override suspend fun getCenter(id: String): Result<Center> = runCatching {
        centerApi.getCenter(id)
    }

    override suspend fun createCenter(input: CreateCenterInput): Result<Center> = runCatching {
        centerApi.createCenter(input)
    }

    override suspend fun updateCenter(id: String, input: UpdateCenterInput): Result<Center> = runCatching {
        centerApi.updateCenter(id, input)
    }

    override suspend fun deleteCenter(id: String): Result<Unit> = runCatching {
        centerApi.deleteCenter(id)
    }
}

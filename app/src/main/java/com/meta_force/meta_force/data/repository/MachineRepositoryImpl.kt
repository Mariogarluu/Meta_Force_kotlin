package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.MachineTypeModel
import com.meta_force.meta_force.data.network.MachineApi
import javax.inject.Inject

class MachineRepositoryImpl @Inject constructor(
    private val machineApi: MachineApi
) : MachineRepository {

    override suspend fun getMachineTypes(centerId: String?): Result<List<MachineTypeModel>> {
        return try {
            val machines = machineApi.getMachineTypes(centerId)
            Result.success(machines)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.MachineTypeModel

interface MachineRepository {
    suspend fun getMachineTypes(centerId: String? = null): Result<List<MachineTypeModel>>
}

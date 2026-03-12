package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.MachineTypeModel

/**
 * Repository interface for gym equipment and machine types.
 */
interface MachineRepository {
    /**
     * Retrieves machine types, optionally filtered by center.
     */
    suspend fun getMachineTypes(centerId: String? = null): Result<List<MachineTypeModel>>
}

package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.MachineTypeModel
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

/**
 * Concrete implementation of [MachineRepository] using Supabase PostgREST directly.
 */
class MachineRepositoryImpl @Inject constructor() : MachineRepository {
    private val supabase = SupabaseProvider.client

    override suspend fun getMachineTypes(centerId: String?): Result<List<MachineTypeModel>> = runCatching {
        val rawColumns = """
            id,
            name,
            type,
            createdAt,
            updatedAt,
            instances:Machine(
                id,
                machineTypeId,
                instanceNumber,
                centerId,
                status,
                createdAt,
                updatedAt
            )
        """.trimIndent()

        val allTypes = supabase.postgrest["MachineType"]
            .select(columns = Columns.raw(rawColumns))
            .decodeList<MachineTypeModel>()

        if (centerId != null) {
            allTypes.mapNotNull { type ->
                val filteredInstances = type.instances?.filter { it.centerId == centerId }
                if (!filteredInstances.isNullOrEmpty()) {
                    type.copy(instances = filteredInstances)
                } else {
                    null
                }
            }
        } else {
            allTypes
        }
    }
}

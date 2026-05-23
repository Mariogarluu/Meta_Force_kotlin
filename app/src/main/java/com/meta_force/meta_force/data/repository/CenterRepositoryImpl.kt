package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.Center
import com.meta_force.meta_force.data.model.CreateCenterInput
import com.meta_force.meta_force.data.model.UpdateCenterInput
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [CenterRepository] using Supabase PostgREST.
 */
@Singleton
class CenterRepositoryImpl @Inject constructor() : CenterRepository {
    private val supabase = SupabaseProvider.client

    override suspend fun getCenters(): Result<List<Center>> = runCatching {
        supabase.postgrest["Center"].select().decodeList<Center>()
    }

    override suspend fun getCenter(id: String): Result<Center> = runCatching {
        supabase.postgrest["Center"].select {
            filter { eq("id", id) }
        }.decodeSingle<Center>()
    }

    override suspend fun createCenter(input: CreateCenterInput): Result<Center> = runCatching {
        supabase.postgrest["Center"].insert(input).decodeSingle<Center>()
    }

    override suspend fun updateCenter(id: String, input: UpdateCenterInput): Result<Center> = runCatching {
        supabase.postgrest["Center"].update(input) {
            filter { eq("id", id) }
        }.decodeSingle<Center>()
    }

    override suspend fun deleteCenter(id: String): Result<Unit> = runCatching {
        supabase.postgrest["Center"].delete {
            filter { eq("id", id) }
        }
    }
}

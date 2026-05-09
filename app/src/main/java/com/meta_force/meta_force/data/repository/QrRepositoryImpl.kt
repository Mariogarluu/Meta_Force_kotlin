package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.SignedQr
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import com.meta_force.meta_force.data.supabase.SupabaseProvider
import io.github.jan.supabase.functions.functions
import javax.inject.Inject

/**
 * Implementación de [QrRepository] que invoca la Edge Function `qr-sign`
 * usando el cliente oficial de Supabase para Kotlin.
 */
class QrRepositoryImpl @Inject constructor(
) : QrRepository {

    private val supabase = SupabaseProvider.client

    override suspend fun getSignedQr(): NetworkResult<SignedQr> {
        return safeApiCall {
            val response = supabase.functions.invoke("qr-sign")
            response.body<SignedQr>()
        }
    }
}


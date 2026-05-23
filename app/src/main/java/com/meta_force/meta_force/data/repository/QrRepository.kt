package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.SignedQr
import com.meta_force.meta_force.data.network.NetworkResult

/**
 * Repositorio para obtener el QR firmado de acceso desde Supabase.
 */
interface QrRepository {

    /**
     * Llama a la Edge Function `qr-sign` y devuelve el JWT firmado junto
     * con la información de la suscripción activa.
     */
    suspend fun getSignedQr(): NetworkResult<SignedQr>
}


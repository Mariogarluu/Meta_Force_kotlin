package com.meta_force.meta_force.data.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Minimal API para comprobar si el usuario tiene acceso activo
 * según la lógica de suscripciones en Supabase.
 *
 * Implementa una llamada RPC a `public.has_active_access()`.
 */
interface AccessApi {

    /**
     * Llama a la función RPC `has_active_access` en Supabase.
     *
     * La función no recibe parámetros, pero Supabase requiere
     * un cuerpo JSON, por lo que se envía `{}` por defecto.
     */
    @Headers("Content-Type: application/json")
    @POST("rest/v1/rpc/has_active_access")
    suspend fun hasActiveAccess(
        @Body body: Map<String, @JvmSuppressWildcards Any?> = emptyMap()
    ): Boolean
}


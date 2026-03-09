package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token de forma síncrona para la petición de red [cite: 123]
        val token = runBlocking {
            sessionManager.authToken.firstOrNull()
        }

        // Construimos la nueva petición añadiendo el header Authorization [cite: 124]
        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it") // Formato estándar Bearer [cite: 124]
            }
        }.build()

        return chain.proceed(request)
    }
}
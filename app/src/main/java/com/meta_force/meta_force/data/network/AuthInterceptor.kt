package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp Interceptor that automatically adds the "Authorization: Bearer <token>" header to every request.
 *
 * @property sessionManager Instance used to retrieve the current authentication token.
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    /**
     * Intercepts the network call and injects the authorization header if a token is available.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token de forma síncrona para la petición de red
        val token = runBlocking {
            sessionManager.authToken.firstOrNull()
        }

        // Construimos la nueva petición añadiendo el header Authorization
        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        return chain.proceed(request)
    }
}
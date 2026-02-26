package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Here we handle the 401 Unauthorized globally.
        // Since there is no refresh token endpoint, we simply clear the session
        // and return null. Returning null tells OkHttp not to retry the request.

        runBlocking {
            sessionManager.clearAuthToken()
        }
        
        return null
    }
}

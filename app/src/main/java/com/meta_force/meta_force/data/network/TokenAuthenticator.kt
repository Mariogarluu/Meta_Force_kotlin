package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * OkHttp Authenticator used to handle authentication failures (HTTP 401).
 * Currently, it clears the local session when a 401 is received.
 *
 * @property sessionManager Instance used to manage local session data.
 */
class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager
) : Authenticator {

    /**
     * Handles the authentication failure response.
     * Clears the current session to force a re-login.
     *
     * @return Always returns null as no retry logic with a refresh token is currently implemented.
     */
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

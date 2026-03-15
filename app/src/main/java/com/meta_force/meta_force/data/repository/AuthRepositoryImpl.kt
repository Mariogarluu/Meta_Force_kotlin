package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.local.SessionManager
import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.model.UserProfile
import com.meta_force.meta_force.data.model.UpdateProfileRequest
import com.meta_force.meta_force.data.network.NetworkResult
import com.meta_force.meta_force.data.network.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] that interacts with [AuthApi] and [SessionManager].
 *
 * @property api The authentication API interface.
 * @property sessionManager Instance for local session/token persistence.
 */
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall {
            val response = api.login(request)
            sessionManager.saveAuthToken(response.token)
            response
        }
    }

    override suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse> {
        return safeApiCall {
            val response = api.register(request)
            sessionManager.saveAuthToken(response.token)
            response
        }
    }

    override suspend fun logout() {
        sessionManager.clearAuthToken()
    }

    override fun getAuthToken(): Flow<String?> {
        return sessionManager.authToken
    }

    override suspend fun getProfile(): NetworkResult<UserProfile> {
        return safeApiCall {
            api.getProfile()
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UserProfile> {
        return safeApiCall {
            api.updateProfile(request)
        }
    }

    override suspend fun uploadAvatar(file: java.io.File): NetworkResult<UserProfile> {
        return safeApiCall {
            val extension = file.extension
            val mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = okhttp3.MultipartBody.Part.createFormData("image", file.name, requestFile)
            api.uploadAvatar(body)
        }
    }
}
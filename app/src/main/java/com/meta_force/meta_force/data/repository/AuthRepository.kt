package com.meta_force.meta_force.data.repository

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import com.meta_force.meta_force.data.model.User
import com.meta_force.meta_force.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication and user profile management.
 */
interface AuthRepository {
    /**
     * Authenticates a user and saves the session token.
     * @return [NetworkResult] with [LoginResponse].
     */
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>

    /**
     * Registers a new user and saves the session token.
     * @return [NetworkResult] with [RegisterResponse].
     */
    suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse>

    /**
     * Clears the user session.
     */
    suspend fun logout()

    /**
     * Retrieves the current auth token as a flow.
     */
    fun getAuthToken(): Flow<String?>

    /**
     * Fetches the current user's profile information.
     */
    suspend fun getProfile(): NetworkResult<com.meta_force.meta_force.data.model.UserProfile>

    /**
     * Updates the user's profile information.
     */
    suspend fun updateProfile(request: com.meta_force.meta_force.data.model.UpdateProfileRequest): NetworkResult<com.meta_force.meta_force.data.model.UserProfile>

    /**
     * Uploads a new profile image.
     */
    suspend fun uploadAvatar(file: java.io.File): NetworkResult<com.meta_force.meta_force.data.model.UserProfile>
}

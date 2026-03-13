package com.meta_force.meta_force.data.network

import com.meta_force.meta_force.data.model.LoginRequest
import com.meta_force.meta_force.data.model.LoginResponse
import com.meta_force.meta_force.data.model.RegisterRequest
import com.meta_force.meta_force.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import com.meta_force.meta_force.data.model.User

/**
 * Retrofit interface for authentication and user profile API endpoints.
 */
interface AuthApi {
    /**
     * Authenticates a user with email and password.
     *
     * @param request The [LoginRequest] credentials.
     * @return A [LoginResponse] containing the auth token and user info.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Registers a new user in the system.
     *
     * @param request The [RegisterRequest] with registration details.
     * @return A [RegisterResponse] containing the new auth token and user info.
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    /**
     * Fetches the current authenticated user's profile information.
     *
     * @return The [User] object representing the current user.
     */
    @GET("users/me")
    suspend fun getProfile(): User

    /**
     * Updates the current user's profile details.
     *
     * @param user The [User] object with updated fields.
     * @return The updated [User] information.
     */
    @PUT("users/me")
    suspend fun updateProfile(@Body user: User): User

    /**
     * Uploads a new profile image for the current user.
     *
     * @param avatar The image file part in a multipart request.
     * @return The updated [User] information including the new image URL.
     */
    @Multipart
    @POST("users/me/profile-image")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): User
}

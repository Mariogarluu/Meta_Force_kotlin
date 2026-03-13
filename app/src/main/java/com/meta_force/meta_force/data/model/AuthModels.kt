package com.meta_force.meta_force.data.model

/**
 * Request object for user login.
 *
 * @property email The user's email address.
 * @property password The user's account password.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Response received after a successful login.
 *
 * @property token The authentication token to be used for future requests.
 * @property user Information about the authenticated [User].
 */
data class LoginResponse(
    val token: String,
    val user: User
)

/**
 * Request object for new user registration.
 *
 * @property name The full name of the user.
 * @property email The email address for the new account.
 * @property password The password for the new account.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Response received after a successful registration.
 *
 * @property token The authentication token for the new user session.
 * @property user Information about the newly created [User].
 */
data class RegisterResponse(
    val token: String,
    val user: User
)

/**
 * Represents a user in the system.
 *
 * @property id Unique identifier for the user.
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property role The role assigned to the user (e.g., "USER", "ADMIN").
 * @property profileImageUrl Optional URL for the user's profile picture.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val profileImageUrl: String? = null
)

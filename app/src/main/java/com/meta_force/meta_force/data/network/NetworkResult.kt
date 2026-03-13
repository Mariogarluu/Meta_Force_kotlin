package com.meta_force.meta_force.data.network

/**
 * A sealed class representing the result of a network request.
 *
 * @param T The type of the data expected in case of success.
 */
sealed class NetworkResult<out T> {
    /**
     * Represents a successful network response.
     *
     * @property data The parsed response data.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * Represents a failure response with an HTTP error code.
     *
     * @property code The HTTP status code (e.g., 404, 500).
     * @property message The error message, usually from the server.
     */
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()

    /**
     * Represents a failure due to an exception (e.g., [java.io.IOException]).
     *
     * @property e The caught exception.
     */
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}

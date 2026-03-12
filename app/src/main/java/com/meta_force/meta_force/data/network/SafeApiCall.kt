package com.meta_force.meta_force.data.network

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * Utility function to perform a network call safely, catching exceptions and mapping errors.
 *
 * @param T The expected return type of the API call.
 * @param apiCall A lambda representing the suspendable network operation.
 * @return A [NetworkResult] encapsulating the Success, Error, or Exception state.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: HttpException) {
        val code = e.code()
        val errorResponse = e.response()?.errorBody()?.string()
        var message = e.message() ?: "Unknown error"

        try {
            if (errorResponse != null) {
                val jsonObject = JSONObject(errorResponse)
                message = jsonObject.getString("message")
            }
        } catch (ex: Exception) {
            // If it's not JSON, fallback to the raw string or standard message
        }
        
        NetworkResult.Error(code, message)
    } catch (e: IOException) {
        NetworkResult.Exception(e) // Network errors like No Internet connection
    } catch (e: Exception) {
        NetworkResult.Exception(e) // Other unexpected exceptions
    }
}

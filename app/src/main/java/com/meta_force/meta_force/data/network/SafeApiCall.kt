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
        var message = e.message()
        if (message.isNullOrBlank()) {
            message = "Error HTTP $code"
        }

        try {
            if (!errorResponse.isNullOrBlank()) {
                val jsonObject = JSONObject(errorResponse)
                // Deno / Supabase functions might return "error" or "message" key
                val msg = jsonObject.optString("message", jsonObject.optString("error", ""))
                if (msg.isNotBlank()) {
                    message = msg
                } else if (errorResponse.length < 150) {
                    message = errorResponse
                }
            }
        } catch (ex: Exception) {
            // If it's not JSON, fallback to raw response if it is a short, readable message
            if (!errorResponse.isNullOrBlank() && errorResponse.length < 150) {
                message = errorResponse
            }
        }
        
        NetworkResult.Error(code, message)
    } catch (e: IOException) {
        NetworkResult.Exception(e) // Network errors like No Internet connection
    } catch (e: Exception) {
        NetworkResult.Exception(e) // Other unexpected exceptions
    }
}

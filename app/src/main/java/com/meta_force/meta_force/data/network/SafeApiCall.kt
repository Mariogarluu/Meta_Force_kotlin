package com.meta_force.meta_force.data.network

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

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
            // If it's not JSON, fallback to the raw raw string or standard message
        }
        
        NetworkResult.Error(code, message)
    } catch (e: IOException) {
        NetworkResult.Exception(e) // Network errors like No Internet connection
    } catch (e: Exception) {
        NetworkResult.Exception(e) // Other unexpected exceptions
    }
}

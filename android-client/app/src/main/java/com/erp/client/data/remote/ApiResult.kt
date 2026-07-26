package com.erp.client.data.remote

import com.erp.client.data.remote.dto.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}

private val gson = Gson()

/**
 * Converts a Retrofit [Response] into an [ApiResult], decoding the backend's
 * GlobalExceptionHandler JSON error body (message field) when the call fails.
 */
fun <T> Response<T>.toApiResult(): ApiResult<T> {
    if (isSuccessful) {
        val body = body()
        return if (body != null) {
            ApiResult.Success(body)
        } else {
            ApiResult.Failure("Empty response from server")
        }
    }

    val rawError = errorBody()?.string()
    val message = rawError?.let {
        runCatching { gson.fromJson(it, ErrorResponse::class.java).message }.getOrNull()
    } ?: "Request failed (HTTP ${code()})"

    return ApiResult.Failure(message)
}

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        call().toApiResult()
    } catch (e: java.io.IOException) {
        ApiResult.Failure("Unable to reach the server. Check your connection.")
    } catch (e: Exception) {
        ApiResult.Failure(e.message ?: "An unexpected error occurred")
    }
}

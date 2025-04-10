package com.alex.androidplayground.core.utils.api

import com.alex.androidplayground.core.model.result.Result
import com.alex.androidplayground.core.model.result.Result.*
import com.alex.androidplayground.core.model.result.error.DataError
import retrofit2.Response
import java.io.IOException

/**
 * Api call wrapper that handles exceptions and returns a [Result].
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T, DataError.Network> {
    return try {
        apiCall().toResult()
    } catch (e: IOException) {
        Error(DataError.Network.NETWORK_ERROR)
    } catch (e: Exception) {
        Error(DataError.Network.UNKNOWN)
    }
}

private fun <T> Response<T>.toResult(): Result<T, DataError.Network> {
    return if (isSuccessful) {
        body()?.let { Success(it) } ?: Error(DataError.Network.EMPTY_BODY)
    } else {
        Error(mapNetworkError(code()))
    }
}

private fun mapNetworkError(code: Int): DataError.Network = when (code) {
    400 -> DataError.Network.BAD_REQUEST            // Client sent an invalid request
    401 -> DataError.Network.UNAUTHORIZED           // Authentication required
    403 -> DataError.Network.FORBIDDEN              // Client is authenticated but not allowed
    404 -> DataError.Network.NOT_FOUND              // Resource not found
    405 -> DataError.Network.METHOD_NOT_ALLOWED     // HTTP method not allowed
    408 -> DataError.Network.REQUEST_TIMEOUT        // Request timed out
    409 -> DataError.Network.CONFLICT               // Conflict with the current state of the resource
    410 -> DataError.Network.GONE                   // Resource is no longer available
    411 -> DataError.Network.LENGTH_REQUIRED        // Content-Length header is required
    413 -> DataError.Network.PAYLOAD_TOO_LARGE      // Payload is too large
    414 -> DataError.Network.URI_TOO_LONG           // URI is too long
    415 -> DataError.Network.UNSUPPORTED_MEDIA_TYPE // Media type is unsupported
    422 -> DataError.Network.UNPROCESSABLE_ENTITY   // Validation error (common in APIs)
    426 -> DataError.Network.UPGRADE_REQUIRED       // Client needs to upgrade
    429 -> DataError.Network.TOO_MANY_REQUESTS      // Rate limit exceeded
    500 -> DataError.Network.INTERNAL_SERVER_ERROR  // Generic server error
    501 -> DataError.Network.NOT_IMPLEMENTED        // Endpoint not implemented
    502 -> DataError.Network.BAD_GATEWAY            // Gateway received an invalid response
    503 -> DataError.Network.SERVICE_UNAVAILABLE    // Server is temporarily unavailable
    504 -> DataError.Network.GATEWAY_TIMEOUT        // Gateway timed out
    else -> DataError.Network.UNKNOWN
}
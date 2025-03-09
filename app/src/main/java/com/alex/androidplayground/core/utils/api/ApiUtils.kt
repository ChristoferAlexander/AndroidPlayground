package com.alex.androidplayground.core.utils.api

import com.alex.androidplayground.core.utils.result.error.DataError
import com.alex.androidplayground.core.utils.result.Result
import retrofit2.Response

fun <T> Response<T>.toResult(): Result<T, DataError.Network> {
    return if (isSuccessful) {
        body()?.let { Result.Success(it) } ?: Result.Error(DataError.Network.EMPTY_BODY)
    } else {
        Result.Error(mapNetworkError(code()))
    }
}

fun mapNetworkError(code: Int): DataError.Network = when (code) {
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
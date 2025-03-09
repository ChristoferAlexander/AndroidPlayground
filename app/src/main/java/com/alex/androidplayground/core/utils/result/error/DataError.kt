package com.alex.androidplayground.core.utils.result.error

sealed interface DataError: Error {
    enum class Network : DataError {
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        METHOD_NOT_ALLOWED,
        REQUEST_TIMEOUT,
        CONFLICT,
        GONE,
        LENGTH_REQUIRED,
        PAYLOAD_TOO_LARGE,
        URI_TOO_LONG,
        UNSUPPORTED_MEDIA_TYPE,
        UNPROCESSABLE_ENTITY,
        UPGRADE_REQUIRED,
        TOO_MANY_REQUESTS,
        INTERNAL_SERVER_ERROR,
        NOT_IMPLEMENTED,
        BAD_GATEWAY,
        SERVICE_UNAVAILABLE,
        GATEWAY_TIMEOUT,
        EMPTY_BODY,
        NO_INTERNET,
        SERIALIZATION,
        AUTH_ERROR,
        UNKNOWN
    }
    enum class Local: DataError {
        DISK_FULL
    }
}
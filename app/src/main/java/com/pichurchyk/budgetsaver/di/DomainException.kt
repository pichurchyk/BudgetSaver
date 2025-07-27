package com.pichurchyk.budgetsaver.di

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
//    val code: String?,
    val message: String?
)

sealed class DomainException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    data class NetworkConnectionException(override val cause: Throwable? = null) :
        DomainException("No internet connection", cause)

    data class UnauthorizedException(override val cause: Throwable? = null) :
        DomainException("Unauthorized", cause)

    data class BadRequestException(
        val apiErrorMessage: String? = null,
        override val cause: Throwable? = null
    ) : DomainException(apiErrorMessage, cause)

    data class ServerErrorException(
        val apiErrorMessage: String? = null,
        override val cause: Throwable? = null
    ) : DomainException(apiErrorMessage, cause)

    data class ApiSpecificException(
        val errorCode: String,
        val errorMessage: String,
        override val cause: Throwable? = null
    ) : DomainException(errorMessage, cause)

    data class UnknownApiException(override val cause: Throwable? = null) :
        DomainException("An unknown API error occurred", cause)

    data class SerializationException(override val cause: Throwable? = null) :
        DomainException("Data format error", cause)
}

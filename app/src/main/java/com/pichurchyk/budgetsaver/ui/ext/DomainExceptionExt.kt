package com.pichurchyk.budgetsaver.ui.ext

import androidx.annotation.StringRes
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.di.DomainException

@StringRes
fun DomainException.asErrorMessage(): Int {
    return when (this) {
        is DomainException.NetworkConnectionException -> R.string.error_network_issue
        is DomainException.UnauthorizedException -> R.string.error_unauthorized
        is DomainException.BadRequestException -> R.string.error_bad_request
        is DomainException.ServerErrorException -> R.string.error_server_issue
        is DomainException.ApiSpecificException -> R.string.error_server_issue
        is DomainException.SerializationException -> R.string.error_data_format
        is DomainException.UnknownApiException -> R.string.error_occurred
    }
}
package com.pichurchyk.budgetsaver.data.model.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenPayload(
    @SerialName("refresh_token")
    val refreshToken: String
)
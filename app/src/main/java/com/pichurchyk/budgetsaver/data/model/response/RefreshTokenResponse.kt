package com.pichurchyk.budgetsaver.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("access_token")
    val accessToken: String
)
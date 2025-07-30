package com.pichurchyk.budgetsaver.data.model.response.user

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesResponse(
    val favoriteCurrencies: List<String>
)
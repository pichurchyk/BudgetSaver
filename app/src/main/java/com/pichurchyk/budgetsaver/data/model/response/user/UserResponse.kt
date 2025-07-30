package com.pichurchyk.budgetsaver.data.model.response.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val preferences: UserPreferencesResponse
)
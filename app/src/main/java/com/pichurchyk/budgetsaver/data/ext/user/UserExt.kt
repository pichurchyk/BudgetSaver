package com.pichurchyk.budgetsaver.data.ext.user

import com.pichurchyk.budgetsaver.data.model.response.user.UserResponse
import com.pichurchyk.budgetsaver.domain.model.user.User

fun UserResponse.toUser() = User(
    id = this.id,
    name = this.name,
    avatarUrl = this.avatarUrl,
    email = this.email,
    preferences = this.preferences.toUserPreferences()
)

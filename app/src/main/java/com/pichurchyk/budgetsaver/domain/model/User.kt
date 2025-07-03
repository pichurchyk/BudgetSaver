package com.pichurchyk.budgetsaver.domain.model

data class User(
    val id: String,
    val name: String? = null,
    val avatarUrl: String? = null,
    val email: String? = null
)
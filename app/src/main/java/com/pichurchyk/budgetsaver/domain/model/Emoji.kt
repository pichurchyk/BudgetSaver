package com.pichurchyk.budgetsaver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val emoji: String,
    val description: String,
    val category: String,
    val aliases: List<String>,
    val tags: List<String>,
)

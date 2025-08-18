package com.pichurchyk.budgetsaver.data.model.payload

import kotlinx.serialization.Serializable

@Serializable
data class TransactionCategoryPayload(
    val color: String,
    val title: String,
    val emoji: String
)
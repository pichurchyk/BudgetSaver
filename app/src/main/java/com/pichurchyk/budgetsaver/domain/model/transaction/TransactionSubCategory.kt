package com.pichurchyk.budgetsaver.domain.model.transaction

import kotlinx.serialization.Serializable

@Serializable
data class TransactionSubCategory(
    val title: String,
    val color: String,
)
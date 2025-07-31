package com.pichurchyk.budgetsaver.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class TransactionCategory(
    val uuid: String,
    val title: String,
    val emoji: String,
    val color: String? = null,
) {
    val asPrettyText: String
        get() = "$emoji   $title"
}
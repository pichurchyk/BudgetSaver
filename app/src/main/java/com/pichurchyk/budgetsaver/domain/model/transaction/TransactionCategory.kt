package com.pichurchyk.budgetsaver.domain.model.transaction

data class TransactionCategory(
    val uuid: String,
    val title: String,
    val emoji: String,
    val color: String? = null,
) {
    val asPrettyText: String
        get() = "$emoji   $title"
}
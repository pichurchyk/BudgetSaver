package com.pichurchyk.budgetsaver.domain.model.transaction

data class Transaction(
    val uuid: String,

    val title: String,

    val value: Money,
    val notes: String,
    val date: TransactionDate,

    val mainCategory: TransactionCategory,
    val subCategory: List<TransactionSubCategory> = emptyList()
)
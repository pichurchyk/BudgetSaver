package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val uuid: String,

    val title: String?,

    val value: Money,
    val notes: String,
    val date: TransactionDate,

    val mainCategory: TransactionCategory?,
    val subCategory: List<TransactionSubCategory> = emptyList()
)
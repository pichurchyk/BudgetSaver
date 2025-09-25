package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import kotlinx.serialization.Serializable

@Serializable
data class TransactionPreset(
    val uuid: String,
    val title: String?,

    val value: Money,
    val notes: String,

    val mainCategory: TransactionCategory?,
    val subCategory: List<TransactionSubCategory> = emptyList()
)
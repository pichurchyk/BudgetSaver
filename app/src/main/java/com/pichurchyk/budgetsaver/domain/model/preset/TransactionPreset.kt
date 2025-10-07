package com.pichurchyk.budgetsaver.domain.model.preset

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
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
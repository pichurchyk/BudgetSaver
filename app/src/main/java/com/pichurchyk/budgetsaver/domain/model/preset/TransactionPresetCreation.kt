package com.pichurchyk.budgetsaver.domain.model.preset

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import java.util.Currency

data class TransactionPresetCreation(
    val title: String? = null,

    val value: String = "",
    val currency: Currency = Currency.getInstance("USD"),

    val notes: String = "",

    val type: TransactionType = TransactionType.EXPENSES,

    val mainCategory: TransactionCategory? = null,
    val subCategory: List<TransactionSubCategory> = emptyList()
)
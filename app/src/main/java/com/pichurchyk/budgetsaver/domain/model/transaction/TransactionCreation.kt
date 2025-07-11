package com.pichurchyk.budgetsaver.domain.model.transaction

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import java.util.Currency

data class TransactionCreation(
    val title: String = "",

    val value: String = "",
    val currency: Currency = Currency.getInstance("USD"),

    val notes: String = "",
    val date: TransactionDate = TransactionDate(
        dateInstant = Clock.System.now(),
        timeZone = TimeZone.currentSystemDefault()
    ),

    val type: TransactionType = TransactionType.EXPENSES,

    val mainCategory: TransactionCategory? = null,
    val subCategory: List<TransactionSubCategory> = emptyList()
)
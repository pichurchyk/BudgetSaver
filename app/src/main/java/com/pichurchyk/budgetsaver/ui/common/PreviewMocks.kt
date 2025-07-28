package com.pichurchyk.budgetsaver.ui.common

import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.TransactionsWithFilters
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.math.BigInteger

object PreviewMocks {

    val category = TransactionCategory("Food", "🍔", "#FF7043")

    val categories = listOf(
        TransactionCategory("Food", "🍔", "#FF7043"),
        TransactionCategory("Transport", "🚌", "#42A5F5"),
        TransactionCategory("Health", "❤️", "#EC407A"),
        TransactionCategory("Gifts", "🎁", "#66BB6A"),
        TransactionCategory("Entertainment", "🎮", "#AB47BC"),
    )

    val transactionSuCategory = TransactionSubCategory(
        title = "Groceries",
        color = "#FF00F0"
    )

    val transactionDate = TransactionDate(
        dateInstant = Instant.fromEpochMilliseconds(
            1748198228000
        ),
        timeZone = TimeZone.UTC
    )

    val transaction = Transaction(
        uuid = "",
        title = "Bus ticket",
        value = Money(
            amountMinor = BigInteger("0"),
            currency = "BYN"
        ),
        notes = "Grocery shopping at local market",
        date = PreviewMocks.transactionDate,
        mainCategory = PreviewMocks.category,
        subCategory = listOf(
            PreviewMocks.transactionSuCategory
        )
    )

    val transactionByCurrency = TransactionsByCurrency(
        transactions = listOf(PreviewMocks.transaction),
        currencyCode = "BYN"
    )

    val transactionWithFilters = TransactionsWithFilters(
        transactions = PreviewMocks.transactionByCurrency,
        selectedCategories = listOf(),
        selectedTransactionType = listOf()
    )

    val money = Money(
        BigInteger("132123123"),
        "USD"
    )
}
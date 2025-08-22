package com.pichurchyk.budgetsaver.ui.common

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.model.user.User
import com.pichurchyk.budgetsaver.domain.model.user.UserPreferences
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.math.BigInteger
import java.util.Currency

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
        date = transactionDate,
        mainCategory = category,
        subCategory = listOf(
            transactionSuCategory
        )
    )

    val transactionByCurrency = TransactionsByCurrency(
        transactions = listOf(transaction),
        currencyCode = "BYN",
        selectedCategories = listOf(category),
        selectedTransactionType = listOf(TransactionType.EXPENSES)
    )

    val money = Money(
        amountMinor = BigInteger("132123123"),
         currency = "USD"
    )

    val userPreferences = UserPreferences(
        favoriteCurrencies = listOf(Currency.getInstance("USD"))
    )

    val user = User(
        id = "0",
        name = "Uladzislau Pichurchyk",
        avatarUrl = "",
        email = "pichurchyk@gmail.com",
        preferences = userPreferences
    )
}
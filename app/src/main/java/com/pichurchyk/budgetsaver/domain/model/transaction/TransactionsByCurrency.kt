package com.pichurchyk.budgetsaver.domain.model.transaction

import java.util.Currency

data class TransactionsByCurrency(
    val transactions: List<Transaction>,
    val currency: Currency
) {
    val allCategories: List<TransactionCategory>
        get() = transactions.map { it.mainCategory }.distinct()

    val totalIncomes: Money
        get() = Money(transactions.filter { it.value.amountMinor > 0 }
            .sumOf { it.value.amountMinor }, currency)

    val totalExpenses: Money
        get() = Money(transactions.filter { it.value.amountMinor < 0 }
            .sumOf { it.value.amountMinor }, currency)
}
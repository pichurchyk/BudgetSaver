package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import java.math.BigInteger

data class TransactionsByCurrency(
    val transactions: List<Transaction>,
    val currencyCode: String
) {
    val allCategories: List<TransactionCategory?>
        get() = transactions.map { it.mainCategory }.distinct()

    val totalIncomes: Money
        get() = Money(transactions.filter { it.value.amountMinor > BigInteger("0") }
            .sumOf { it.value.amountMinor }, currencyCode)

    val totalExpenses: Money
        get() = Money(transactions.filter { it.value.amountMinor < BigInteger("0") }
            .sumOf { it.value.amountMinor }, currencyCode)
}
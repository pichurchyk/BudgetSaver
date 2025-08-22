package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import java.math.BigInteger

data class TransactionsByCurrency(
    val transactions: List<Transaction>,
    val currencyCode: String,
    val selectedCategories: List<TransactionCategory?>,
    val selectedTransactionType: List<TransactionType>
) {
    val allCategories: List<TransactionCategory?>
        get() = transactions.map { it.mainCategory }.distinct()

    val totalIncomes: Money
        get() = Money(transactions.filter { it.value.amountMinor > BigInteger("0") }
            .sumOf { it.value.amountMinor }, currencyCode)

    val totalExpenses: Money
        get() = Money(transactions.filter { it.value.amountMinor < BigInteger("0") }
            .sumOf { it.value.amountMinor }, currencyCode)

    val filteredTransactionsWithCurrency: List<Transaction>
        get() {
            val filtered = transactions
                .filter { it.mainCategory in selectedCategories }
                .filter { tx ->
                    when {
                        selectedTransactionType.containsAll(TransactionType.entries) -> true
                        tx.value.amountMinor >= BigInteger("0") -> TransactionType.INCOMES in selectedTransactionType
                        else -> TransactionType.EXPENSES in selectedTransactionType
                    }
                }

            return filtered
        }
}
package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import java.math.BigInteger

data class TransactionsByCurrency(
    val transactions: List<Transaction>,
    val currencyCode: String,
    val selectedCategories: List<TransactionCategory?>,
    val selectedTransactionType: List<TransactionType>,
    val allCategories: List<TransactionCategory?>,
    val totalIncomes: Money,
    val totalExpenses: Money,
    val filteredTransactions: List<Transaction>
) {
    companion object {
        fun create(
            transactions: List<Transaction>,
            currencyCode: String,
            selectedCategories: List<TransactionCategory?> = transactions.map { it.mainCategory }.distinct(),
            selectedTransactionType: List<TransactionType> = TransactionType.entries
        ): TransactionsByCurrency {
            val allCategories = transactions.map { it.mainCategory }.distinct()

            val totalIncomes = Money(
                transactions.filter { it.value.amountMinor > BigInteger("0") }
                    .sumOf { it.value.amountMinor },
                currencyCode
            )

            val totalExpenses = Money(
                transactions.filter { it.value.amountMinor < BigInteger("0") }
                    .sumOf { it.value.amountMinor },
                currencyCode
            )

            val filteredTransactions = transactions
                .filter { it.mainCategory in selectedCategories }
                .filter { tx ->
                    when {
                        selectedTransactionType.containsAll(TransactionType.entries) -> true
                        tx.value.amountMinor >= BigInteger("0") -> TransactionType.INCOMES in selectedTransactionType
                        else -> TransactionType.EXPENSES in selectedTransactionType
                    }
                }

            return TransactionsByCurrency(
                transactions = transactions,
                currencyCode = currencyCode,
                selectedCategories = selectedCategories,
                selectedTransactionType = selectedTransactionType,
                allCategories = allCategories,
                totalIncomes = totalIncomes,
                totalExpenses = totalExpenses,
                filteredTransactions = filteredTransactions
            )
        }
    }
}
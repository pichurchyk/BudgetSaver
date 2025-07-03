package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType

data class TransactionsWithFilters(
    val transactions: TransactionsByCurrency,
    val selectedCategories: List<TransactionCategory>,
    val selectedTransactionType: List<TransactionType>
) {
    val filteredTransactionsWithCurrency: TransactionsByCurrency
        get() {
            val filtered = transactions.transactions
                .filter { it.mainCategory in selectedCategories }
                .filter { tx ->
                    when {
                        selectedTransactionType.containsAll(TransactionType.entries) -> true
                        tx.value.amountMinor >= 0 -> TransactionType.INCOMES in selectedTransactionType
                        else -> TransactionType.EXPENSES in selectedTransactionType
                    }
                }

            return TransactionsByCurrency(
                transactions = filtered,
                currency = transactions.currency
            )
        }
}
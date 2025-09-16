package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

sealed class DashboardUiStatus {
    data object Idle: DashboardUiStatus()
    data class IdleDeletingTransaction(val transaction: Transaction): DashboardUiStatus()
    data object LoadingAll : DashboardUiStatus()
    data object LoadingTransactions : DashboardUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : DashboardUiStatus()
}

data class DashboardViewState(
    val status: DashboardUiStatus = DashboardUiStatus.Idle,
    val availableCurrencies: List<Currency> = emptyList(),
    val selectedCurrency: Currency? = null,
    val allTransactions: List<Transaction> = emptyList(),
    val allCategories: List<TransactionCategory?> = emptyList(),
    val selectedCategories: List<TransactionCategory?> = emptyList(),
    val selectedTransactionType: List<TransactionType> = TransactionType.entries,
    val datePeriod: Pair<TransactionDate?, TransactionDate?> = null to null,
)
package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.math.BigInteger
import java.util.Currency

sealed class DashboardUiStatus {
    data object Idle: DashboardUiStatus()
    data class IdleDeletingTransaction(val transaction: Transaction): DashboardUiStatus()
    data object LoadingCurrencies : DashboardUiStatus()
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

    val filteredTransactions: List<Transaction> = emptyList(),
    val totalIncomes: Money = Money(BigInteger.ZERO, ""),
    val totalExpenses: Money = Money(BigInteger.ZERO, ""),

    val allCategories: List<TransactionCategory?> = emptyList(),
    val selectedCategories: List<TransactionCategory?> = emptyList(),
    val selectedTransactionType: List<TransactionType> = TransactionType.entries,
    val datePeriod: Pair<TransactionDate?, TransactionDate?> = null to null,
)
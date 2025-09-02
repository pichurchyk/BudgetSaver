package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency

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
    val availableCurrencies: List<String> = emptyList(),
    val selectedCurrency: String? = null,
    val currentTransactions: TransactionsByCurrency? = null
)
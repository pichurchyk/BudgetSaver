package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency

sealed class TransactionsUiStatus {
    data object Idle : TransactionsUiStatus()
    data object Loading : TransactionsUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : TransactionsUiStatus()
}

sealed class CurrenciesUiStatus {
    data object Idle : CurrenciesUiStatus()
    data object Loading : CurrenciesUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : CurrenciesUiStatus()
}

data class DashboardViewState(
    val transactionsStatus: TransactionsUiStatus = TransactionsUiStatus.Idle,
    val currenciesStatus: CurrenciesUiStatus = CurrenciesUiStatus.Idle,
    val availableCurrencies: List<String> = emptyList(),
    val selectedCurrency: String? = null,
    val transactions: List<TransactionsByCurrency>? = null
)
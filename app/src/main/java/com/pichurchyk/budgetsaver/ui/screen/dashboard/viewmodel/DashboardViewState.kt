package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import java.util.Currency

sealed interface DashboardViewState {
    data object Init : DashboardViewState

    data object Loading : DashboardViewState

    data class Loaded(
        val allTransactions: List<TransactionsWithFilters>,
        val selectedCurrency: Currency? = allTransactions.firstOrNull()?.transactions?.currency
    ) : DashboardViewState {
        val allCurrencies: List<Currency>
            get() = allTransactions.map { it.transactions.currency }

        val indexOfSelectedCurrency: Int
            get() = allCurrencies.indexOf(selectedCurrency)

        val sortedTransactionsBySelectedCurrency: TransactionsWithFilters?
            get() = allTransactions.find { it.transactions.currency == selectedCurrency }
    }

    data class Error(val message: String?) : DashboardViewState
}
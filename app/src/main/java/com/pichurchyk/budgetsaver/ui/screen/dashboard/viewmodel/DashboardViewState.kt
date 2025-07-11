package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

sealed interface DashboardViewState {
    data object Init : DashboardViewState

    data object Loading : DashboardViewState

    data class Loaded(
        val allTransactions: List<TransactionsWithFilters>,
        val selectedCurrency: String? = allTransactions.firstOrNull()?.transactions?.currencyCode
    ) : DashboardViewState {
        val allCurrencies: List<String>
            get() = allTransactions.map { it.transactions.currencyCode }

        val indexOfSelectedCurrency: Int
            get() = allCurrencies.indexOf(selectedCurrency)

        val sortedTransactionsBySelectedCurrency: TransactionsWithFilters?
            get() = allTransactions.find { it.transactions.currencyCode == selectedCurrency }
    }

    data class Error(val message: String?) : DashboardViewState
}
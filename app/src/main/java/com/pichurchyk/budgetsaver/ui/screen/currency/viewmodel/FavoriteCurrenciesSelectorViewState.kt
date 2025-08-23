package com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import java.util.Currency

data class FavoriteCurrenciesSelectorViewState(
    val allCurrencies: List<Currency> = emptyList(),
    val selectedCurrencies: List<Currency> = emptyList(),
    val searchValue: String = "",
    val status: FavoriteCurrenciesSelectorUiStatus = FavoriteCurrenciesSelectorUiStatus.Loading
) {
    val filteredCurrencies: List<Currency>
        get() = allCurrencies.filter { it.currencyCode.lowercase().contains(searchValue.lowercase()) }
}

sealed class FavoriteCurrenciesSelectorUiStatus {
    data object Loading: FavoriteCurrenciesSelectorUiStatus()
    data object Idle: FavoriteCurrenciesSelectorUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ): FavoriteCurrenciesSelectorUiStatus()
}
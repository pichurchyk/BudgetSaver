package com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import java.util.Currency

data class FavoriteCurrenciesSelectorViewState(
    val allCurrencies: List<Currency> = emptyList(),
    val selectedCurrencies: List<Currency> = emptyList(),
    val status: FavoriteCurrenciesSelectorUiStatus = FavoriteCurrenciesSelectorUiStatus.Loading
)

sealed class FavoriteCurrenciesSelectorUiStatus {
    data object Loading: FavoriteCurrenciesSelectorUiStatus()
    data object Idle: FavoriteCurrenciesSelectorUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ): FavoriteCurrenciesSelectorUiStatus()
}
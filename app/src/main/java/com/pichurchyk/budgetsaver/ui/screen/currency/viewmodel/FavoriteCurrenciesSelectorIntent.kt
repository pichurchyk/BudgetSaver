package com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel

import java.util.Currency

sealed class FavoriteCurrenciesSelectorIntent {
    data class SelectCurrency(val currency: Currency): FavoriteCurrenciesSelectorIntent()
    data class UnselectCurrency(val currency: Currency): FavoriteCurrenciesSelectorIntent()
}
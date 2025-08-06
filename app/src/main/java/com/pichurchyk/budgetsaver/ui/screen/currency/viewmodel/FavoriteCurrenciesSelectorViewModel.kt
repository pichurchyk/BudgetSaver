package com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.domain.usecase.AddFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.DeleteFavoriteCurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency

class FavoriteCurrenciesSelectorViewModel(
    private val sessionManager: SessionManager,
    private val addFavoriteCurrencyUseCase: AddFavoriteCurrencyUseCase,
    private val deleteFavoriteCurrencyUseCase: DeleteFavoriteCurrencyUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        FavoriteCurrenciesSelectorViewState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.user.collect { user ->
                user?.let {
                    _viewState.update { currentState ->
                        currentState.copy(
                            selectedCurrencies = it.preferences.favoriteCurrencies,
                            status = FavoriteCurrenciesSelectorUiStatus.Idle
                        )
                    }
                }
            }
        }
    }

    fun handleIntent(intent: FavoriteCurrenciesSelectorIntent) {
        when (intent) {
            is FavoriteCurrenciesSelectorIntent.SelectCurrency -> {
                selectCurrency(intent.currency)
            }

            is FavoriteCurrenciesSelectorIntent.UnselectCurrency -> {
                unselectCurrency(intent.currency)
            }
        }
    }

    private fun selectCurrency(currency: Currency) {
        _viewState.update { currentState ->
            val currentCurrencies = currentState.selectedCurrencies

            currentState.copy(selectedCurrencies = currentCurrencies + currency)
        }

        viewModelScope.launch {
            addFavoriteCurrencyUseCase
                .invoke(currency)
                .collect {}
        }
    }

    private fun unselectCurrency(currency: Currency) {
        _viewState.update { currentState ->
            val currentCurrencies = currentState.selectedCurrencies

            currentState.copy(selectedCurrencies = currentCurrencies.filter { it != currency })
        }

        viewModelScope.launch {
            deleteFavoriteCurrencyUseCase
                .invoke(currency)
                .collect {}
        }
    }
}
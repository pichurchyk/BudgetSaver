package com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.usecase.currency.AddFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.currency.DeleteFavoriteCurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency

class FavoriteCurrenciesSelectorViewModel(
    private val sessionManager: SessionManager,
    private val addFavoriteCurrencyUseCase: AddFavoriteCurrencyUseCase,
    private val deleteFavoriteCurrencyUseCase: DeleteFavoriteCurrencyUseCase,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        FavoriteCurrenciesSelectorViewState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        initLoad()
    }

    private fun initLoad() {
        viewModelScope.launch {
            try {
                updateCurrencies()
            } catch (e: DomainException) {
                _viewState.update {
                    it.copy(
                        status = FavoriteCurrenciesSelectorUiStatus.Error(
                            e,
                            lastAction = { initLoad() }
                        )
                    )
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

            is FavoriteCurrenciesSelectorIntent.Search -> {
                changeSearchValue(intent.value)
            }
        }
    }

    private fun changeSearchValue(value: String) {
        _viewState.update { currentState ->
            currentState.copy(searchValue = value)
        }
    }

    private fun selectCurrency(currency: Currency) {
        viewModelScope.launch {
            addFavoriteCurrencyUseCase
                .invoke(currency)
                .catch {
                    _viewState.update { currentState ->
                        currentState.copy(
                            status = FavoriteCurrenciesSelectorUiStatus.Error(
                                error = it as DomainException,
                                lastAction = { unselectCurrency(currency) }
                            ))
                    }
                }
                .collect {
                    updateCurrencies()
                }
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
                .catch {
                    _viewState.update { currentState ->
                        currentState.copy(
                            status = FavoriteCurrenciesSelectorUiStatus.Error(
                            error = it as DomainException,
                            lastAction = { unselectCurrency(currency) }
                        ))
                    }
                }
                .collect {
                    updateCurrencies()
                }
        }
    }

    private fun updateCurrencies() {
        viewModelScope.launch {
            val allCurrencies = currencyRepository.getAllCurrencies().first()
            val user = sessionManager.user.first()

            val userFavoriteCurrencies = user?.preferences?.favoriteCurrencies ?: emptyList()

            _viewState.update {
                it.copy(
                    allCurrencies = allCurrencies,
                    selectedCurrencies = userFavoriteCurrencies,
                    status = FavoriteCurrenciesSelectorUiStatus.Idle
                )
            }
        }
    }
}
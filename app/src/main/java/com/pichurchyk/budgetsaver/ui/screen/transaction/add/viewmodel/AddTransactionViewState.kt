package com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import java.util.Currency

data class AddTransactionViewState(
    val transaction: TransactionCreation = TransactionCreation(),

    val allCurrencies: List<Currency> = Currency.getAvailableCurrencies().toList(),
    val currenciesSearch: String = "",

    val isLoading: Boolean = false,

    val validationError: List<AddTransactionValidationError> = emptyList<AddTransactionValidationError>(),

    val status: AddTransactionUiStatus = AddTransactionUiStatus.Idle
) {
    val filteredCurrencies: List<Currency>
        get() =
            allCurrencies
                .filter {
                    it.currencyCode.lowercase().contains(currenciesSearch.lowercase())
                }
                .sortedByDescending { it == transaction.currency }
}

sealed interface AddTransactionUiStatus {

    object Idle : AddTransactionUiStatus

    object Loading : AddTransactionUiStatus

    object Success : AddTransactionUiStatus

    object ValidationError: AddTransactionUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : AddTransactionUiStatus
}
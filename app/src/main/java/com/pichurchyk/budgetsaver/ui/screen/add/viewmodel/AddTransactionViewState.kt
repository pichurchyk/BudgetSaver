package com.pichurchyk.budgetsaver.ui.screen.add.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

data class AddTransactionViewState(
    val transaction: TransactionCreation = TransactionCreation(),

    val allCurrencies: List<Currency> = Currency.getAvailableCurrencies().toList(),
    val currenciesSearch: String = "",

    val isLoading: Boolean = false,

    val validationError: List<AddTransactionValidationError> = emptyList<AddTransactionValidationError>(),

    val status: UIStatus = UIStatus.Idle
) {
    val filteredCurrencies: List<Currency>
        get() =
            allCurrencies
                .filter {
                    it.currencyCode.lowercase().contains(currenciesSearch.lowercase())
                }
                .sortedByDescending { it == transaction.currency }
}

sealed interface UIStatus {

    object Idle : UIStatus

    object Loading : UIStatus

    object Success : UIStatus

    object ValidationError: UIStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : UIStatus
}
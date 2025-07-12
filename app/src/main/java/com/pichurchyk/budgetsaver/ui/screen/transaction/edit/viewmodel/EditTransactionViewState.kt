package com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import java.util.Currency

data class EditTransactionViewState(
    val transaction: TransactionCreation = TransactionCreation(),

    val allCurrencies: List<Currency> = Currency.getAvailableCurrencies().toList(),
    val currenciesSearch: String = "",

    val isLoading: Boolean = false,

    val validationError: List<EditTransactionValidationError> = emptyList<EditTransactionValidationError>(),

    val status: EditTransactionUiStatus = EditTransactionUiStatus.Idle
) {
    val filteredCurrencies: List<Currency>
        get() =
            allCurrencies
                .filter {
                    it.currencyCode.lowercase().contains(currenciesSearch.lowercase())
                }
                .sortedByDescending { it == transaction.currency }
}

sealed interface EditTransactionUiStatus {

    object Idle : EditTransactionUiStatus

    object Deleting : EditTransactionUiStatus

    object Loading : EditTransactionUiStatus

    object Success : EditTransactionUiStatus

    object ValidationError: EditTransactionUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : EditTransactionUiStatus
}
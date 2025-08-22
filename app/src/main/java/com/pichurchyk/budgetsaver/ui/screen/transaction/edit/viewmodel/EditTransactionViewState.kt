package com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionUiStatus
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionValidationError
import java.util.Currency

data class EditTransactionViewState(
    val transaction: TransactionCreation = TransactionCreation(),
    val allCurrencies: List<Currency>? = null,
    val currenciesSearch: String = "",

    val validationError: List<EditTransactionValidationError> = emptyList<EditTransactionValidationError>(),

    val status: EditTransactionUiStatus = EditTransactionUiStatus.Idle
) {
    val filteredCurrencies: List<Currency>
        get() =
            allCurrencies
                ?.filter {
                    it.currencyCode.lowercase().contains(currenciesSearch.lowercase()) ||
                            it.displayName.lowercase().contains(currenciesSearch.lowercase())
                }
                ?.sortedByDescending { it == transaction.currency } ?: emptyList()
}

sealed interface EditTransactionUiStatus {

    object Idle : EditTransactionUiStatus

    object Deleting : EditTransactionUiStatus

    object Loading : EditTransactionUiStatus

    data class Success(val action: EditTransactionAction): EditTransactionUiStatus

    object ValidationError: EditTransactionUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : EditTransactionUiStatus
}
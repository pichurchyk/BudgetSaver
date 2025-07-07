package com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

sealed class AddTransactionIntent {
    data object Submit: AddTransactionIntent()

    data class ChangeValue(val value: String): AddTransactionIntent()

    data class ChangeTitle(val value: String): AddTransactionIntent()

    data class ChangeNotes(val value: String): AddTransactionIntent()

    data class ChangeCurrency(val currency: Currency): AddTransactionIntent()

    data class SearchCurrency(val value: String): AddTransactionIntent()

    data class ChangeType(val value: TransactionType): AddTransactionIntent()

    data class ChangeCategory(val value: TransactionCategory?): AddTransactionIntent()

    data object ClearData: AddTransactionIntent()

    data object DismissNotification: AddTransactionIntent()
}
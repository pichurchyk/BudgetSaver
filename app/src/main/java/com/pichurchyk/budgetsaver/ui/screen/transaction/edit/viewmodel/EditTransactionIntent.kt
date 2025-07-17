package com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

sealed class EditTransactionIntent {
    data object Submit: EditTransactionIntent()

    data object Delete: EditTransactionIntent()

    data object SubmitDelete: EditTransactionIntent()

    data object CancelDelete: EditTransactionIntent()

    data class ChangeValue(val value: String): EditTransactionIntent()

    data class ChangeTitle(val value: String): EditTransactionIntent()

    data class ChangeNotes(val value: String): EditTransactionIntent()

    data class ChangeCurrency(val currency: Currency): EditTransactionIntent()

    data class SearchCurrency(val value: String): EditTransactionIntent()

    data class ChangeType(val value: TransactionType): EditTransactionIntent()

    data class ChangeCategory(val value: TransactionCategory?): EditTransactionIntent()

    data object DismissNotification: EditTransactionIntent()
}
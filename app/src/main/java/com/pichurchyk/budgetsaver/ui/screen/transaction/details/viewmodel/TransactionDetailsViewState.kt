package com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction

data class TransactionDetailsViewState(
    val transactions: List<Transaction> = emptyList(),
    val currentTransaction: Transaction? = null,
    val uiStatus: TransactionDetailsUiStatus = TransactionDetailsUiStatus.Idle
)

sealed interface TransactionDetailsUiStatus {
    data object Loading : TransactionDetailsUiStatus

    data object Idle : TransactionDetailsUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : TransactionDetailsUiStatus
}
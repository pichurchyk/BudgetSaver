package com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel

sealed interface TransactionDetailsIntent {
    data object LoadNextTransaction: TransactionDetailsIntent
    data object LoadPreviousTransaction: TransactionDetailsIntent
}
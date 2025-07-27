package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.runtime.Composable
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.math.BigInteger

@Composable
fun Transaction.getTransactionDefaultTitle(): String {
    val transactionType = if (this.value.amountMinor > BigInteger("0")) TransactionType.INCOMES else TransactionType.EXPENSES

    val categoryTitle = mainCategory?.title

    return categoryTitle?.let {
        "${transactionType.getTitle()} ($it)"
    } ?: transactionType.getTitle()
}
package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red

@Composable
fun TransactionType.getTitle() : String {
    return when(this) {
        TransactionType.EXPENSES -> stringResource(R.string.expenses)
        TransactionType.INCOMES -> stringResource(R.string.incomes)
    }
}

fun TransactionType.getColor() : Color {
    return when(this) {
        TransactionType.EXPENSES -> red
        TransactionType.INCOMES -> green
    }
}
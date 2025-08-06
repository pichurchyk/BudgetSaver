package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.nav.Screen

@Composable
fun Screen.getTitle(): String {
    return when(this) {
        Screen.Auth -> stringResource(R.string.auth)
        Screen.Profile -> stringResource(R.string.profile)
        Screen.Dashboard -> stringResource(R.string.dashboard)
        Screen.AddTransaction -> stringResource(R.string.add_transaction)
        Screen.AddCategory -> stringResource(R.string.add_category)
        is Screen.EditTransaction -> stringResource(R.string.edit_transaction)
    }
}
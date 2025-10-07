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
        Screen.AddCategory -> stringResource(R.string.add_category)
        is Screen.EditCategory -> stringResource(R.string.edit_category)
        Screen.AddPreset -> stringResource(R.string.add_preset)
        is Screen.AddTransaction -> stringResource(R.string.add_transaction)
        is Screen.EditTransaction -> stringResource(R.string.edit_transaction)
    }
}
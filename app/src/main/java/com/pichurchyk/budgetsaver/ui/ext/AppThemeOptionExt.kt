package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption

@Composable
fun AppThemeOption.getTitle(): String = when (this) {
    AppThemeOption.DARK -> stringResource(R.string.dark)
    AppThemeOption.LIGHT -> stringResource(R.string.light)
    AppThemeOption.SYSTEM -> stringResource(R.string.system)
}

@Composable
fun AppThemeOption.resolveTheme(): Boolean {
    return when (this) {
        AppThemeOption.DARK -> true
        AppThemeOption.LIGHT -> false
        AppThemeOption.SYSTEM -> isSystemInDarkTheme()
    }
}
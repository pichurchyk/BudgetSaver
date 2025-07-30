package com.pichurchyk.budgetsaver.domain.model.user

import java.util.Currency

data class UserPreferences(
    val favoriteCurrencies: List<Currency> = emptyList()
)
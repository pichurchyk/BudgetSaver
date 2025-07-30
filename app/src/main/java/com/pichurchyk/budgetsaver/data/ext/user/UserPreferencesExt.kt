package com.pichurchyk.budgetsaver.data.ext.user

import com.pichurchyk.budgetsaver.data.model.response.user.UserPreferencesResponse
import com.pichurchyk.budgetsaver.domain.model.user.UserPreferences
import java.util.Currency

fun UserPreferencesResponse.toUserPreferences() = UserPreferences(
    favoriteCurrencies = this.favoriteCurrencies.map { Currency.getInstance(it) })
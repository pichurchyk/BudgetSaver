package com.pichurchyk.budgetsaver.data.datasource

import android.util.Log
import com.pichurchyk.budgetsaver.domain.model.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Currency

class SessionManager {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun setUser(user: User) {
        _user.update { user }
    }

    fun addFavoriteCurrency(currency: Currency) {
        _user.value?.let { user ->

            _user.update {
                val currentList = user.preferences.favoriteCurrencies

                user.copy(preferences = user.preferences.copy(favoriteCurrencies = currentList + currency))
            }
        }
    }

    fun deleteFavoriteCurrency(currency: Currency) {
        _user.value?.let { user ->

            _user.update {
                val currentList = user.preferences.favoriteCurrencies

                user.copy(preferences = user.preferences.copy(favoriteCurrencies = currentList.filter { it != currency }))
            }
        }
    }

    fun clearSession() {
        _user.update { null }
    }
}
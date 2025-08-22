package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.util.Currency

class CurrencyRepositoryImpl(
    private val sessionManager: SessionManager
) : CurrencyRepository {

    private fun fetchAllAvailableCurrencies(): List<Currency> {
        return Currency.getAvailableCurrencies().toList().sortedBy { it.displayName }
    }

    override fun getAllCurrencies(): Flow<List<Currency>> {
        val allCurrenciesFlow = flowOf(fetchAllAvailableCurrencies())

        return combine(allCurrenciesFlow, sessionManager.user) { allCurrencies, user ->
            val favoriteCurrencies = user?.preferences?.favoriteCurrencies ?: emptyList()

            if (favoriteCurrencies.isEmpty()) {
                allCurrencies
            } else {
                val favoritesSet = favoriteCurrencies.toSet()
                val (favoritesInList, otherCurrencies) = allCurrencies.partition { it in favoritesSet }

                val sortedFavorites = favoriteCurrencies.filter { it in favoritesInList }
                (sortedFavorites + otherCurrencies).distinct()
            }
        }
    }
}

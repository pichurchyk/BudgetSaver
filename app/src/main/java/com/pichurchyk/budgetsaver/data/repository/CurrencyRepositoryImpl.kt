package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.Currency

class CurrencyRepositoryImpl(
    private val sessionManager: SessionManager
) : CurrencyRepository {

    private val cachedCurrencies = mutableListOf<Currency>()

    private fun fetchAllAvailableCurrencies(): List<Currency> {
        return Currency.getAvailableCurrencies().toList().sortedBy { it.displayName }
    }

    override fun getAllCurrencies(): Flow<List<Currency>> {
        if (cachedCurrencies.isNotEmpty()) {
            return flowOf(cachedCurrencies)
        }

        val allCurrenciesFlow = flowOf(fetchAllAvailableCurrencies())

        return combine(allCurrenciesFlow, sessionManager.user) { allCurrencies, user ->
            val favoriteCurrencies = user?.preferences?.favoriteCurrencies ?: emptyList()

            if (favoriteCurrencies.isEmpty()) {
                allCurrencies
            } else {
                val favoritesSet = favoriteCurrencies.toSet()
                val (favoritesInList, otherCurrencies) = allCurrencies.partition { it in favoritesSet }

                val sortedFavorites = favoriteCurrencies.filter { it in favoritesInList }
                val currenciesWithFavoriteFirst = (sortedFavorites + otherCurrencies).distinct()

                cachedCurrencies.addAll(currenciesWithFavoriteFirst)
                currenciesWithFavoriteFirst
            }
        }
    }

    override suspend fun updateCache() {
        cachedCurrencies.clear()

        val allCurrenciesFlow = flowOf(fetchAllAvailableCurrencies())

        combine(allCurrenciesFlow, sessionManager.user) { allCurrencies, user ->
            val favoriteCurrencies = user?.preferences?.favoriteCurrencies ?: emptyList()

            if (favoriteCurrencies.isEmpty()) {
                allCurrencies
            } else {
                val favoritesSet = favoriteCurrencies.toSet()
                val (favoritesInList, otherCurrencies) = allCurrencies.partition { it in favoritesSet }

                val sortedFavorites = favoriteCurrencies.filter { it in favoritesInList }
                val currenciesWithFavoriteFirst = (sortedFavorites + otherCurrencies).distinct()

                cachedCurrencies.addAll(currenciesWithFavoriteFirst)
                currenciesWithFavoriteFirst
            }
        }
            .first()
    }
}

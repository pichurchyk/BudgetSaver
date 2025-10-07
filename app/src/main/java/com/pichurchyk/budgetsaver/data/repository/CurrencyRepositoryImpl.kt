package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.Currency

internal class CurrencyRepositoryImpl(
    private val sessionManager: SessionManager,
    private val transactionsDataSource: TransactionsDataSource
) : CurrencyRepository {

    private val cachedCurrencies = mutableListOf<Currency>()

    private fun fetchAllAvailableCurrencies(): List<Currency> {
        return Currency.getAvailableCurrencies()
            .toList()
            .sortedBy { it.currencyCode }
            .also {
                cachedCurrencies.clear()
                cachedCurrencies.addAll(it)
            }
    }

    override fun getAllCurrencies(): Flow<List<Currency>> {
        if (cachedCurrencies.isNotEmpty()) {
            return flowOf(cachedCurrencies)
        }

        val allCurrenciesFlow = flowOf(fetchAllAvailableCurrencies())

        return combine(allCurrenciesFlow, sessionManager.user) { allCurrencies, user ->

            val favoriteCurrencies = user?.preferences?.favoriteCurrencies ?: emptyList()

            if (favoriteCurrencies.isEmpty()) {
                allCurrencies.sortedBy { it.currencyCode }
            } else {
                val favoritesInList = favoriteCurrencies.mapNotNull { fav ->
                    allCurrencies.find { it == fav }
                }
                val otherCurrencies = allCurrencies
                    .filterNot { it in favoritesInList }
                    .sortedBy { it.currencyCode }

                val currenciesWithFavoriteFirst = (favoritesInList + otherCurrencies).distinct()

                cachedCurrencies.clear()
                cachedCurrencies.addAll(currenciesWithFavoriteFirst)
                currenciesWithFavoriteFirst
            }
        }
    }

    override suspend fun deleteFavoriteCurrency(currency: Currency): Flow<List<Currency>> = flow {
        val userFavorites = sessionManager.user.value?.preferences?.favoriteCurrencies ?: emptyList()
        val updatedFavorites = userFavorites.filter { it != currency }

        val nonFavorites = cachedCurrencies.filterNot { it in updatedFavorites }
            .sortedBy { it.currencyCode }

        val updatedList = updatedFavorites + nonFavorites
        cachedCurrencies.clear()
        cachedCurrencies.addAll(updatedList)

        emit(cachedCurrencies)

        transactionsDataSource.removeFavoriteCurrency(currency)
    }

    override suspend fun addFavoriteCurrency(currency: Currency): Flow<List<Currency>> = flow {
        val userFavorites = sessionManager.user.value?.preferences?.favoriteCurrencies ?: emptyList()

        val updatedFavorites = (userFavorites + currency).distinct()
        val nonFavorites = cachedCurrencies.filterNot { it in updatedFavorites }
            .sortedBy { it.currencyCode }

        val updatedList = updatedFavorites + nonFavorites
        cachedCurrencies.clear()
        cachedCurrencies.addAll(updatedList)

        emit(cachedCurrencies)

        transactionsDataSource.addFavoriteCurrency(currency)
    }


    override suspend fun updateCache() {
        val allCurrenciesFlow = if (cachedCurrencies.isEmpty()) {
            flowOf(cachedCurrencies)
        } else {
            flowOf(fetchAllAvailableCurrencies())
        }

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

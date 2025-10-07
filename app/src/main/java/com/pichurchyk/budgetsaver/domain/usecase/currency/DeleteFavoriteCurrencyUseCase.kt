package com.pichurchyk.budgetsaver.domain.usecase.currency

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface DeleteFavoriteCurrencyUseCase {
    suspend fun invoke(currency: Currency): Flow<List<Currency>>
}

internal class DeleteFavoriteCurrencyUseCaseImpl(
    private val repository: CurrencyRepository,
    private val sessionManager: SessionManager
) : DeleteFavoriteCurrencyUseCase {
    override suspend fun invoke(currency: Currency): Flow<List<Currency>> =
        try {
            repository.deleteFavoriteCurrency(currency)
                .also { sessionManager.deleteFavoriteCurrency(currency) }
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
}
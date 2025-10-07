package com.pichurchyk.budgetsaver.domain.usecase.currency

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface AddFavoriteCurrencyUseCase {
    suspend fun invoke(currency: Currency): Flow<List<Currency>>
}

internal class AddFavoriteCurrencyUseCaseImpl(
    private val repository: CurrencyRepository,
    private val sessionManager: SessionManager
) : AddFavoriteCurrencyUseCase {
    override suspend fun invoke(currency: Currency) =
        try {
            repository.addFavoriteCurrency(currency).also {
                sessionManager.addFavoriteCurrency(currency)
            }
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
}
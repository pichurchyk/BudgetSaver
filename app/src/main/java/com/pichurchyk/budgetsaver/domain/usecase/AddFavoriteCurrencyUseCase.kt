package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Currency

interface AddFavoriteCurrencyUseCase {
    suspend fun invoke(currency: Currency): Flow<Unit>
}

internal class AddFavoriteCurrencyUseCaseImpl(
    private val repository: TransactionsRepository,
    private val sessionManager: SessionManager
) : AddFavoriteCurrencyUseCase {
    override suspend fun invoke(currency: Currency) = flow {
        try {
            emit(repository.addFavoriteCurrency(currency)).also {
                sessionManager.addFavoriteCurrency(currency)
            }
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
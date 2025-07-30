package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Currency

interface DeleteFavoriteCurrencyUseCase {
    suspend fun invoke(currency: Currency): Flow<Unit>
}

internal class DeleteFavoriteCurrencyUseCaseImpl(
    private val repository: TransactionsRepository,
    private val sessionManager: SessionManager
) : DeleteFavoriteCurrencyUseCase {
    override suspend fun invoke(currency: Currency) = flow {
        try {
            emit(repository.deleteFavoriteCurrency(currency)).also {
                sessionManager.deleteFavoriteCurrency(currency)
            }
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
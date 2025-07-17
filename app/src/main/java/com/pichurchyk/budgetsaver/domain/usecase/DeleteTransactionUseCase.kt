package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DeleteTransactionUseCase {
    suspend fun invoke(transactionId: String): Flow<Unit>
}

internal class DeleteTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : DeleteTransactionUseCase {
    override suspend fun invoke(transactionId: String): Flow<Unit> = flow {
        try {
            repository.deleteTransaction(transactionId)

            emit(Unit)
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
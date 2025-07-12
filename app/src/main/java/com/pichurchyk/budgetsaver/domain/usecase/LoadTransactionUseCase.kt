package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoadTransactionUseCase {
    suspend fun invoke(transactionId: String): Flow<Transaction>
}

internal class LoadTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : LoadTransactionUseCase {
    override suspend fun invoke(transactionId: String): Flow<Transaction> = flow {
        try {
            emit(repository.getTransaction(transactionId))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
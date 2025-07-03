package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddTransactionUseCase {
    suspend fun invoke(transaction: TransactionCreation): Flow<Unit>
}

internal class AddTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : AddTransactionUseCase {
    override suspend fun invoke(transaction: TransactionCreation): Flow<Unit> = flow {
        try {
            repository.addTransaction(transaction)

            emit(Unit)
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
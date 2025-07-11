package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.RelativeTransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GetSingleTransactionUseCase {
    suspend fun invoke(transactionId: String, direction: RelativeTransactionType? = null): Flow<Transaction>
}

internal class GetSingleTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : GetSingleTransactionUseCase {
    override suspend fun invoke(
        transactionId: String,
        direction: RelativeTransactionType?
    ): Flow<Transaction> = flow {
        try {
            val transaction = if (direction != null) {
                repository.getRelativeTransaction(transactionId, direction)
            } else {
                repository.getTransaction(transactionId)
            }

            emit(transaction)
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}
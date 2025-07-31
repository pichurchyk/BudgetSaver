package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

interface GetTransactionsCategoriesUseCase {
    suspend fun invoke(): Flow<List<TransactionCategory>>
}

internal class GetTransactionsCategoriesUseCaseImpl(
    private val repository: TransactionsRepository
) : GetTransactionsCategoriesUseCase {
    override suspend fun invoke() = repository.getCategories()

}
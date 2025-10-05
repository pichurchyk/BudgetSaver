package com.pichurchyk.budgetsaver.domain.usecase.category

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

interface GetTransactionsCategoriesUseCase {
    suspend fun invoke(categoriesId: List<String> = emptyList()): Flow<List<TransactionCategory>>
}

internal class GetTransactionsCategoriesUseCaseImpl(
    private val repository: TransactionsRepository
) : GetTransactionsCategoriesUseCase {
    override suspend fun invoke(categoriesId: List<String>) = repository.getCategories(categoriesId)
}
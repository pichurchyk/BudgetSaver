package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.ext.toDomain
import com.pichurchyk.budgetsaver.data.ext.toPayload
import com.pichurchyk.budgetsaver.domain.model.transaction.RelativeTransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TransactionsRepositoryImpl(
    private val transactionsDataSource: TransactionsDataSource
) : TransactionsRepository {

    override suspend fun getTransactions(): Flow<List<TransactionsByCurrency>> =
        transactionsDataSource.getTransactions().map { transactions ->
            transactions
                .map { transaction ->
                    transaction.toDomain()
                }
                .groupBy { it.value.currency }
                .map { (currency, transactions) ->
                    TransactionsByCurrency(
                        transactions = transactions,
                        currencyCode = currency
                    )
                }
        }

    override suspend fun getTransaction(transactionId: String): Transaction =
        transactionsDataSource.getTransaction(transactionId).toDomain()

    override suspend fun deleteCategory(categoryId: String) = transactionsDataSource.deleteCategory(categoryId)

    override suspend fun getRelativeTransaction(
        transactionId: String,
        direction: RelativeTransactionType
    ): Transaction =
        transactionsDataSource.getRelativeTransaction(transactionId, direction).toDomain()

    override suspend fun getCategories(): Flow<List<TransactionCategory>> =
        transactionsDataSource.getCategories().map { categories ->
            categories
                .map { category ->
                    category.toDomain()
                }
        }

    override suspend fun addTransaction(transaction: TransactionCreation): Unit =
        transactionsDataSource.addTransaction(transaction.toPayload())

    override suspend fun editTransaction(transactionId: String, transaction: TransactionCreation): Unit =
        transactionsDataSource.editTransaction(transactionId = transactionId, transaction.toPayload())

    override suspend fun deleteTransaction(transactionId: String): Unit =
        transactionsDataSource.deleteTransaction(transactionId = transactionId)

}
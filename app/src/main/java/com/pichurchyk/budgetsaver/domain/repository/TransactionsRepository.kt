package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.domain.model.transaction.RelativeTransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface TransactionsRepository {

    suspend fun getTransactions(currency: String): Flow<List<Transaction>>

    suspend fun getTransaction(transactionId: String): Transaction

    suspend fun deleteCategory(categoryId: String)

    suspend fun addCategory(category: TransactionCategoryCreation)

    suspend fun getRelativeTransaction(transactionId: String, direction: RelativeTransactionType): Transaction

    suspend fun getCategories(): Flow<List<TransactionCategory>>

    suspend fun addTransaction(transaction: TransactionCreation)

    suspend fun editTransaction(transactionId: String, transaction: TransactionCreation)

    suspend fun deleteTransaction(transactionId: String)

    suspend fun addFavoriteCurrency(currency: Currency)

    suspend fun deleteFavoriteCurrency(currency: Currency)

}
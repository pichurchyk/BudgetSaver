package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import kotlinx.coroutines.flow.Flow

internal interface TransactionsRepository {

    suspend fun getTransactions(): Flow<List<TransactionsByCurrency>>

    suspend fun getCategories(): Flow<List<TransactionCategory>>

    suspend fun addTransaction(transaction: TransactionCreation)

}
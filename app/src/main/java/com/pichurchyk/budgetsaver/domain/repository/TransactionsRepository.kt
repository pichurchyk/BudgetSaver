package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import kotlinx.coroutines.flow.Flow

internal interface TransactionsRepository {

    suspend fun getTransactions(currency: String): Flow<List<Transaction>>

    suspend fun getTransaction(transactionId: String): Transaction

    suspend fun deleteCategory(categoryId: String)

    suspend fun deletePreset(presetId: String)

    suspend fun addCategory(category: TransactionCategoryCreation)

    suspend fun getCategories(): Flow<List<TransactionCategory>>

    suspend fun getPresets(): Flow<List<TransactionPreset>>

    suspend fun addTransaction(transaction: TransactionCreation)

    suspend fun editTransaction(transactionId: String, transaction: TransactionCreation)

    suspend fun deleteTransaction(transactionId: String)

    suspend fun addPreset(preset: TransactionPresetCreation)

}
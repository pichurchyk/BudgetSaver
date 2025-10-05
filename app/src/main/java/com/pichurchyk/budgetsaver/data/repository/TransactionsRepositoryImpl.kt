package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.ext.category.toPayload
import com.pichurchyk.budgetsaver.data.ext.toDomain
import com.pichurchyk.budgetsaver.data.ext.toPayload
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class TransactionsRepositoryImpl(
    private val transactionsDataSource: TransactionsDataSource
) : TransactionsRepository {

    private val transactionsCache = mutableMapOf<String, List<Transaction>>()

    override suspend fun getTransactions(currency: String): Flow<List<Transaction>> {
        return flow {
            transactionsCache[currency]?.let { cachedTransactions ->
                emit(cachedTransactions)
            }

            transactionsDataSource.getTransactions(currency)
                .map { transactions ->
                    transactions.map { transaction ->
                        transaction.toDomain()
                    }
                }
                .collect { freshTransactions ->
                    transactionsCache[currency] = freshTransactions

                    emit(freshTransactions)
                }
        }
    }

    override suspend fun getTransaction(transactionId: String): Transaction =
        transactionsDataSource.getTransaction(transactionId).toDomain()

    override suspend fun deleteCategory(categoryId: String) {
        transactionsDataSource.deleteCategory(categoryId)

        transactionsCache.forEach { (currencyCode, transactions) ->
            val updatedTransactions = transactions.map { transaction ->
                val updatedMainCategory = if (transaction.mainCategory?.uuid == categoryId) {
                    null // Remove main category if it matches deleted category
                } else {
                    transaction.mainCategory
                }

                transaction.copy(
                    mainCategory = updatedMainCategory,
                )
            }
            transactionsCache[currencyCode] = updatedTransactions
        }
    }

    override suspend fun deletePreset(presetId: String) =
        transactionsDataSource.deletePreset(presetId)

    override suspend fun addCategory(category: TransactionCategoryCreation) {
        val newCategory = transactionsDataSource.addCategory(category.toPayload())

//        categoriesCache.forEach { (key, categories) ->
//            categoriesCache[key] = categories + newCategory.toDomain()
//        }
    }

    override suspend fun editCategory(categoryId: String, category: TransactionCategoryCreation) {
        val updatedCategory = transactionsDataSource.editCategory(categoryId, category.toPayload())
    }

    override suspend fun getCategories(categoriesId: List<String>): Flow<List<TransactionCategory>> =
        transactionsDataSource.getCategories(categoriesId).map { categories ->
            categories
                .map { category ->
                    category.toDomain()
                }
        }

    override suspend fun getPresets(): Flow<List<TransactionPreset>> =
        transactionsDataSource.getPresets().map { categories ->
            categories
                .map { category ->
                    category.toDomain()
                }
        }

    override suspend fun addPreset(preset: TransactionPresetCreation) =
        transactionsDataSource.addPreset(preset.toPayload())

    override suspend fun addTransaction(transaction: TransactionCreation) {
        val newTransaction = transactionsDataSource.addTransaction(transaction.toPayload())
            .toDomain()

        val currencyCode = transaction.currency.currencyCode
        val cached = transactionsCache[currencyCode].orEmpty()
        transactionsCache[currencyCode] = listOf(newTransaction) + cached
    }

    override suspend fun editTransaction(transactionId: String, transaction: TransactionCreation) {
        val updatedTransaction = transactionsDataSource.editTransaction(
            transactionId = transactionId,
            transaction.toPayload()
        ).toDomain()

        transactionsCache.forEach { (currencyCode, cached) ->
            if (cached.any { it.uuid == transactionId }) {
                transactionsCache[currencyCode] = cached.mapNotNull {
                    if (it.uuid == transactionId) null else it
                }
            }
        }

        val newCurrencyCode = updatedTransaction.value.currency
        val existing = transactionsCache[newCurrencyCode] ?: emptyList()
        transactionsCache[newCurrencyCode] = existing + updatedTransaction
    }


    override suspend fun deleteTransaction(transactionId: String) {
        transactionsDataSource.deleteTransaction(transactionId = transactionId)

        transactionsCache.forEach { (currencyCode, transactions) ->
            val updatedTransactions = transactions.filter { it.uuid != transactionId }
            if (updatedTransactions.size != transactions.size) {
                transactionsCache[currencyCode] = updatedTransactions
                return@forEach
            }
        }
    }
}
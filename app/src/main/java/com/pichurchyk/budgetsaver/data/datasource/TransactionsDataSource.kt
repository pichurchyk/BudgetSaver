package com.pichurchyk.budgetsaver.data.datasource

import com.pichurchyk.budgetsaver.data.model.payload.TransactionPayload
import com.pichurchyk.budgetsaver.data.model.response.MainCategoryResponse
import com.pichurchyk.budgetsaver.data.model.response.TransactionResponse
import com.pichurchyk.budgetsaver.domain.model.transaction.RelativeTransactionType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.resources.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

internal class TransactionsDataSource(
    private val httpClient: HttpClient,
) {
    fun getTransactions(): Flow<List<TransactionResponse>> = flow {
        httpClient
            .get(GetTransactions()) {
                parameter("sort", "dateMillis")
                parameter("order", "desc")
            }
            .body<List<TransactionResponse>>()
            .also { emit(it) }
    }

    suspend fun getTransaction(transactionId: String): TransactionResponse {
        return httpClient
            .get(GetTransaction()) {
                parameter("id", transactionId)
            }
            .body<TransactionResponse>()
    }

    suspend fun getRelativeTransaction(
        transactionId: String,
        direction: RelativeTransactionType
    ): TransactionResponse {
        return httpClient
            .get(GetTransaction()) {
                parameter("id", transactionId)
                parameter("direction", direction.name.lowercase())
            }
            .body<TransactionResponse>()
    }

    fun getCategories(): Flow<List<MainCategoryResponse>> = flow {
        httpClient
            .get(Category()) {
                parameter("order", "created.desc")
            }
            .body<List<MainCategoryResponse>>()
            .also { emit(it) }
    }

    suspend fun addTransaction(transactionPayload: TransactionPayload) {
        httpClient.post(Transaction()) {
            setBody(transactionPayload)
        }.body<Unit>()
    }

    suspend fun editTransaction(transactionId: String, transactionPayload: TransactionPayload) {
        httpClient.post(Transaction()) {
            parameter("id", "eq.$transactionId")

            setBody(transactionPayload)
        }.body<Unit>()
    }

    suspend fun deleteTransaction(transactionId: String) {
        httpClient.delete(Transaction()) {
            parameter("id", "eq.$transactionId")
        }.body<Unit>()
    }

    companion object {
        private const val TAG = "AuthDataSource"
    }
}

@Serializable
@Resource("/functions/v1/all_transactions")
private class GetTransactions()

@Serializable
@Resource("/functions/v1/single-transaction")
private class GetTransaction()

@Serializable
@Resource("/rest/v1/Category")
private class Category()

@Serializable
@Resource("/rest/v1/Transaction")
private class Transaction()
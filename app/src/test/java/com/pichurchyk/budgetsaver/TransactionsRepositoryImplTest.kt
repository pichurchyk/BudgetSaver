package com.pichurchyk.budgetsaver

import app.cash.turbine.test
import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.ext.toDomain
import com.pichurchyk.budgetsaver.data.model.response.MainCategoryResponse
import com.pichurchyk.budgetsaver.data.model.response.TransactionResponse
import com.pichurchyk.budgetsaver.data.repository.TransactionsRepositoryImpl
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.Currency

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TransactionsRepositoryImpl Tests")
class TransactionsRepositoryImplTest {

    private lateinit var dataSource: TransactionsDataSource
    private lateinit var repository: TransactionsRepositoryImpl

    private val usd = Currency.getInstance("USD")
    private val eur = Currency.getInstance("EUR")

    private val mockCategoryResponse = MainCategoryResponse(
        uuid = "cat",
        emoji = "\uD83D\uDE0A",
        title = "title",
        color = "#FFFFFF"
    )
    private val mockCategory = mockCategoryResponse.toDomain()

    private val newTransactionCreation = TransactionCreation(
        title = "title",
        value = "123",
        currency = usd,
        notes = "notes",
        date = TransactionDate(
            dateInstant = Clock.System.now(),
            timeZone = TimeZone.currentSystemDefault()
        ),
        type = TransactionType.EXPENSES,
        mainCategory = mockCategory,
        subCategory = emptyList()
    )

    private val mockTransaction = Transaction(
        uuid = "tx1",
        title = newTransactionCreation.title,
        notes = newTransactionCreation.notes,
        value = Money(BigInteger.valueOf(newTransactionCreation.value.toLong()), usd.currencyCode),
        date = newTransactionCreation.date,
        mainCategory = newTransactionCreation.mainCategory
    )

    private val mockTransactionResponse = TransactionResponse(
        uuid = mockTransaction.uuid,
        title = newTransactionCreation.title,
        notes = newTransactionCreation.notes,
        value = newTransactionCreation.value,
        currency = usd.currencyCode,
        dateMillis = Clock.System.now().toEpochMilliseconds(),
        dateTimeZone = TimeZone.currentSystemDefault().id,
        mainCategory = mockCategoryResponse
    )

    @BeforeEach
    fun setUp() {
        dataSource = mockk(relaxed = true)
        repository = TransactionsRepositoryImpl(dataSource)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("getTransactions() Tests")
    inner class GetTransactionsTests {
        @Test
        fun `should return cached transactions if available`() = runTest {
            repository.apply {
                val field = this::class.java.getDeclaredField("transactionsCache")
                field.isAccessible = true
                val cache = field.get(this) as MutableMap<String, List<Transaction>>
                cache[usd.currencyCode] = listOf(mockTransaction)
            }

            repository.getTransactions(usd.currencyCode).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(mockTransactionResponse.uuid, result.first().uuid)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `should fetch and cache fresh transactions when cache is empty`() = runTest {
            val flow = MutableStateFlow(listOf(mockTransactionResponse))
            coEvery { dataSource.getTransactions(usd.currencyCode) } returns flow

            repository.getTransactions(usd.currencyCode).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("tx1", result.first().uuid)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("addTransaction() Tests")
    inner class AddTransactionTests {
        @Test
        fun `should add transaction to cache`() = runTest {
            val newTransactionResponse = mockTransactionResponse.copy(uuid = "newTx")

            coEvery { dataSource.addTransaction(any()) } returns newTransactionResponse

            repository.addTransaction(newTransactionCreation)

            val field = repository::class.java.getDeclaredField("transactionsCache")
            field.isAccessible = true
            val cache = field.get(repository) as MutableMap<String, List<Transaction>>
            assertTrue(cache[usd.currencyCode]!!.any { it.uuid == newTransactionResponse.uuid })
        }
    }

    @Nested
    @DisplayName("editTransaction() Tests")
    inner class EditTransactionTests {

        @Test
        fun `should update transaction in cache if currency not changed`() = runTest {
            val originalTransaction = mockTransactionResponse.toDomain()

            val cacheField = repository::class.java.getDeclaredField("transactionsCache")
                .apply { isAccessible = true }
            val cache = cacheField.get(repository) as MutableMap<String, List<Transaction>>
            cache[originalTransaction.value.currency] = listOf(originalTransaction)

            val updatedTransactionCreation =
                originalTransaction.toCreation().copy(title = "updated title")

            coEvery {
                dataSource.editTransaction(
                    "tx1",
                    any()
                )
            } returns updatedTransactionCreation.toResponse(originalTransaction.uuid)

            repository.editTransaction("tx1", updatedTransactionCreation)

            val updatedCache = cacheField.get(repository) as MutableMap<String, List<Transaction>>
            val updatedTx = updatedCache[updatedTransactionCreation.currency.currencyCode]!!.first()
            assertEquals("tx1", updatedTx.uuid)
            assertEquals("updated title", updatedTx.title)
        }

        @Test
        fun `should update transaction in cache even if currency changed`() = runTest {
            // arrange: repository must know about the original transaction
            val originalTransactionResponse = mockTransactionResponse
            coEvery { dataSource.getTransactions(any()) } returns flowOf(
                listOf(
                    originalTransactionResponse
                )
            )

            // call repo to load data (so it fills the cache naturally)
            repository.getTransactions(originalTransactionResponse.currency).first()

            val updatedTransactionCreation =
                originalTransactionResponse.toDomain().toCreation().copy(currency = eur)

            coEvery {
                dataSource.editTransaction("tx1", any())
            } returns updatedTransactionCreation.toResponse(originalTransactionResponse.uuid)

            // act
            repository.editTransaction("tx1", updatedTransactionCreation)

            // assert: inspect the repo's cache via reflection or, even better, a repo getter
            val cacheField = repository::class.java.getDeclaredField("transactionsCache")
                .apply { isAccessible = true }
            val updatedCache = cacheField.get(repository) as MutableMap<String, List<Transaction>>

            // old currency no longer has it
            assertTrue(updatedCache[originalTransactionResponse.currency]?.none { it.uuid == "tx1" } == true)

            // new currency has it
            assertTrue(updatedCache[updatedTransactionCreation.currency.currencyCode]?.any { it.uuid == "tx1" } == true)
        }
    }

    @Nested
    @DisplayName("deleteTransaction() Tests")
    inner class DeleteTransactionTests {
        @Test
        fun `should remove transaction from cache`() = runTest {
            val cacheField = repository::class.java.getDeclaredField("transactionsCache")
                .apply { isAccessible = true }
            val cache = cacheField.get(repository) as MutableMap<String, List<Transaction>>
            cache[eur.currencyCode] = listOf(mockTransaction)

            repository.deleteTransaction("tx1")

            assertTrue(cache[eur.currencyCode]!!.isEmpty())
        }
    }

    @Nested
    @DisplayName("getCategories() Tests")
    inner class GetCategoriesTests {
        @Test
        fun `should map categories from datasource`() = runTest {
            coEvery { dataSource.getCategories() } returns flowOf(listOf(mockCategoryResponse))

            repository.getCategories().test {
                val result = awaitItem()
                assertEquals(mockCategoryResponse.uuid, result.first().uuid)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}

fun Transaction.toCreation(): TransactionCreation {
    return TransactionCreation(
        title = this.title,
        value = this.value.amountMinor.toString(),
        currency = Currency.getInstance(this.value.currency),
        notes = this.notes,
        date = this.date,
        type = TransactionType.EXPENSES,
        mainCategory = this.mainCategory,
        subCategory = this.subCategory
    )
}

fun TransactionCreation.toResponse(
    uuid: String
): TransactionResponse {
    return TransactionResponse(
        uuid = uuid,
        title = this.title,
        value = this.value,
        currency = this.currency.currencyCode,
        notes = this.notes,
        dateMillis = this.date.dateInstant.toEpochMilliseconds(),
        dateTimeZone = this.date.timeZone.id,
        mainCategory = this.mainCategory?.let {
            MainCategoryResponse(
                uuid = it.uuid,
                emoji = it.emoji,
                title = it.title,
                color = it.color
            )
        },
    )
}
package com.pichurchyk.budgetsaver

import app.cash.turbine.test
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.repository.CurrencyRepositoryImpl
import com.pichurchyk.budgetsaver.domain.model.user.User
import com.pichurchyk.budgetsaver.domain.model.user.UserPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Currency

@DisplayName("CurrencyRepository Tests")
class CurrencyRepositoryTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var repository: CurrencyRepositoryImpl

    private val usd = Currency.getInstance("USD")
    private val eur = Currency.getInstance("EUR")
    private val gbp = Currency.getInstance("GBP")
    private val jpy = Currency.getInstance("JPY")

    private val pln = Currency.getInstance("PLN")

    private val mockCurrencies = listOf(usd, eur, gbp, jpy).sortedBy { it.displayName }

    @BeforeEach
    fun setUp() {
        sessionManager = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        repository = CurrencyRepositoryImpl(sessionManager, transactionsDataSource = mockk())

        mockkStatic(Currency::class)
        every { Currency.getAvailableCurrencies() } returns mockCurrencies.toSet()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("getAllCurrencies() Tests")
    inner class GetAllCurrenciesTests {

        @Test
        @DisplayName("Should return all currencies when no user preferences")
        fun `getAllCurrencies returns all currencies when no user preferences`() = runTest {
            val userFlow = MutableStateFlow<User?>(null)
            every { sessionManager.user } returns userFlow

            repository.getAllCurrencies().test {
                val result = awaitItem()
                assertEquals(mockCurrencies.size, result.size)
                assertEquals(mockCurrencies, result)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("Should return all currencies when user has no favorite currencies")
        fun `getAllCurrencies returns all currencies when user has no favorites`() = runTest {
            val user = User("1", preferences = UserPreferences(favoriteCurrencies = emptyList()))

            val userFlow = MutableStateFlow<User?>(user)
            every { sessionManager.user } returns userFlow

            repository.getAllCurrencies().test {
                val result = awaitItem()
                assertEquals(mockCurrencies.size, result.size)
                assertEquals(mockCurrencies, result)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("Should prioritize favorite currencies when user has preferences")
        fun `getAllCurrencies prioritizes favorite currencies`() = runTest {
            val favoriteCurrencies =
                listOf(gbp, eur)

            val user =
                User("1", preferences = UserPreferences(favoriteCurrencies = favoriteCurrencies))
            val userFlow = MutableStateFlow<User?>(user)
            every { sessionManager.user } returns userFlow

            repository.getAllCurrencies().test {
                val result = awaitItem()

                assertEquals(eur, result[0])
                assertEquals(gbp, result[1])
                assertEquals(mockCurrencies.size, result.size)
                assertTrue(result.containsAll(mockCurrencies))

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("Should use cache on subsequent calls")
        fun `getAllCurrencies uses cache on subsequent calls`() = runTest {
            val userFlow = MutableStateFlow<User?>(null)
            every { sessionManager.user } returns userFlow

            repository.getAllCurrencies().test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }

            repository.getAllCurrencies().test {
                val result = awaitItem()
                assertEquals(mockCurrencies.size, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("Should handle favorite currencies not in available list")
        fun `getAllCurrencies handles favorites not in available list`() = runTest {
            val favoriteCurrencies =
                listOf(eur, pln)
            val user =
                User("1", preferences = UserPreferences(favoriteCurrencies = favoriteCurrencies))
            val userFlow = MutableStateFlow<User?>(user)
            every { sessionManager.user } returns userFlow

            repository.getAllCurrencies().test {
                val result = awaitItem()

                assertEquals(eur, result[0])
                assertEquals(mockCurrencies.size, result.size)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("updateCache() Tests")
    inner class UpdateCacheTests {

        @Test
        @DisplayName("Should clear and rebuild cache")
        fun `updateCache clears and rebuilds cache`() = runTest {
            val initialUserFlow = MutableStateFlow<User?>(null)
            every { sessionManager.user } returns initialUserFlow

            repository.getAllCurrencies().test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }

            val updatedUser =
                User("1", preferences = UserPreferences(listOf(jpy)))
            val updatedUserFlow = MutableStateFlow<User?>(updatedUser)
            every { sessionManager.user } returns updatedUserFlow

            repository.updateCache()

            repository.getAllCurrencies().test {
                val result = awaitItem()
                assertEquals(jpy, result[0])
                awaitComplete() // Correct: Reading from the now-updated cache
            }
        }

        @Test
        @DisplayName("Should handle empty user preferences in cache update")
        fun `updateCache handles empty user preferences`() = runTest {
            val initialUser =
                User("1", preferences = UserPreferences(listOf(jpy)))
            every { sessionManager.user } returns MutableStateFlow<User?>(initialUser)
            repository.getAllCurrencies().test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }

            every { sessionManager.user } returns MutableStateFlow<User?>(null)

            repository.updateCache()

            repository.getAllCurrencies().test {
                val result = awaitItem()
                assertEquals(mockCurrencies.size, result.size)
                assertEquals(mockCurrencies, result)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
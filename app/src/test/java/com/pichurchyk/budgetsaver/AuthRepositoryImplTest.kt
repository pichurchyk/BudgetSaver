package com.pichurchyk.budgetsaver

import app.cash.turbine.test
import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.ext.user.toUser
import com.pichurchyk.budgetsaver.data.model.response.user.UserPreferencesResponse
import com.pichurchyk.budgetsaver.data.model.response.user.UserResponse
import com.pichurchyk.budgetsaver.data.repository.AuthRepositoryImpl
import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.model.user.User
import io.github.jan.supabase.gotrue.user.UserInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AuthRepositoryImpl Tests")
internal class AuthRepositoryImplTest {

    private lateinit var authDataSource: AuthDataSource
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: AuthRepositoryImpl

    private val mockUserResponse = UserResponse(
        id = "123",
        name = "Test User",
        email = "test@example.com",
        avatarUrl = "https://example.com/pic.jpg",
        preferences = UserPreferencesResponse(
            emptyList()
        )
    )

    private val mockUser = mockUserResponse.toUser()
    private val mockedUserInfo = mockk<UserInfo>()

    @BeforeEach
    fun setUp() {
        authDataSource = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        repository = AuthRepositoryImpl(authDataSource, sessionManager)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("signIn() Tests")
    inner class SignInTests {
        @Test
        fun `should return flow from authDataSource on signIn`() = runTest {
            val googleIdToken = "test_token"
            val expectedResult = SignInResult.Success(mockedUserInfo)
            coEvery { authDataSource.signIn(googleIdToken) } returns flowOf(expectedResult)

            repository.signIn(googleIdToken).test {
                val result = awaitItem()
                assertEquals(expectedResult, result)
                awaitComplete()
            }

            coVerify(exactly = 1) { authDataSource.signIn(googleIdToken) }
        }

        @Test
        fun `should add user to session on getUser()`() = runTest {
            coEvery { authDataSource.getUser() } returns mockUserResponse

            repository.getUser()

            coVerify(exactly = 1) { authDataSource.getUser() }
            coVerify(exactly = 1) { sessionManager.setUser(mockUser) }
        }
    }

    @Nested
    @DisplayName("getUser() Tests")
    inner class GetUserTests {

        @Test
        fun `should return user and update session when user is available`() = runTest {
            coEvery { authDataSource.getUser() } returns mockUserResponse

            val user = repository.getUser()

            assertEquals(mockUser, user)
            coVerify(exactly = 1) { authDataSource.getUser() }
            coVerify(exactly = 1) { sessionManager.setUser(mockUser) }
        }

        @Test
        fun `should return null and not update session when user is not available`() = runTest {
            coEvery { authDataSource.getUser() } returns null

            val user = repository.getUser()

            assertNull(user)
            coVerify(exactly = 1) { authDataSource.getUser() }
            coVerify(exactly = 0) { sessionManager.setUser(any()) }
        }
    }

    @Nested
    @DisplayName("signOut() Tests")
    inner class SignOutTests {
        @Test
        fun `should call signOut() and clear sessionManager`() = runTest {
            repository.signOut()

            coVerify(exactly = 1) { authDataSource.signOut() }
            coVerify(exactly = 1) { sessionManager.clearSession() }
        }
    }
}

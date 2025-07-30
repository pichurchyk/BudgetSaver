package com.pichurchyk.budgetsaver.data.datasource

import android.util.Log
import com.pichurchyk.budgetsaver.data.model.response.user.UserResponse
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferencesActions
import com.pichurchyk.budgetsaver.domain.model.SignInResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

internal class AuthDataSource(
    private val supabaseClient: SupabaseClient,
    private val preferences: AuthPreferencesActions,
    private val httpClient: HttpClient,
) {
    suspend fun getUser(): UserResponse? {
        return httpClient
            .get(User())
            .body<UserResponse>()
    }

    fun signIn(googleIdToken: String): Flow<SignInResult> = callbackFlow {
        try {
            supabaseClient.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }

            supabaseClient.auth.sessionStatus.first {
                when (it) {
                    is SessionStatus.Authenticated -> {
                        trySend(
                            SignInResult.Success(it.session.user!!)
                        )

                        preferences.setAccessToken(it.session.accessToken)
                        preferences.setRefreshToken(it.session.refreshToken)
                        preferences.setUserUid(it.session.user?.id)
                    }

                    else -> {
                        trySend(
                            SignInResult.Error("Error while signing in")
                        )
                    }
                }

                close()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Error occurred")
            trySend(
                SignInResult.Error("Error while signing in")
            )

            close()
        }

        awaitClose()
    }

    fun signOut(): Flow<Unit> = callbackFlow {
        try {
            supabaseClient.auth.signOut()

            preferences.setAccessToken(null)
            preferences.setRefreshToken(null)
            preferences.setUserUid(null)

            close()

            awaitClose()
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Error occurred")
        }
    }

    companion object {
        private const val TAG = "AuthDataSource"
    }
}

@Serializable
@Resource("/functions/v1/user")
private class User()
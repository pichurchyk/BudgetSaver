package com.pichurchyk.budgetsaver.ui.screen.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.pichurchyk.budgetsaver.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoogleAuthClient(
    private val context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    private val _accountGoogleIdToken = MutableStateFlow<String?>(null)
    val accountGoogleIdToken: StateFlow<String?> = _accountGoogleIdToken

    private val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()

    private val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    suspend fun startSignIn() {
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = credentialRequest
            )

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            _accountGoogleIdToken.value = googleIdToken

            Log.i(TAG, "GoogleSignInCredentials received")
        } catch (e: GetCredentialException) {
            Log.e(TAG, e.message ?: "Error while getting credentials")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, e.message ?: "Error while parsing GoogleIdToken")
        }
    }

    fun clearIdToken() {
        _accountGoogleIdToken.value = null
    }

    companion object {
        private const val TAG = "GoogleAuthClient"
    }
}

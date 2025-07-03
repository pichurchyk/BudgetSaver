package com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel

sealed class AuthIntent {
    data class Auth(val googleIdToken: String): AuthIntent()
    data object CheckSignedInUser: AuthIntent()
}
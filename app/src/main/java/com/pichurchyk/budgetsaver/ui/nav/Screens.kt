package com.pichurchyk.budgetsaver.ui.nav

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable
    data object Auth: Screen()

    @Serializable
    data object Dashboard: Screen()

    @Serializable
    data object AddTransaction: Screen()

    @Serializable
    data class EditTransaction(val transactionId: String): Screen()

}
package com.pichurchyk.budgetsaver.ui.common.notification

data class NotificationAction(
    val name: String,
    val action: () -> Unit
)
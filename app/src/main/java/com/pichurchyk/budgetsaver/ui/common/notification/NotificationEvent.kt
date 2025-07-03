package com.pichurchyk.budgetsaver.ui.common.notification

data class NotificationEvent(
    val message: String,
    val type: NotificationType = NotificationType.INFO,
    val action: NotificationAction? = null
)
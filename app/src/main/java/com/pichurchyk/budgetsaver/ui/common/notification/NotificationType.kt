package com.pichurchyk.budgetsaver.ui.common.notification

import androidx.compose.ui.graphics.Color
import com.pichurchyk.budgetsaver.ui.theme.notificationGreenDark
import com.pichurchyk.budgetsaver.ui.theme.notificationGreenLight
import com.pichurchyk.budgetsaver.ui.theme.notificationRedDark
import com.pichurchyk.budgetsaver.ui.theme.notificationRedLight
import com.pichurchyk.budgetsaver.ui.theme.notificationYellowDark
import com.pichurchyk.budgetsaver.ui.theme.notificationYellowLight

enum class NotificationType {
    ERROR,
    SUCCESS,
    INFO
}

val NotificationType.bgColor: Color
    get() = when (this) {
        NotificationType.INFO -> notificationYellowLight
        NotificationType.ERROR -> notificationRedLight
        NotificationType.SUCCESS -> notificationGreenLight
    }

val NotificationType.textColor: Color
    get() = when (this) {
        NotificationType.INFO -> notificationYellowDark
        NotificationType.ERROR -> notificationRedDark
        NotificationType.SUCCESS -> notificationGreenDark
    }
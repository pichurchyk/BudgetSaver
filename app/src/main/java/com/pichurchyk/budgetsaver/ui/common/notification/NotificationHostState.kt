package com.pichurchyk.budgetsaver.ui.common.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationHostState {
    private val _event = MutableStateFlow<NotificationEvent?>(null)
    val event: StateFlow<NotificationEvent?> = _event

    suspend fun show(event: NotificationEvent) {
        _event.emit(event)
    }

    suspend fun clear() {
        _event.emit(null)
    }
}

package com.pichurchyk.budgetsaver.ui.common.notification

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object NotificationController {

    private val _events = Channel<NotificationEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: NotificationEvent) {
        _events.send(event)
    }

}
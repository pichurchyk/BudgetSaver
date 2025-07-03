package com.pichurchyk.budgetsaver.ui.common.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@Composable
fun NotificationHost(state: NotificationHostState) {
    val event by state.event.collectAsState()

    var notificationData by remember {
        mutableStateOf<NotificationEvent?>(null)
    }

    var notificationVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(event) {
        notificationVisible = event != null

        if (event != null) {
            notificationData = event
        }
    }

    AnimatedVisibility(
        visible = notificationVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {

        notificationData?.let { notification ->
            AppNotification(
                modifier = Modifier
                    .padding(
                        WindowInsets.statusBars
                            .only(WindowInsetsSides.Top)
                            .asPaddingValues()
                    ),
                message = notification.message,
                action = {
                    notification.action?.action?.invoke()

                    coroutineScope.launch {
                        state.clear()
                    }
                },
                actionText = notification.action?.name,
                type = notification.type
            )
        }
    }

    LaunchedEffect(event) {
        if (event != null && event?.action == null) {

            delay(2000)

            state.clear()
        }
    }
}

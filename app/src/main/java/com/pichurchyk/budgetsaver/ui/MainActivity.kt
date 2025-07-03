package com.pichurchyk.budgetsaver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.pichurchyk.budgetsaver.ui.common.ObserveAsEvents
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationHost
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationHostState
import com.pichurchyk.budgetsaver.ui.nav.NavHost
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val notificationHostState = remember { NotificationHostState() }

            val coroutineScope = rememberCoroutineScope()

            ObserveAsEvents(NotificationController.events) { event ->
                coroutineScope.launch {
                    notificationHostState.show(event)
                }
            }

            AppTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(rememberNavController())

                    NotificationHost(state = notificationHostState)
                }
            }
        }
    }
}
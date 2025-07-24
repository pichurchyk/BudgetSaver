package com.pichurchyk.budgetsaver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pichurchyk.budgetsaver.ui.common.ObserveAsEvents
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationHost
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationHostState
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.ext.navigateSingleTopTo
import com.pichurchyk.budgetsaver.ui.ext.resolveTheme
import com.pichurchyk.budgetsaver.ui.nav.NavHost
import com.pichurchyk.budgetsaver.ui.nav.Screen
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import com.pichurchyk.budgetsaver.ui.theme.red
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val notificationHostState = remember { NotificationHostState() }

            val coroutineScope = rememberCoroutineScope()

            ObserveAsEvents(NotificationController.events) { event ->
                coroutineScope.launch {
                    notificationHostState.show(event)
                }
            }
            val themeState = remember { mutableStateOf(AppThemeOption.DARK) }
            val localAppTheme = compositionLocalOf { mutableStateOf(AppThemeOption.DARK) }


            CompositionLocalProvider(localAppTheme provides themeState) {

                val isDark = themeState.value.resolveTheme()

                AppTheme(darkTheme = isDark) {
                    Scaffold(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        topBar = {
                            val currentScreen =
                                Screen.screensWithMenu.firstOrNull { currentDestination?.route == it::class.qualifiedName }

                            currentScreen?.let { currentScreen ->
                                if (currentScreen != Screen.Auth) {
                                    Row(
                                        modifier = Modifier
                                            .padding(
                                                top = WindowInsets.statusBars
                                                    .asPaddingValues()
                                                    .calculateTopPadding() + 16.dp,
                                            )
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Screen.menuScreens.forEach { screen ->
                                            val isSelected = screen == currentScreen
                                            val targetWeight =
                                                if (isSelected) Screen.menuScreens.size.toFloat() else 1f
                                            val animatedWeight by animateFloatAsState(
                                                targetValue = targetWeight,
                                                animationSpec = tween(durationMillis = 300)
                                            )

                                            val targetSize = if (isSelected) 20f else 16f

                                            val animatedFontSize by animateFloatAsState(
                                                targetValue = targetSize,
                                                animationSpec = tween(durationMillis = 300),
                                                label = "FontSizeAnimation"
                                            )

                                            Text(
                                                modifier = Modifier
                                                    .weight(animatedWeight)
                                                    .doOnClick {
                                                        navController.navigateSingleTopTo(screen)
                                                    },
                                                textAlign = TextAlign.Center,
                                                fontSize = animatedFontSize.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                text = screen.getTitle(),
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else disableGrey
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        content = { paddingValues ->
                            Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
                                NavHost(navController)

                                NotificationHost(state = notificationHostState)
                            }
                        },
                        bottomBar = {}
                    )
                }
            }
        }
    }
}
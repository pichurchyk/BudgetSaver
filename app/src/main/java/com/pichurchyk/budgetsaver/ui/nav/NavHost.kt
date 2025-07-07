package com.pichurchyk.budgetsaver.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pichurchyk.budgetsaver.ui.ext.navigateSingleTopTo
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.AddTransactionScreen
import com.pichurchyk.budgetsaver.ui.screen.auth.AuthScreen
import com.pichurchyk.budgetsaver.ui.screen.dashboard.DashboardScreen

@Composable
fun NavHost(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Screen.Auth) {

        composable<Screen.Auth> {
            AuthScreen(
                openDashboard = {
                    navController.navigateSingleTopTo(Screen.Dashboard)
                }
            )
        }

        composable<Screen.Dashboard> {
            DashboardScreen(
                openAddTransactionScreen = {
                    navController.navigate(Screen.AddTransaction)
                }
            )
        }

        composable<Screen.AddTransaction> {
            AddTransactionScreen(
                closeScreen = { navController.popBackStack() }
            )
        }
    }
}
package com.pichurchyk.budgetsaver.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.pichurchyk.budgetsaver.ui.ext.navigateSingleTopTo
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.AddTransactionScreen
import com.pichurchyk.budgetsaver.ui.screen.auth.AuthScreen
import com.pichurchyk.budgetsaver.ui.screen.dashboard.DashboardScreen
import com.pichurchyk.budgetsaver.ui.screen.profile.ProfileScreen
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.EditTransactionScreen

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
                },
                openEditTransactionScreen = { transactionId ->
                    navController.navigate(Screen.EditTransaction(transactionId = transactionId))
                }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen()
        }

        composable<Screen.AddTransaction> {
            AddTransactionScreen(
                closeScreen = { navController.popBackStack() },
            )
        }

        composable<Screen.EditTransaction> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.EditTransaction>()

            EditTransactionScreen(
                transactionId = args.transactionId,
                closeScreen = { navController.popBackStack() },
            )
        }
    }
}
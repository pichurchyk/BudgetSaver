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
import com.pichurchyk.budgetsaver.ui.screen.transaction.details.TransactionDetailsScreen

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
                openTransactionDetails = { transactions, transactionId ->
                    navController.navigate(Screen.TransactionDetails(transactionIdToOpen = transactionId))
                }
            )
        }

        composable<Screen.AddTransaction> {
            AddTransactionScreen(
                closeScreen = { navController.popBackStack() },
            )
        }

        composable<Screen.TransactionDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.TransactionDetails>()
            TransactionDetailsScreen(
                initialTransactionId = args.transactionIdToOpen,
                closeScreen = { navController.popBackStack() }
            )
        }
    }
}
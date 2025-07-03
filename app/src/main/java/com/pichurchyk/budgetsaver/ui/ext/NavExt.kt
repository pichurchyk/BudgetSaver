package com.pichurchyk.budgetsaver.ui.ext

import androidx.navigation.NavHostController
import com.pichurchyk.budgetsaver.ui.nav.Screen

fun NavHostController.navigateSingleTopTo(route: Screen) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.id
        )
        launchSingleTop = true
        restoreState = true
    }
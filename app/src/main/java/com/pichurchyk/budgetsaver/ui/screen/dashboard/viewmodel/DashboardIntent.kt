package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

sealed class DashboardIntent {
    data object LoadData: DashboardIntent()

    data object ToggleAllTypesFilter: DashboardIntent()
    data class ToggleTypeFilter(val type: TransactionType): DashboardIntent()

    data class ToggleCategoriesFilter(val category: TransactionCategory): DashboardIntent()
    data object ToggleAllCategoriesFilter: DashboardIntent()

    data class SelectCurrency(val currency: Currency): DashboardIntent()
}
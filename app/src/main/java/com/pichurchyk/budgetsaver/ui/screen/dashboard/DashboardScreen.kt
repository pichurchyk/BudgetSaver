package com.pichurchyk.budgetsaver.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.common.ErrorBlock
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.common.currency.CurrencyItem
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.CategoriesFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.ExpenseIncomeFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.total.DashboardTotal
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardIntent
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardUiStatus
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import com.pichurchyk.budgetsaver.ui.theme.notificationRedDark
import com.pichurchyk.budgetsaver.ui.theme.notificationRedLight
import org.koin.androidx.compose.koinViewModel
import java.math.BigInteger
import java.util.Currency


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    openEditTransactionScreen: (transactionId: String) -> Unit,
    openAddTransactionScreen: () -> Unit
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Content(
        viewState = viewState,
        callViewModel = { viewModel.handleIntent(it) },
        onAddTransactionClick = openAddTransactionScreen,
        onEditTransactionClick = openEditTransactionScreen,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewState: DashboardViewState,
    callViewModel: (DashboardIntent) -> Unit,
    onAddTransactionClick: () -> Unit,
    onEditTransactionClick: (transactionId: String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        callViewModel(DashboardIntent.Init)
    }

    if (viewState.status == DashboardUiStatus.LoadingAll) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Loader(Modifier.align(Alignment.Center))
        }
        return
    }

    if (viewState.status is DashboardUiStatus.Error) {
        ErrorBlock(
            modifier = Modifier.fillMaxSize(),
            message = stringResource(R.string.error_while_loading_occurred)
        ) {
            viewState.status.lastAction()
        }
        return
    }

    val allTransactions = viewState.allTransactions
    val selectedCategories = viewState.selectedCategories
    val selectedTransactionType = viewState.selectedTransactionType
    val selectedCurrency = viewState.selectedCurrency

    val filteredTransactions by remember(
        allTransactions,
        selectedCategories,
        selectedTransactionType
    ) {
        derivedStateOf {
            allTransactions
                .filter { it.mainCategory in selectedCategories }
                .filter { tx ->
                    when {
                        selectedTransactionType.containsAll(TransactionType.entries) -> true
                        tx.value.amountMinor >= BigInteger("0") -> TransactionType.INCOMES in selectedTransactionType
                        else -> TransactionType.EXPENSES in selectedTransactionType
                    }
                }
        }
    }

    val totalIncomes by remember(filteredTransactions, selectedCurrency) {
        derivedStateOf {
            Money(
                filteredTransactions.filter { it.value.amountMinor > BigInteger("0") }
                    .sumOf { it.value.amountMinor },
                selectedCurrency?.currencyCode ?: ""
            )
        }
    }

    val totalExpenses by remember(filteredTransactions, selectedCurrency) {
        derivedStateOf {
            Money(
                filteredTransactions.filter { it.value.amountMinor < BigInteger("0") }
                    .sumOf { it.value.amountMinor },
                selectedCurrency?.currencyCode ?: ""
            )
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { paddingValues ->
            Column {
                if (viewState.availableCurrencies.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(viewState.availableCurrencies, key = { it }) { currency ->
                            val selected = viewState.selectedCurrency == currency
                            CurrencyItem(
                                modifier = Modifier,
                                isSelected = selected,
                                currency = currency,
                                onClick = {
                                    callViewModel(DashboardIntent.SelectCurrency(currency))
                                }
                            )
                        }
                    }
                }

                when (viewState.status) {
                    DashboardUiStatus.LoadingTransactions -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Loader(Modifier.align(Alignment.Center))
                        }
                    }

                    is DashboardUiStatus.Idle, is DashboardUiStatus.IdleDeletingTransaction -> {
                        if (allTransactions.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(
                                    bottom = (WindowInsets.navigationBars)
                                        .only(WindowInsetsSides.Bottom)
                                        .asPaddingValues()
                                        .calculateBottomPadding() + paddingValues.calculateBottomPadding()
                                )
                            ) {
                                item {
                                    DashboardTotal(
                                        modifier = Modifier.fillMaxWidth(),
                                        totalIncomes = totalIncomes,
                                        totalExpenses = totalExpenses
                                    )
                                }

                                item {
                                    ExpenseIncomeFilter(
                                        modifier = Modifier.fillMaxWidth(),
                                        selectedItems = selectedTransactionType,
                                        onItemClick = {
                                            callViewModel(
                                                DashboardIntent.ToggleTypeFilter(it)
                                            )
                                        },
                                        onSelectAllClick = {
                                            callViewModel(
                                                DashboardIntent.ToggleAllTypesFilter
                                            )
                                        }
                                    )
                                }

                                item {
                                    CategoriesFilter(
                                        modifier = Modifier.fillMaxWidth(),
                                        allCategories = viewState.allCategories,
                                        selectedItems = selectedCategories,
                                        onItemClick = {
                                            callViewModel(
                                                DashboardIntent.ToggleCategoriesFilter(it)
                                            )
                                        },
                                        onSelectAllClick = {
                                            callViewModel(
                                                DashboardIntent.ToggleAllCategoriesFilter
                                            )
                                        }
                                    )
                                }

                                if (filteredTransactions.isNotEmpty()) {
                                    item {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 16.dp, top = 40.dp)
                                                .fillMaxWidth(),
                                            text = stringResource(R.string.recent_transactions),
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }

                                    items(
                                        items = filteredTransactions,
                                        key = { it.uuid }
                                    ) { transaction ->
                                        val deletingTransaction =
                                            (viewState.status as? DashboardUiStatus.IdleDeletingTransaction)?.transaction

                                        ListTransactionItem(
                                            modifier = Modifier,
                                            transaction = transaction,
                                            isDeleting = deletingTransaction == transaction,
                                            onEditTransactionClick = onEditTransactionClick,
                                            onDeleteTransactionClick = {
                                                callViewModel(
                                                    DashboardIntent.DeleteTransaction(it)
                                                )
                                            }
                                        )
                                    }
                                } else {
                                    item {
                                        Text(
                                            modifier = Modifier.padding(top = 20.dp),
                                            textAlign = TextAlign.Center,
                                            text = stringResource(R.string.no_filtered_data),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = disableGrey
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    modifier = Modifier.size(60.dp),
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "",
                                    tint = disableGrey
                                )

                                Text(
                                    modifier = Modifier,
                                    text = stringResource(R.string.no_currency_transactions),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = disableGrey
                                )
                            }
                        }
                    }

                    else -> {
                        // Handle other states if needed
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Show FAB when not in error or loading states
                if (viewState.status !is DashboardUiStatus.Error &&
                    viewState.status != DashboardUiStatus.LoadingAll
                ) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(
                                (WindowInsets.navigationBars)
                                    .only(WindowInsetsSides.Bottom)
                                    .asPaddingValues()
                            )
                            .padding(20.dp),
                        shape = RoundedCornerShape(12.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = onAddTransactionClick,
                        content = {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Add transaction"
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ListTransactionItem(
    modifier: Modifier,
    transaction: Transaction,
    isDeleting: Boolean,
    onEditTransactionClick: (transactionId: String) -> Unit,
    onDeleteTransactionClick: (transaction: Transaction) -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) onEditTransactionClick(transaction.uuid)
            else if (it == SwipeToDismissBoxValue.EndToStart) onDeleteTransactionClick(transaction)

            false
        }
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeToDismissBoxState,
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(notificationRedLight)
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = notificationRedDark
                    )
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit item",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .wrapContentSize(Alignment.CenterStart)
                            .padding(12.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                else -> {}
            }
        },
        content = {
            TransactionCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                transaction = transaction,
                isDeleting = isDeleting,
                onEditClick = { onEditTransactionClick(it) }
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        Content(
            viewState = DashboardViewState(
                status = DashboardUiStatus.Idle,
                availableCurrencies = Currency.getAvailableCurrencies().toList(),
                selectedCurrency = Currency.getInstance("USD"),
                allTransactions = listOf(PreviewMocks.transaction)
            ),
            callViewModel = {},
            onAddTransactionClick = {},
            onEditTransactionClick = { _ -> }
        )
    }
}
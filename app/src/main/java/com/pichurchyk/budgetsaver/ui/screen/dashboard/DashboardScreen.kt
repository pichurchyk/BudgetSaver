package com.pichurchyk.budgetsaver.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.ui.common.ErrorBlock
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.screen.dashboard.chart.TransactionsDashboardLineChart
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.CategoriesFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.ExpenseIncomeFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.total.DashboardTotal
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.CurrenciesUiStatus
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardIntent
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewState
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.TransactionsUiStatus
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import com.pichurchyk.budgetsaver.ui.theme.notificationRedDark
import com.pichurchyk.budgetsaver.ui.theme.notificationRedLight
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.math.BigInteger
import java.util.Currency

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    openEditTransactionScreen: (transactionId: String) -> Unit,
    openAddTransactionScreen: () -> Unit
) {
    val viewState by viewModel.state.collectAsState()

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
    val scrollState = rememberLazyListState()
    val coroutineContext = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        callViewModel(DashboardIntent.Init)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { paddingValues ->
            Column {
                when (viewState.currenciesStatus) {
                    CurrenciesUiStatus.Idle -> {
                        ScrollableTabRow(
                            modifier = Modifier.height(60.dp),
                            divider = {},
                            containerColor = MaterialTheme.colorScheme.background,
                            selectedTabIndex = viewState.availableCurrencies.indexOf(viewState.selectedCurrency),
                        ) {
                            viewState.availableCurrencies.forEachIndexed { index, currency ->
                                Tab(
                                    selected = viewState.selectedCurrency == currency,
                                    onClick = {
                                        coroutineContext.launch {
                                            scrollState.animateScrollToItem(0)
                                        }
                                        callViewModel(DashboardIntent.SelectCurrency(currency))
                                    },
                                    text = {
                                        Text(
                                            text = currency,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = disableGrey
                                )
                            }
                        }
                    }

                    is CurrenciesUiStatus.Error -> {}
                    CurrenciesUiStatus.Loading -> {
                        Loader(
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        )
                    }
                }

                when (viewState.transactionsStatus) {
                    TransactionsUiStatus.Idle -> {
                        Column {
                            viewState.transactions?.find { it.currencyCode == viewState.selectedCurrency }
                                ?.let { activeData ->
                                    if (activeData.transactions.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 16.dp)
                                                .verticalScroll(rememberScrollState()),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                        ) {
                                            ExpenseIncomeFilter(
                                                modifier = Modifier.fillMaxWidth(),
                                                selectedItems = activeData.selectedTransactionType,
                                                onItemClick = {
                                                    callViewModel(
                                                        DashboardIntent.ToggleTypeFilter(it)
                                                    )
                                                },
                                                onSelectAllClick = {
                                                    callViewModel(DashboardIntent.ToggleAllTypesFilter)
                                                }
                                            )

                                            CategoriesFilter(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                allCategories = activeData.allCategories,
                                                selectedItems = activeData.selectedCategories,
                                                onItemClick = {
                                                    callViewModel(
                                                        DashboardIntent.ToggleCategoriesFilter(it)
                                                    )
                                                },
                                                onSelectAllClick = {
                                                    callViewModel(DashboardIntent.ToggleAllCategoriesFilter)
                                                }
                                            )

                                            val totalIncomes = activeData.totalIncomes
                                            val totalExpenses = activeData.totalExpenses

                                            DashboardTotal(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                totalIncomes = totalIncomes,
                                                totalExpenses = totalExpenses
                                            )

//                                            TransactionsDashboardLineChart(
//                                                modifier = Modifier,
//                                                transactions = activeData.filteredTransactionsWithCurrency.reversed(),
//                                            )

                                            if (activeData.filteredTransactionsWithCurrency.isNotEmpty()) {
                                                Column(
                                                    modifier = Modifier.padding(
                                                        bottom = paddingValues.calculateBottomPadding(),
                                                        top = 40.dp
                                                    ),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(start = 16.dp)
                                                            .fillMaxWidth(),
                                                        text = stringResource(R.string.recent_transactions),
                                                        textAlign = TextAlign.Start,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )

                                                    activeData.filteredTransactionsWithCurrency.forEach { transaction ->
                                                        ListTransactionItem(
                                                            modifier = Modifier,
                                                            transaction = transaction,
                                                            onEditTransactionClick = onEditTransactionClick,
                                                            onDeleteTransactionClick = {
                                                                callViewModel(
                                                                    DashboardIntent.DeleteTransaction(
                                                                        it
                                                                    )
                                                                )
                                                            }
                                                        )
                                                    }
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
                                                text = stringResource(R.string.no_data_available),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = disableGrey
                                            )
                                        }
                                    }
                                }
                        }
                    }

                    is TransactionsUiStatus.Loading -> {
                        Box(
                            Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Loader(Modifier.align(Alignment.Center))
                        }
                    }

                    is TransactionsUiStatus.Error -> {
                        ErrorBlock(
                            modifier = Modifier
                                .fillMaxSize(),
                            message = stringResource(R.string.error_while_loading_occurred)
                        ) {
                            viewState.transactionsStatus.lastAction.invoke()
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (viewState.transactionsStatus !is TransactionsUiStatus.Error) {
                    FloatingActionButton(
                        modifier = Modifier.padding(20.dp),
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
                transactionsStatus = TransactionsUiStatus.Idle,
                currenciesStatus = CurrenciesUiStatus.Idle,
                availableCurrencies = Currency.getAvailableCurrencies().map { it.currencyCode },
                selectedCurrency = "USD",
                transactions = listOf(
                    PreviewMocks.transactionByCurrency,
                    PreviewMocks.transactionByCurrency
                )
            ),
            callViewModel = {},
            onAddTransactionClick = {},
            onEditTransactionClick = { _ -> }
        )
    }
}
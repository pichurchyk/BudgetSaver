package com.pichurchyk.budgetsaver.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.ui.common.ErrorBlock
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.screen.dashboard.chart.TransactionsDashboardLineChart
import com.pichurchyk.budgetsaver.ui.screen.dashboard.chart.TransactionsDashboardRateChart
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.CategoriesFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.filter.ExpenseIncomeFilter
import com.pichurchyk.budgetsaver.ui.screen.dashboard.total.DashboardTotal
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardIntent
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewState
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.TransactionsWithFilters
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.koin.androidx.compose.koinViewModel
import java.math.BigInteger

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
        onEditTransactionClick = openEditTransactionScreen
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
        callViewModel.invoke(DashboardIntent.LoadData)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.dashboard),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background.copy(0f)
                    ),
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            when (viewState) {
                is DashboardViewState.Init -> {
                    // Handle Init state
                }

                is DashboardViewState.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Loader(Modifier.align(Alignment.Center))
                    }
                }

                is DashboardViewState.Loaded -> {
                    Column {
                        PrimaryTabRow(
                            divider = {},
                            containerColor = MaterialTheme.colorScheme.background,
                            selectedTabIndex = viewState.indexOfSelectedCurrency,
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                        ) {
                            viewState.allCurrencies.forEachIndexed { index, currency ->
                                Tab(
                                    selected = viewState.indexOfSelectedCurrency == index,
                                    onClick = {
                                        coroutineContext.launch {
                                            scrollState.animateScrollToItem(0)
                                        }
                                        callViewModel.invoke(DashboardIntent.SelectCurrency(currency))
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

                        viewState.sortedTransactionsBySelectedCurrency?.let { activeData ->
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                state = scrollState,
                                contentPadding = PaddingValues(
                                    bottom = paddingValues.calculateBottomPadding()
                                ),
                            ) {
                                item {
                                    ExpenseIncomeFilter(
                                        modifier = Modifier.fillMaxWidth(),
                                        selectedItems = activeData.selectedTransactionType,
                                        onItemClick = {
                                            callViewModel.invoke(DashboardIntent.ToggleTypeFilter(it))
                                        },
                                        onSelectAllClick = {
                                            callViewModel.invoke(DashboardIntent.ToggleAllTypesFilter)
                                        }
                                    )
                                }

                                item {
                                    CategoriesFilter(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        allCategories = activeData.transactions.allCategories,
                                        selectedItems = activeData.selectedCategories,
                                        onItemClick = {
                                            callViewModel.invoke(
                                                DashboardIntent.ToggleCategoriesFilter(
                                                    it
                                                )
                                            )
                                        },
                                        onSelectAllClick = {
                                            callViewModel.invoke(DashboardIntent.ToggleAllCategoriesFilter)
                                        }
                                    )
                                }

                                item {
                                    val totalIncomes =
                                        activeData.filteredTransactionsWithCurrency.totalIncomes
                                    val totalExpenses =
                                        activeData.filteredTransactionsWithCurrency.totalExpenses

                                    if (totalExpenses.amountMinor == BigInteger("0") &&
                                        totalIncomes.amountMinor == BigInteger("0")
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(top = 10.dp),
                                            text = stringResource(R.string.no_data_available),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                item {
                                    DashboardTotal(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        totalIncomes = activeData.filteredTransactionsWithCurrency.totalIncomes,
                                        totalExpenses = activeData.filteredTransactionsWithCurrency.totalExpenses
                                    )

                                    TransactionsDashboardLineChart(
                                        modifier = Modifier.padding(4.dp),
                                        transactions = activeData
                                            .filteredTransactionsWithCurrency
                                            .transactions
                                            .reversed(),
                                    )
                                }

                                item {
                                    TransactionsDashboardRateChart(
                                        transactions = activeData.filteredTransactionsWithCurrency.transactions,
                                        modifier = Modifier,
                                    )
                                }

                                if (activeData.filteredTransactionsWithCurrency.transactions.isNotEmpty()) {
                                    item {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .fillMaxWidth(),
                                            text = stringResource(R.string.recent_transactions),
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }

                                    items(
                                        items = activeData.filteredTransactionsWithCurrency.transactions,
                                        key = { it.uuid }) { transactionData ->
                                        TransactionCard(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            transaction = transactionData,
                                            onEditClick = { onEditTransactionClick(it) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is DashboardViewState.Error -> {
                    ErrorBlock(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        message = stringResource(R.string.error_while_loading_occurred)
                    ) {
                        callViewModel.invoke(DashboardIntent.LoadData)
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (viewState is DashboardViewState.Loaded) {
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
@Preview
private fun Preview() {
    AppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Content(
                viewState = DashboardViewState.Loaded(
                    listOf(
                        TransactionsWithFilters(
                            transactions =
                                TransactionsByCurrency(
                                    transactions = listOf(
                                        Transaction(
                                            uuid = "",
                                            title = "Bus ticket",
                                            value = Money(
                                                amountMinor = BigInteger("0"),
                                                currency = "USD"
                                            ),
                                            notes = "Grocery shopping at local market",
                                            date = TransactionDate(
                                                dateInstant = Instant.fromEpochMilliseconds(
                                                    1748198228000
                                                ),
                                                timeZone = TimeZone.UTC
                                            ),
                                            mainCategory = TransactionCategory(
                                                title = "Food",
                                                emoji = "🍎",
                                                color = "#FF00FF",
                                                uuid = "123"
                                            ),
                                            subCategory = listOf(
                                                TransactionSubCategory(
                                                    title = "Groceries",
                                                    color = "#FF00F0"
                                                )
                                            )
                                        )
                                    ),
                                    currencyCode = "USD"
                                ),
                            listOf(),
                            listOf()
                        ),
                        TransactionsWithFilters(
                            transactions =
                                TransactionsByCurrency(
                                    transactions = listOf(
                                        Transaction(
                                            uuid = "",
                                            title = "Bus ticket",
                                            value = Money(
                                                amountMinor = BigInteger("0"),
                                                currency = "BYN"
                                            ),
                                            notes = "Grocery shopping at local market",
                                            date = TransactionDate(
                                                dateInstant = Instant.fromEpochMilliseconds(
                                                    1748198228000
                                                ),
                                                timeZone = TimeZone.UTC
                                            ),
                                            mainCategory = TransactionCategory(
                                                title = "Food",
                                                emoji = "🍎",
                                                color = "#FF00DS",
                                                uuid = "123"
                                            ),
                                            subCategory = listOf(
                                                TransactionSubCategory(
                                                    title = "Groceries",
                                                    color = "#FF00F0"
                                                )
                                            )
                                        )
                                    ),
                                    currencyCode = "BYN"
                                ),
                            listOf(),
                            listOf()
                        )
                    ),
                    selectedCurrency = "USD",
                ),
                callViewModel = {},
                onAddTransactionClick = {},
                onEditTransactionClick = { _ -> }
            )
        }
    }
}
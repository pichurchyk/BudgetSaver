package com.pichurchyk.budgetsaver.ui.screen.transaction.details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel.TransactionDetailsIntent
import com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel.TransactionDetailsUiStatus
import com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel.TransactionDetailsViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel.TransactionDetailsViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigInteger
import java.util.Currency

@Composable
fun TransactionDetailsScreen(
    initialTransactionId: String,
    closeScreen: () -> Unit,
    viewModel: TransactionDetailsViewModel = koinViewModel(
        parameters = { parametersOf(initialTransactionId) }
    )
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        viewState = viewState,
        callViewModel = { viewModel.handleIntent(it) },
        closeScreen = closeScreen
    )
}

@Composable
private fun Content(
    viewState: TransactionDetailsViewState,
    callViewModel: (TransactionDetailsIntent) -> Unit,
    closeScreen: () -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState.isScrollInProgress) {
        if (viewState.transactions.isNotEmpty() && !listState.isScrollInProgress) {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset

            val snapIndex = if (offset > 150) firstVisible + 1 else firstVisible

            coroutineScope.launch {
                listState.animateScrollToItem(snapIndex.coerceIn(0, viewState.transactions.lastIndex))
            }
        }
    }

    LazyColumn(
        state = listState,
    ) {
        when (viewState.uiStatus) {
            is TransactionDetailsUiStatus.Idle -> {
                itemsIndexed(viewState.transactions) { index, transaction ->
                    TransactionCard(
                        transaction = transaction,
                        modifier = Modifier
                    )
                }
            }

            else -> {}
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Content(
            viewState = TransactionDetailsViewState(
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
                currentTransaction = null,
            ),
            callViewModel = {},
            closeScreen = {}
        )
    }
}
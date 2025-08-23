package com.pichurchyk.budgetsaver.ui.screen.currency

import androidx.activity.result.launch
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.currency.CurrencyItem
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorIntent
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorUiStatus
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Currency

@Composable
fun FavoriteCurrenciesSelector(
    modifier: Modifier,
    viewModel: FavoriteCurrenciesSelectorViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        modifier = modifier,
        viewState = viewState,
        callViewModel = { viewModel.handleIntent(it) })
}

@Composable
private fun Content(
    modifier: Modifier,
    viewState: FavoriteCurrenciesSelectorViewState,
    callViewModel: (FavoriteCurrenciesSelectorIntent) -> Unit
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.primary_currency),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            modifier = Modifier.padding(bottom = 4.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.primary_currency_description),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
        )

        CommonInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            ,
            placeholder = stringResource(R.string.search),
            value = viewState.searchValue
        ) {
            callViewModel(FavoriteCurrenciesSelectorIntent.Search(it))
        }

        when (viewState.status) {
            is FavoriteCurrenciesSelectorUiStatus.Idle, is FavoriteCurrenciesSelectorUiStatus.Error -> {

                val (selectedCurrencies, unselectedCurrencies) = remember(
                    viewState.selectedCurrencies,
                    viewState.filteredCurrencies
                ) {
                    val selected = viewState.selectedCurrencies
                    val unselected = viewState.filteredCurrencies.filterNot { selected.contains(it) }
                    selected to unselected
                }

                LazyRow(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(100.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedCurrencies.isNotEmpty()) {
                        item(key = "selected_block") {
                            Column(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .animateItem()
                            ) {
                                Text(
                                    text = stringResource(R.string.selected),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .animateContentSize(
                                            animationSpec = tween(
                                                durationMillis = 300
                                            )
                                        )
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(0.05f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary.copy(0.4f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        viewState.selectedCurrencies.forEach { currency ->
                                            CurrencyItem(
                                                isSelected = true,
                                                currency = currency,
                                                onClick = {
                                                    callViewModel.invoke(
                                                        FavoriteCurrenciesSelectorIntent.UnselectCurrency(
                                                            currency
                                                        )
                                                    )
                                                })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(
                        unselectedCurrencies,
                    ) { currency ->
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .animateItem()
                        ) {
                            CurrencyItem(
                                modifier = Modifier.padding(top = 18.dp),
                                isSelected = false, currency = currency, onClick = {
                                    callViewModel.invoke(
                                        FavoriteCurrenciesSelectorIntent.SelectCurrency(
                                            currency
                                        )
                                    )
                                })
                        }
                    }
                }
            }

            is FavoriteCurrenciesSelectorUiStatus.Loading -> {
//                CurrencyButtonLoader(
//                    modifier = Modifier
//                        .height(40.dp)
//                        .width(80.dp)
//                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        Content(
            modifier = Modifier
                .width(500.dp)
                .aspectRatio(1f),
            viewState = FavoriteCurrenciesSelectorViewState(
                status = FavoriteCurrenciesSelectorUiStatus.Idle,
                allCurrencies = Currency.getAvailableCurrencies().toList(),
                selectedCurrencies = listOf(Currency.getInstance("USD"))
            ),
            callViewModel = {})
    }
}
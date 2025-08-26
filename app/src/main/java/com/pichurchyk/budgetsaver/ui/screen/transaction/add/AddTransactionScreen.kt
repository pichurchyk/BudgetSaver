package com.pichurchyk.budgetsaver.ui.screen.transaction.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.TransactionTypeChip
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationAction
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationEvent
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationType
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.screen.category.CategoryButton
import com.pichurchyk.budgetsaver.ui.screen.category.selector.CategorySelector
import com.pichurchyk.budgetsaver.ui.common.currency.CurrencyButton
import com.pichurchyk.budgetsaver.ui.screen.currency.CurrencySelector
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionIntent
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionUiStatus
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionValidationError
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionViewState
import org.koin.androidx.compose.koinViewModel

private enum class BottomSheetState {
    NONE, CATEGORY, CURRENCY
}

@Composable
fun AddTransactionScreen(
    closeScreen: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel(),
) {
    val focusManager = LocalFocusManager.current

    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewState.status) {
        when (val uiStatus = viewState.status) {
            AddTransactionUiStatus.Success -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(R.string.transaction_saved),
                        type = NotificationType.SUCCESS,
                    )
                )
            }

            is AddTransactionUiStatus.Error -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(uiStatus.error.asErrorMessage()),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.retry),
                            action = {
                                uiStatus.lastAction.invoke()

                                viewModel.handleIntent(AddTransactionIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            is AddTransactionUiStatus.ValidationError -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(R.string.fill_require_fields),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.dismiss),
                            action = {
                                viewModel.handleIntent(AddTransactionIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            AddTransactionUiStatus.Idle, AddTransactionUiStatus.Loading -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Content(
            viewState = viewState,
            callViewModel = { viewModel.handleIntent(it) },
            closeScreen = closeScreen,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewState: AddTransactionViewState,
    callViewModel: (AddTransactionIntent) -> Unit,
    closeScreen: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var modalBottomSheetState by remember { mutableStateOf(BottomSheetState.NONE) }

    val transactionData = viewState.transaction
    val isLoading = viewState.status is AddTransactionUiStatus.Loading

    val focusRequester = remember { FocusRequester() }

    var isInitialFocusTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(viewState.status) {
        if (viewState.status == AddTransactionUiStatus.Idle && !isInitialFocusTriggered) {
            focusRequester.requestFocus()

            isInitialFocusTriggered = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0.dp),
                title = {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.add_transaction),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background.copy(0f)
                ),
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                stringResource(R.string.back)
                            )
                        },
                        onClick = closeScreen,
                    )
                }
            )
        },
        content = { paddingValues ->
            when (modalBottomSheetState) {
                BottomSheetState.CATEGORY -> {
                    val selectedValues =
                        transactionData.mainCategory?.let { listOf(it) } ?: emptyList()
                    ModalBottomSheet(
                        modifier = Modifier,
                        sheetState = sheetState,
                        onDismissRequest = { modalBottomSheetState = BottomSheetState.NONE },
                        content = {
                            CategorySelector(
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                selectedValues = selectedValues,
                                onValuesSelected = {
                                    callViewModel.invoke(AddTransactionIntent.ChangeCategory(it.firstOrNull())) // Use firstOrNull for safety
                                    modalBottomSheetState = BottomSheetState.NONE
                                },
                                isMultiSelect = false,
                                isNullable = true
                            )
                        }
                    )
                }

                BottomSheetState.CURRENCY -> {
                    ModalBottomSheet(
                        modifier = Modifier,
                        sheetState = sheetState,
                        onDismissRequest = { modalBottomSheetState = BottomSheetState.NONE },
                        content = {
                            CurrencySelector(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                selectedCurrency = transactionData.currency,
                                searchValue = viewState.currenciesSearch,
                                currencies = viewState.filteredCurrencies,
                                onSearchValueChanged = {
                                    callViewModel.invoke(AddTransactionIntent.SearchCurrency(it))
                                },
                                onValueSelected = {
                                    callViewModel.invoke(AddTransactionIntent.ChangeCurrency(it))
                                    modalBottomSheetState = BottomSheetState.NONE
                                }
                            )
                        }
                    )
                }

                BottomSheetState.NONE -> { /* No sheet visible */
                }
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Type Chips
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TransactionType.entries.forEach { type ->
                        val isSelected = viewState.transaction.type == type
                        TransactionTypeChip(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            isSelected = isSelected,
                            value = type,
                            onClick = {
                                if (!isLoading) {
                                    callViewModel.invoke(AddTransactionIntent.ChangeType(type))
                                }
                            }
                        )
                    }
                }

                CommonInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.title),
                    value = transactionData.title,
                    placeholder = transactionData.mainCategory?.let { "${transactionData.type.getTitle()} (${transactionData.mainCategory.title})" },
                ) {
                    callViewModel.invoke(AddTransactionIntent.ChangeTitle(it))
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommonInput(
                        modifier = Modifier
                            .weight(3f)
                            .focusRequester(focusRequester),
                        headline = stringResource(R.string.amount),
                        isOptional = false,
                        keyboardType = KeyboardType.Decimal,
                        value = if (transactionData.value.toDoubleOrNull() == 0.0 && transactionData.value.isNotEmpty()) "" else transactionData.value,
                        error = viewState.validationError.contains(AddTransactionValidationError.EMPTY_AMOUNT),
                    ) {
                        callViewModel.invoke(AddTransactionIntent.ChangeValue(it))
                    }

                    CurrencyButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .padding(bottom = 2.dp)
                            .clickable {
                                if (!isLoading) {
                                    modalBottomSheetState = BottomSheetState.CURRENCY
                                }
                            },
                        value = transactionData.currency.currencyCode
                    )

                    CategoryButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 2.dp)
                            .clickable(enabled = !isLoading) {
                                modalBottomSheetState = BottomSheetState.CATEGORY
                            },
                        value = transactionData.mainCategory
                    )
                }

                CommonInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    height = 150.dp,
                    headline = stringResource(R.string.comments),
                    value = transactionData.notes,
                ) {
                    callViewModel.invoke(AddTransactionIntent.ChangeNotes(it))
                }
            }
        },
        bottomBar = {
            when (viewState.status) {
                is AddTransactionUiStatus.Loading -> {
                    Loader(modifier = Modifier)
                }

                else -> {
                    CommonButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                (WindowInsets.navigationBars)
                                    .only(WindowInsetsSides.Bottom)
                                    .asPaddingValues()
                            )
                            .padding(horizontal = 16.dp),
                        value = stringResource(R.string.submit),
                        onClick = {
                            if (!isLoading) {
                                callViewModel.invoke(AddTransactionIntent.Submit)
                            }
                        }
                    )
                }
            }
        }
    )
}
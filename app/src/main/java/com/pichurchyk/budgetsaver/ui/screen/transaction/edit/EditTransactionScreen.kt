package com.pichurchyk.budgetsaver.ui.screen.transaction.edit

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.platform.LocalContext
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
import com.pichurchyk.budgetsaver.ui.common.TwoOptionsSelector
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationAction
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationEvent
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationType
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.screen.category.CategoryButton
import com.pichurchyk.budgetsaver.ui.screen.category.CategorySelector
import com.pichurchyk.budgetsaver.ui.screen.currency.CurrencyButton
import com.pichurchyk.budgetsaver.ui.screen.currency.CurrencySelector
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionAction
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionIntent
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionUiStatus
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionValidationError
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionViewState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private enum class BottomSheetState {
    NONE, CATEGORY, CURRENCY, DELETE_TRANSACTION
}

@Composable
fun EditTransactionScreen(
    transactionId: String,
    closeScreen: () -> Unit,
    viewModel: EditTransactionViewModel = koinViewModel(
        parameters = { parametersOf(transactionId) }
    ),
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        viewState = viewState,
        callViewModel = { viewModel.handleIntent(it) },
        closeScreen = closeScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewState: EditTransactionViewState,
    callViewModel: (EditTransactionIntent) -> Unit,
    closeScreen: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var modalBottomSheetState by remember { mutableStateOf(BottomSheetState.NONE) }

    val transactionData = viewState.transaction
    val isLoading = viewState.status is EditTransactionUiStatus.Loading

    val context = LocalContext.current

    LaunchedEffect(viewState.status) {
        when (val uiStatus = viewState.status) {
            is EditTransactionUiStatus.Success -> {
                val message = when (uiStatus.action) {
                    EditTransactionAction.EDIT -> context.getString(R.string.transaction_saved)
                    EditTransactionAction.DELETE -> context.getString(R.string.transaction_deleted)
                }

                NotificationController.sendEvent(
                    NotificationEvent(
                        message = message,
                        type = NotificationType.SUCCESS,
                    )
                )

                if (uiStatus.action == EditTransactionAction.DELETE) {
                    closeScreen()
                }
            }

            is EditTransactionUiStatus.Error -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(uiStatus.error.asErrorMessage()),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.retry),
                            action = {
                                uiStatus.lastAction.invoke()

                                callViewModel.invoke(EditTransactionIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            is EditTransactionUiStatus.ValidationError -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(R.string.fill_require_fields),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.dismiss),
                            action = {
                                callViewModel.invoke(EditTransactionIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            is EditTransactionUiStatus.Deleting -> {
                modalBottomSheetState = BottomSheetState.DELETE_TRANSACTION
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
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
                                stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = closeScreen,
                    )
                },
                actions = {
                    IconButton(
                        content = {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        },
                        onClick = {
                            callViewModel.invoke(EditTransactionIntent.Delete)
                        },
                    )
                }
            )
        },
        content = { paddingValues ->
            if (modalBottomSheetState != BottomSheetState.NONE) {
                ModalBottomSheet(
                    modifier = Modifier,
                    sheetState = sheetState,
                    onDismissRequest = {
                        modalBottomSheetState = BottomSheetState.NONE
                        if (viewState.status == EditTransactionUiStatus.Deleting) {
                            callViewModel.invoke(EditTransactionIntent.CancelDelete)
                        }
                    },
                    content = {
                        when (modalBottomSheetState) {
                            BottomSheetState.CATEGORY -> {
                                val selectedValues =
                                    transactionData.mainCategory?.let { listOf(it) } ?: emptyList()

                                CategorySelector(
                                    modifier = Modifier
                                        .padding(bottom = 6.dp)
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    selectedValues = selectedValues,
                                    onValuesSelected = {
                                        callViewModel.invoke(EditTransactionIntent.ChangeCategory(it.firstOrNull())) // Use firstOrNull for safety
                                        modalBottomSheetState = BottomSheetState.NONE
                                    },
                                    isMultiSelect = false,
                                    isNullable = true
                                )
                            }

                            BottomSheetState.CURRENCY -> {
                                CurrencySelector(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    selectedCurrency = transactionData.currency,
                                    searchValue = viewState.currenciesSearch,
                                    currencies = viewState.filteredCurrencies,
                                    onSearchValueChanged = {
                                        callViewModel.invoke(EditTransactionIntent.SearchCurrency(it))
                                    },
                                    onValueSelected = {
                                        callViewModel.invoke(EditTransactionIntent.ChangeCurrency(it))
                                        modalBottomSheetState = BottomSheetState.NONE
                                    }
                                )
                            }

                            BottomSheetState.DELETE_TRANSACTION -> {
                                TwoOptionsSelector(
                                    modifier = Modifier,
                                    positiveText = stringResource(R.string.delete),
                                    negativeText = stringResource(R.string.cancel),
                                    title = stringResource(R.string.delete_question),
                                    onNegativeClick = {
                                        callViewModel.invoke(EditTransactionIntent.CancelDelete)
                                    },
                                    onPositiveClick = {
                                        callViewModel.invoke(EditTransactionIntent.SubmitDelete)
                                    }
                                )
                            }

                            else -> {}
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                                .height(40.dp)
                                .doOnClick {
                                    if (!isLoading) {
                                        callViewModel.invoke(EditTransactionIntent.ChangeType(type))
                                    }
                                },
                            isSelected = isSelected,
                            value = type
                        )
                    }
                }

                CommonInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.title),
                    value = transactionData.title,
                    error = viewState.validationError.contains(EditTransactionValidationError.EMPTY_TITLE),
                ) {
                    callViewModel.invoke(EditTransactionIntent.ChangeTitle(it))
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommonInput(
                        modifier = Modifier.weight(3f),
                        headline = stringResource(R.string.amount),
                        keyboardType = KeyboardType.Decimal,
                        value = if (transactionData.value.toDoubleOrNull() == 0.0 && transactionData.value.isNotEmpty()) "" else transactionData.value,
                        error = viewState.validationError.contains(EditTransactionValidationError.EMPTY_AMOUNT),
                    ) {
                        callViewModel.invoke(EditTransactionIntent.ChangeValue(it))
                    }

                    CurrencyButton(
                        modifier = Modifier
                            .weight(1f)
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
                        error = viewState.validationError.contains(EditTransactionValidationError.EMPTY_CATEGORY),
                        value = transactionData.mainCategory
                    )
                }

                CommonInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    height = 150.dp,
                    headline = stringResource(R.string.comments),
                    value = transactionData.notes,
                ) {
                    callViewModel.invoke(EditTransactionIntent.ChangeNotes(it))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (viewState.status) {
                    is EditTransactionUiStatus.Loading -> {
                        Loader(modifier = Modifier)
                    }

                    else -> {
                        CommonButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = stringResource(R.string.submit),
                            onClick = {
                                if (!isLoading) {
                                    callViewModel.invoke(EditTransactionIntent.Submit)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
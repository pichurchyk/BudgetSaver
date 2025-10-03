package com.pichurchyk.budgetsaver.ui.screen.preset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.common.TransactionTypeChip
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.common.currency.CurrencyButton
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationAction
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationEvent
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationType
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.ext.imePaddingWithoutNavBars
import com.pichurchyk.budgetsaver.ui.screen.category.selector.CategorySelector
import com.pichurchyk.budgetsaver.ui.screen.currency.CurrencySelector
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetIntent
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetUiStatus
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetValidationError
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetViewModel
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetViewState
import com.pichurchyk.budgetsaver.ui.screen.transaction.TransactionValueInput
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import org.koin.androidx.compose.koinViewModel
import java.util.Currency

private enum class BottomSheetState {
    NONE, CATEGORY, CURRENCY
}

@Composable
fun AddPresetScreen(
    closeScreen: () -> Unit,
    viewModel: AddPresetViewModel = koinViewModel(),
) {
    val focusManager = LocalFocusManager.current

    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewState.status) {
        when (val uiStatus = viewState.status) {
            AddPresetUiStatus.Success -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(R.string.transaction_saved),
                        type = NotificationType.SUCCESS,
                    )
                )
            }

            is AddPresetUiStatus.Error -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(uiStatus.error.asErrorMessage()),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.retry),
                            action = {
                                uiStatus.lastAction.invoke()
                                viewModel.handleIntent(AddPresetIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            is AddPresetUiStatus.ValidationError -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(R.string.fill_require_fields),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.dismiss),
                            action = {
                                viewModel.handleIntent(AddPresetIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            AddPresetUiStatus.Idle, AddPresetUiStatus.Loading -> {}
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
            focusManager = focusManager
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun Content(
    viewState: AddPresetViewState,
    callViewModel: (AddPresetIntent) -> Unit,
    focusManager: FocusManager,
    closeScreen: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var modalBottomSheetState by remember { mutableStateOf(BottomSheetState.NONE) }

    val transactionData = viewState.preset
    val isLoading = viewState.status is AddPresetUiStatus.Loading

    var pendingSheet by remember { mutableStateOf<BottomSheetState?>(null) }
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible && pendingSheet != null) {
            modalBottomSheetState = pendingSheet!!
            pendingSheet = null
        }
    }

    fun showBottomSheet(sheetType: BottomSheetState) {
        focusManager.clearFocus()
        if (!isKeyboardVisible) {
            modalBottomSheetState = sheetType
        } else {
            pendingSheet = sheetType
        }
    }

    Scaffold(
        modifier = Modifier.imePaddingWithoutNavBars(),
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
                                    callViewModel.invoke(AddPresetIntent.ChangeCategory(it.firstOrNull()))
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
                                    callViewModel.invoke(AddPresetIntent.SearchCurrency(it))
                                },
                                onValueSelected = {
                                    callViewModel.invoke(AddPresetIntent.ChangeCurrency(it))
                                    modalBottomSheetState = BottomSheetState.NONE
                                }
                            )
                        }
                    )
                }

                BottomSheetState.NONE -> {}
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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
                        val isSelected = viewState.preset.type == type
                        TransactionTypeChip(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            isSelected = isSelected,
                            value = type,
                            onClick = {
                                if (!isLoading) {
                                    focusManager.clearFocus(true)
                                    callViewModel.invoke(AddPresetIntent.ChangeType(type))
                                }
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TransactionValueInput(
                        modifier = Modifier
                            .weight(1f),
                        value = transactionData.value.ifEmpty { "" },
                        error = viewState.validationError.contains(AddPresetValidationError.EMPTY_AMOUNT),
                        transactionType = viewState.preset.type
                    ) {
                        callViewModel.invoke(AddPresetIntent.ChangeValue(it))
                    }

                    CurrencyButton(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable {
                                showBottomSheet(BottomSheetState.CURRENCY)
                            },
                        value = transactionData.currency.currencyCode
                    )
                }

                CommonInput(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp),
                    headline = stringResource(R.string.title),
                    value = transactionData.title,
                    placeholder = transactionData.mainCategory?.let { "${transactionData.type.getTitle()} (${transactionData.mainCategory.title})" },
                ) {
                    callViewModel.invoke(AddPresetIntent.ChangeTitle(it))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val selectedCategory = viewState.preset.mainCategory

                    selectedCategory?.let {
                        TransactionCategoryChip(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(horizontal = 4.dp),
                            category = it,
                            isSelected = true
                        )
                    } ?: run {
                        Text(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(horizontal = 4.dp),
                            text = "No category",
                            color = disableGrey,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.animateContentSize(
                            animationSpec = tween()
                        )
                    ) {
                        CommonButton(
                            modifier = Modifier.padding(end = 16.dp),
                            value = stringResource(R.string.select_category),
                            onClick = {
                                showBottomSheet(BottomSheetState.CATEGORY)
                            }
                        )

                        AnimatedVisibility(
                            visible = selectedCategory != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(28.dp)
                                    .doOnClick {
                                        focusManager.clearFocus(true)
                                        callViewModel(AddPresetIntent.ChangeCategory(null))
                                    },
                                imageVector = Icons.Rounded.Clear,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = stringResource(R.string.clear_category)
                            )
                        }
                    }
                }


                CommonInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.comments),
                    value = transactionData.notes,
                ) {
                    callViewModel.invoke(AddPresetIntent.ChangeNotes(it))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (viewState.status) {
                    is AddPresetUiStatus.Loading -> {
                        Loader(modifier = Modifier)
                    }

                    else -> {
                        CommonButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = stringResource(R.string.submit),
                            onClick = {
                                if (!isLoading) {
                                    callViewModel.invoke(AddPresetIntent.Submit)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Content(
            viewState = AddPresetViewState(
                preset = PreviewMocks.transactionPresetCreation,
                allCurrencies = Currency.getAvailableCurrencies().toList(),
            ),
            callViewModel = {},
            closeScreen = {},
            focusManager = LocalFocusManager.current
        )
    }
}
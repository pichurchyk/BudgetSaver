package com.pichurchyk.budgetsaver.ui.screen.category.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryIntent
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel = koinViewModel(),
    closeScreen: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        viewState = viewState,
        closeScreen = closeScreen,
        callViewModel = {
            viewModel.handleIntent(it)
        }
    )
}

private enum class BottomSheetState {
    NONE,
    EMOJI_PICKER,
    COLOR_PICKER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewState: AddCategoryViewState,
    closeScreen: () -> Unit,
    callViewModel: (AddCategoryIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var modalBottomSheetState by remember { mutableStateOf(BottomSheetState.NONE) }

    LaunchedEffect(modalBottomSheetState) {
        when (modalBottomSheetState) {
            BottomSheetState.NONE -> {
                if (sheetState.isVisible) {
                    sheetState.hide()
                }
            }
            else -> {
                if (!sheetState.isVisible) {
                    sheetState.show()
                }
            }
        }
    }

    val showSheet = { sheetType: BottomSheetState ->
        modalBottomSheetState = sheetType
    }

    val hideSheet = {
        modalBottomSheetState = BottomSheetState.NONE
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0.dp),
                title = {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.add_category),
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                if (modalBottomSheetState != BottomSheetState.NONE) {
                    ModalBottomSheet(
                        modifier = Modifier,
                        sheetState = sheetState,
                        onDismissRequest = hideSheet,
                        content = {
                            when (modalBottomSheetState) {
                                BottomSheetState.EMOJI_PICKER -> {
                                    EmojiPicker(
                                        modifier = Modifier
                                            .padding(
                                                (WindowInsets.navigationBars)
                                                    .only(WindowInsetsSides.Bottom)
                                                    .asPaddingValues()
                                            ),
                                        emojisList = viewState.availableEmojis,
                                        onSelect = { emoji ->
                                            callViewModel.invoke(AddCategoryIntent.ChangeEmoji(emoji))
                                            hideSheet()
                                        },
                                        onSearchChanged = { searchValue ->
                                            callViewModel.invoke(AddCategoryIntent.ChangeSearchEmojiValue(searchValue))
                                        },
                                        search = viewState.searchEmojisValue
                                    )
                                }

                                BottomSheetState.COLOR_PICKER -> {
                                    ColorPicker(
                                        initialColor = Color.fromHex(viewState.model.color),
                                        onColorChanged = {
                                            callViewModel.invoke(AddCategoryIntent.ChangeColor(it.toHex()))
                                        }
                                    )
                                }

                                BottomSheetState.NONE -> {
                                    // This should never happen due to the outer if condition
                                }
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CommonInput(
                        modifier = Modifier.weight(1f),
                        value = viewState.model.title,
                        headline = stringResource(R.string.title),
                        isOptional = false,
                        onValueChanged = {
                            callViewModel.invoke(AddCategoryIntent.ChangeTitle(it))
                        }
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(100))
                            .background(Color.fromHex(viewState.model.color))
                            .doOnClick {
                                showSheet(BottomSheetState.COLOR_PICKER)
                            }
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(40.dp)
                            .doOnClick {
                                showSheet(BottomSheetState.EMOJI_PICKER)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .zIndex(1f),
                            text = viewState.model.emoji,
                            fontSize = 26.sp,
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.create_transaction_description),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.create_transaction_tip),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                CategoryPreview(
                    modifier = Modifier.padding(top = 26.dp, start = 16.dp, end = 16.dp),
                    transactionCategory = TransactionCategory(
                        uuid = "",
                        title = viewState.model.title,
                        emoji = viewState.model.emoji,
                        color = viewState.model.color
                    )
                )
            }
        },
        bottomBar = {
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
                onClick = {}
            )
        },
    )
}

@Composable
private fun EmojiPicker(
    modifier: Modifier,
    emojisList: List<Emoji>,
    onSelect: (String) -> Unit,
    search: String,
    onSearchChanged: (String) -> Unit
) {
    Column {
        CommonInput(
            value = search,
            modifier = modifier.padding(horizontal = 16.dp),
            placeholder = stringResource(R.string.search),
            onValueChanged = {
                onSearchChanged(it)
            }
        )

        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
            columns = GridCells.Adaptive(minSize = 60.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(emojisList) { emoji ->
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(100))
                        .clickable {
                            onSelect(emoji.emoji)
                        },
                    textAlign = TextAlign.Center,
                    text = emoji.emoji,
                    fontSize = 30.sp
                )
            }
        }
    }
}


@Composable
private fun ColorPicker(
    initialColor: Color,
    onColorChanged: (Color) -> Unit
) {
    val controller = rememberColorPickerController()

    HsvColorPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(10.dp),
        controller = controller,
        initialColor = initialColor,
        onColorChanged = { colorEnvelope: ColorEnvelope ->
            onColorChanged(colorEnvelope.color)
        }
    )
}

@Composable
private fun CategoryPreview(
    modifier: Modifier,
    transactionCategory: TransactionCategory
) {
    var isSelected by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(R.string.preview),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransactionCategoryChip(
                modifier = Modifier,
                category = TransactionCategory(
                    "",
                    title = transactionCategory.title.ifEmpty { stringResource(R.string.here_will_be_title) },
                    emoji = transactionCategory.emoji,
                    color = transactionCategory.color
                ),
                isSelected = isSelected,
                onItemClick = {
                    isSelected = !isSelected
                }
            )

            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(R.string.click_to_select_unselect),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        Content(
            viewState = AddCategoryViewState(
                model = TransactionCategoryCreation(
                    title = "Title",
                    color = "F19E2A",
                )
            ),
            callViewModel = {},
            closeScreen = {}
        )
    }
}
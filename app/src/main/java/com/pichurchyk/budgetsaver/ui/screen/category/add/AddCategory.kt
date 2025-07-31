package com.pichurchyk.budgetsaver.ui.screen.category.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.ext.random
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryIntent
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryViewState
import com.pichurchyk.budgetsaver.ui.screen.emojipicker.EmojiPicker
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import net.fellbaum.jemoji.EmojiGroup
import net.fellbaum.jemoji.EmojiManager
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCategory(
    modifier: Modifier,
    viewModel: AddCategoryViewModel = koinViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    Content(
        modifier = modifier,
        viewState = viewState,
        callViewModel = {
            viewModel.handleIntent(it)
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier,
    viewState: AddCategoryViewState,
    callViewModel: (AddCategoryIntent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var requestKeyboard by remember { mutableStateOf(false) }

    LaunchedEffect(requestKeyboard) {
        if (requestKeyboard) {
            focusRequester.requestFocus()
            keyboardController?.show()
            requestKeyboard = false
        }
    }

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Row(
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
                    .background(Color.random())
            )

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(36.dp)
                    .clip(RoundedCornerShape(100))
                    .clickable {
                        requestKeyboard = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.zIndex(1f),
                    text = viewState.model.emoji.ifEmpty { EmojiManager.getAllEmojis().random().emoji },
                    fontSize = 26.sp
                )

                TextField(
                    modifier = modifier
                        .size(1.dp)
                        .alpha(0f)
                        .zIndex(0f)
                        .focusRequester(focusRequester),
                    value = "",
                    onValueChange = {
                        if (EmojiManager.containsAnyEmoji(it)) {
                            callViewModel.invoke(AddCategoryIntent.ChangeEmoji(it))
                            keyboardController?.hide()
                        }
                    }
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
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            viewState = AddCategoryViewState(),
            callViewModel = {}
        )
    }
}
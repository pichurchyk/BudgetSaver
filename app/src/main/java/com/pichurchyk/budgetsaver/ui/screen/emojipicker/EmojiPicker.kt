package com.pichurchyk.budgetsaver.ui.screen.emojipicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getEmoji
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import net.fellbaum.jemoji.EmojiGroup
import net.fellbaum.jemoji.EmojiManager

@Composable
fun EmojiPicker(
    modifier: Modifier,
    onEmojiSelected: (String) -> Unit
) {

    var selectedGroup by remember { mutableStateOf(EmojiGroup.entries.first()) }
    var search by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            EmojiGroup.entries.forEach { group ->
                val isSelected = selectedGroup == group
                val color =
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                Column(
                    modifier = Modifier.doOnClick {
                        selectedGroup = group
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = group.getEmoji(),
                        fontSize = 18.sp
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .background(color)
                            .height(2.dp)
                            .width(20.dp)
                            .clip(RoundedCornerShape(100))
                    )
                }
            }
        }

        CommonInput(
            modifier = Modifier.padding(top = 10.dp),
            value = search,
            placeholder = stringResource(R.string.search),
            onValueChanged = {

                println()
            }
        )

        LazyHorizontalGrid(
            modifier = Modifier.padding(top = 10.dp),
            rows = GridCells.Adaptive(minSize = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val list = EmojiManager.getAllEmojisByGroup(selectedGroup).toList()

            items(list) { item ->
                Text(
                    modifier = Modifier.clickable {
                        onEmojiSelected(item.emoji)
                    },
                    text = item.emoji,
                    fontSize = 32.sp,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        EmojiPicker(
            modifier = Modifier.height(400.dp)
        ) { }
    }
}
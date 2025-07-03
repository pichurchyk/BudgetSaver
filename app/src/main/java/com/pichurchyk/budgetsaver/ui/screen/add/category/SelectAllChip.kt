package com.pichurchyk.budgetsaver.ui.screen.add.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@Composable
fun SelectAllChip(
    modifier: Modifier,
    isAllSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isAllSelected) {
        MaterialTheme.colorScheme.primary.copy(0.1f)
    } else {
        disableGrey.copy(0.1f)
    }

    val textColor = if (isAllSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .doOnClick(onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = stringResource(R.string.all),
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}

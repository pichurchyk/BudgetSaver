package com.pichurchyk.budgetsaver.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.ext.getColor
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@Composable
fun TransactionTypeChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    value: TransactionType,
    onClick: (TransactionType) -> Unit
) {
    val textColor = if (isSelected) {
        value.getColor()
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val bgColor = if (isSelected) {
        value.getColor().copy(0.1f)
    } else {
        disableGrey.copy(0.1f)
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(100))
            .background(bgColor, RoundedCornerShape(100))
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .clickable { onClick(value) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.getTitle(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            maxLines = 1
        )
    }
}
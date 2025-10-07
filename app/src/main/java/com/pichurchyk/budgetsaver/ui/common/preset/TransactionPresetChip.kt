package com.pichurchyk.budgetsaver.ui.common.preset


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.getColorBasedOnValue
import com.pichurchyk.budgetsaver.ui.ext.getTransactionDefaultTitle
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.ext.toMajorWithCurrency
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@Composable
fun TransactionPresetChip(
    modifier: Modifier,
    preset: TransactionPreset,
    isSelected: Boolean,
    onItemClick: (TransactionPreset) -> Unit = {},
    onItemLongClick: (TransactionPreset) -> Unit = {}
) {
    val categoryColor = Color.fromHex(preset.mainCategory?.color ?: MaterialTheme.colorScheme.primary.toHex())
    val bgColor = if (isSelected) categoryColor.copy(0.1f) else disableGrey.copy(0.1f)
    val textColor = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { onItemClick(preset) },
                onLongClick = { onItemLongClick(preset) }
            )
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = preset.mainCategory?.emoji ?: "❓",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 14.sp,
                )

                Text(
                    modifier = Modifier,
                    text = preset.title ?: preset.getTransactionDefaultTitle(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                modifier = Modifier,
                color = preset.value.getColorBasedOnValue(),
                text = preset.value.toMajorWithCurrency(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        TransactionPresetChip(
            modifier = Modifier,
            preset = PreviewMocks.transactionPreset,
            isSelected = false,
            onItemClick = {},
            onItemLongClick = {}
        )
    }
}
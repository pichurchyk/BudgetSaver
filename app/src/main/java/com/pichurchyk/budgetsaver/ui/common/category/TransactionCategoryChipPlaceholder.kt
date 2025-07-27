package com.pichurchyk.budgetsaver.ui.common.category


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.theme.disableGrey


@Composable
fun TransactionCategoryChipPlaceHolder(modifier: Modifier) {
    val text by remember { mutableStateOf(getRandomChipText()) }

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(disableGrey.copy(0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = disableGrey.copy(0f)
    )
}

private fun getRandomChipText(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val length = (5..15).random()
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}
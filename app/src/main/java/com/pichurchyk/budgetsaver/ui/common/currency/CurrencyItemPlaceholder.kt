package com.pichurchyk.budgetsaver.ui.common.currency

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import java.util.Currency

@Composable
fun CurrencyItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.shimmerBackground(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, disableGrey.copy(0.1f), RoundedCornerShape(8.dp))
                .background(
                    disableGrey.copy(0.05f),
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(10.dp),
            textAlign = TextAlign.Center,
            text = "AAA",
            style = MaterialTheme.typography.bodyMedium,
            color = disableGrey.copy(0f)
        )
    }
}
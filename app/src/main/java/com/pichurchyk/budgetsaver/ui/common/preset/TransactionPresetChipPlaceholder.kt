package com.pichurchyk.budgetsaver.ui.common.preset


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.theme.disableGrey


@Composable
fun TransactionPresetChipPlaceHolder(modifier: Modifier = Modifier) {
    val text by remember { mutableStateOf(getRandomChipText()) }

    Row(
        modifier = modifier
            .shimmerBackground(
                RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(disableGrey.copy(0.1f))
            .border(1.dp, disableGrey.copy(0.3f), RoundedCornerShape(12.dp))
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
                    text = text,
                    color = Color.Transparent,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 14.sp,
                )

                Text(
                    modifier = Modifier,
                    text = text,
                    color = Color.Transparent,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                modifier = Modifier,
                text = text,
                color = Color.Transparent,
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
fun TransactionPresetChipPlaceHolderPreview() {
    TransactionPresetChipPlaceHolder(Modifier)
}

private fun getRandomChipText(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val length = (5..10).random()
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}
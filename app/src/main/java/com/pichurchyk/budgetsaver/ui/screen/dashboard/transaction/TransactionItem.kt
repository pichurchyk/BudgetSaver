package com.pichurchyk.budgetsaver.ui.screen.dashboard.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.util.Currency

@Composable
fun TransactionItem(transaction: Transaction) {
    val value by remember {
        mutableStateOf(transaction.value.toMajorWithCurrency())
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier,
            text = transaction.mainCategory.emoji,
            fontSize = 16.sp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                modifier = Modifier,
                text = transaction.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Text(
            modifier = Modifier
                .weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (transaction.value.amountMinor > 0) green else red
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TransactionItem(
                Transaction(
                    uuid = "",
                    title = "Bus ticket",
                    value = Money(amountMinor = 1000000000, currency = Currency.getInstance("USD")),
                    notes = "Grocery shopping at local market",
                    date = TransactionDate(
                        dateInstant = Instant.fromEpochMilliseconds(1748198228000),
                        zoneId = TimeZone.UTC
                    ),
                    mainCategory = TransactionCategory(
                        title = "Food",
                        emoji = "🍎",
                        color = "#FF00DS",
                        uuid = "123"
                    ),
                    subCategory = listOf(
                        TransactionSubCategory(
                            title = "Groceries",
                            color = "#FF00F0"
                        )
                    )
                )
            )
        }
    }
}
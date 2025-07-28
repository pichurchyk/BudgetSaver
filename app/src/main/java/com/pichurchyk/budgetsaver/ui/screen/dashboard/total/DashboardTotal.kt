package com.pichurchyk.budgetsaver.ui.screen.dashboard.total

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.ext.toMajorWithCurrency
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red
import java.math.BigInteger

@Composable
fun DashboardTotal(
    modifier: Modifier = Modifier,
    totalIncomes: Money,
    totalExpenses: Money
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = stringResource(R.string.summary),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, "", tint = green)

                Text(
                    text = totalIncomes.toMajorWithCurrency(),
                    color = green,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = totalExpenses.toMajorWithCurrency(),
                    color = red,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Icon(Icons.Rounded.KeyboardArrowDown, "", tint = red)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        DashboardTotal(
            modifier = Modifier.fillMaxWidth(),
            totalExpenses = PreviewMocks.money,
            totalIncomes = PreviewMocks.money,
        )
    }
}
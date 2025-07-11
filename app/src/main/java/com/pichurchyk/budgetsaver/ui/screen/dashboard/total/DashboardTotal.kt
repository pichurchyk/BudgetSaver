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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.ui.ext.getAbbreviated
import com.pichurchyk.budgetsaver.ui.ext.toMajorWithCurrency
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red
import java.math.BigInteger
import java.time.format.TextStyle

@Composable
fun DashboardTotal(
    modifier: Modifier = Modifier,
    totalIncomes: Money,
    totalExpenses: Money
) {
    var columnWidth by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .onSizeChanged { size ->
                columnWidth = size.width
            },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = stringResource(R.string.summary),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (totalExpenses.amountMinor == BigInteger("0") && totalIncomes.amountMinor == BigInteger("0")) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = stringResource(R.string.no_data_available),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.KeyboardArrowUp, "", tint = green)

                    Text(
                        text = totalExpenses.toMajorWithCurrency(),
                        color = green,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.KeyboardArrowDown, "", tint = red)

                    Text(
                        text = totalExpenses.toMajorWithCurrency(),
                        color = red,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .width(800.dp)
        ) {
            DashboardTotal(
                modifier = Modifier.fillMaxWidth(),
                totalExpenses = Money(
                    BigInteger("132123123"),
                    "USD"
                ),
                totalIncomes = Money(
                    BigInteger("32132131"),
                    "USD"
                ),
            )
        }
    }
}
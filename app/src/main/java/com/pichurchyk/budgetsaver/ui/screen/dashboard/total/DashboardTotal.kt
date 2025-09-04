package com.pichurchyk.budgetsaver.ui.screen.dashboard.total

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.ext.toMajorWithCurrency
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.appFont
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red

@Composable
fun DashboardTotal(
    modifier: Modifier = Modifier,
    totalIncomes: Money,
    totalExpenses: Money
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = stringResource(R.string.summary),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(green.copy(0.05f), RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = totalIncomes.toMajorWithCurrency(),
                    color = green,
                    fontSize = 24.sp,
                    fontFamily = appFont,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(red.copy(0.05f), RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = totalExpenses.toMajorWithCurrency(),
                    color = red,
                    fontSize = 24.sp,
                    fontFamily = appFont,
                    fontWeight = FontWeight.Bold
                )
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
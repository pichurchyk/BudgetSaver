package com.pichurchyk.budgetsaver.ui.screen.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import java.util.Currency

@Composable
fun CurrencySelector(
    modifier: Modifier,
    selectedCurrency: Currency,
    searchValue: String,
    currencies: List<Currency>,
    onSearchValueChanged: (String) -> Unit,
    onValueSelected: (Currency) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonInput(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            placeholder = stringResource(R.string.search),
            value = searchValue
        ) {
            onSearchValueChanged(it)
        }

        LazyHorizontalGrid(
            modifier = Modifier.height(100.dp),
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
        ) {
            val currencies = currencies

            items(items = currencies) { currency ->
                val isSelected = selectedCurrency == currency
                val borderColor =
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(0.1f)
                                else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onValueSelected(currency)
                            }
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        text = currency.currencyCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
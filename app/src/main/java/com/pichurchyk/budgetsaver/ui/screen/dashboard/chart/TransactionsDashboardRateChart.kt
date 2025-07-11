package com.pichurchyk.budgetsaver.ui.screen.dashboard.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.CorneredShape.Corner.Relative
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.toMajor
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import com.pichurchyk.budgetsaver.ui.theme.green
import java.math.BigInteger

@Composable
fun TransactionsDashboardRateChart(
    modifier: Modifier,
    transactions: List<Transaction>,
) {
    var chartWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    var singleColumnWidth by remember { mutableStateOf(0.dp) }

    LaunchedEffect(chartWidth) {
        singleColumnWidth =
            if (transactions.size <= 10) chartWidth / transactions.size else chartWidth / 10
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    var categoriesPercentage by remember {
        mutableStateOf<List<Pair<TransactionCategory, Double>>>(emptyList())
    }

    var isDataEmpty by remember { mutableStateOf(false) }

    val chartHeight by remember { mutableStateOf(100.dp) }

    LaunchedEffect(transactions) {
        categoriesPercentage = transactions
            .filter { it.value.amountMinor < BigInteger("0") }
            .groupBy { it.mainCategory }
            .mapValues { entry ->
                entry.value.sumOf { it.value.toMajor() }
            }
            .let { categorySums ->
                val total = categorySums.values.sum()
                categorySums.map { (category, sum) ->
                    category to if (total != 0.0) (sum / total * 100) else 0.0
                }
            }
            .sortedByDescending { it.second }

        if (categoriesPercentage.isEmpty()) {
            isDataEmpty = true
            modelProducer.runTransaction {
                columnSeries { series((10 downTo 1).toList().shuffled()) }
            }
        } else {
            isDataEmpty = false
            modelProducer.runTransaction {
                columnSeries { series(categoriesPercentage.map { it.second }) }
            }
        }
    }

    val chartStroke = if (isDataEmpty) disableGrey.copy(0.2f) else green.copy(0.5f)
    val chartItemStartColor =
        if (isDataEmpty) disableGrey.copy(0.1f).toArgb() else green.copy(0.5f).toArgb()
    val chartItemEndColor =
        if (isDataEmpty) disableGrey.copy(0.05f).toArgb() else green.copy(0.2f).toArgb()

    Column {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, top = 44.dp, bottom = 4.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.expenses_by_category),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = fill(
                                    ShaderProvider.verticalGradient(
                                        chartItemStartColor,
                                        chartItemEndColor
                                    )
                                ),
                                strokeThickness = 1.dp,
                                strokeFill = fill(chartStroke),
                                thickness = singleColumnWidth,
                                shape = CorneredShape(
                                    Relative(5, CorneredShape.CornerTreatment.Rounded),
                                    Relative(5, CorneredShape.CornerTreatment.Rounded),
                                ),
                            )
                        ),
                    ),
                    startAxis = null,
                    bottomAxis =
                        HorizontalAxis.rememberBottom(
                            valueFormatter = { _, value, _ ->
                                categoriesPercentage.getOrNull(value.toInt())?.first?.emoji ?: " "
                            },
                            label = TextComponent(
                                textSizeSp = 16f
                            ),
                            guideline = null,
                            tick = null,
                            line = null
                        ),
                    layerPadding = {
                        cartesianLayerPadding(
                            scalableStart = 8.dp,
                            scalableEnd = 8.dp
                        )
                    },
                ),
            animateIn = false,
            modelProducer = modelProducer,
            modifier = modifier
                .height(chartHeight)
                .onSizeChanged { size ->
                    with(density) {
                        chartWidth = size.width.toDp()
                    }
                },
            scrollState = rememberVicoScrollState(scrollEnabled = !isDataEmpty),
        )
    }
}

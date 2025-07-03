package com.pichurchyk.budgetsaver.ui.chart

import android.graphics.Typeface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun TransactionsLineChart(
    modifier: Modifier,
    transactions: List<Transaction>,
) {
    val context = LocalContext.current

    val modelProducer = remember { CartesianChartModelProducer() }

    var isDataEmpty by remember {
        mutableStateOf(false)
    }

    val chartHeight by remember { mutableStateOf(100.dp) }

    LaunchedEffect(transactions) {
        val chartData = transactions.map { it.value.toMajor() }
            .runningFold(0.0) { acc, v -> acc + v }
            .drop(1)

        if (chartData.isEmpty()) {
            isDataEmpty = true
            modelProducer.runTransaction {
                lineSeries { series((1..10).toList().shuffled()) }
            }
        } else {
            isDataEmpty = false

            modelProducer.runTransaction {
                lineSeries { series(chartData) }
            }
        }
    }

    val typeface by remember {
        mutableStateOf(ResourcesCompat.getFont(context, R.font.plus_jakarta_sans_regular))
    }

    val chartStroke = if (isDataEmpty) disableGrey.copy(0.2f) else green
    val chartItemStartColor =
        if (isDataEmpty) disableGrey.copy(0.1f).toArgb() else green.copy(0.3f).toArgb()
    val chartItemEndColor =
        if (isDataEmpty) disableGrey.copy(0.05f).toArgb() else green.copy(0f).toArgb()

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            pointConnector = LineCartesianLayer.PointConnector.cubic(1f),
                            fill = LineCartesianLayer.LineFill.single(fill(chartStroke)),
                            stroke = LineCartesianLayer.LineStroke.continuous(
                                cap = StrokeCap.Round,
                                thickness = 1.dp
                            ),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            chartItemStartColor,
                                            chartItemEndColor
                                        )
                                    )
                                ),
                        )
                    ),
            ),
            startAxis = if (!isDataEmpty) {
                VerticalAxis.rememberStart(
                    itemPlacer = VerticalAxis.ItemPlacer.count(),
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        typeface = typeface ?: Typeface.DEFAULT,
                        padding = Insets(horizontalDp = 20f)
                    ),
                    tick = null,
                    line = null,
                    guideline = rememberAxisGuidelineComponent(
                        fill = fill(MaterialTheme.colorScheme.onBackground.copy(0.05f))
                    ),
                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                    verticalLabelPosition = Position.Vertical.Top
                )
            } else null,
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
                line = null,
                tick = null,
                label = null
            ),
        ),
        animateIn = false,
        modelProducer = modelProducer,
        modifier = modifier
            .height(chartHeight),
        scrollState = rememberVicoScrollState(scrollEnabled = false)
    )
}
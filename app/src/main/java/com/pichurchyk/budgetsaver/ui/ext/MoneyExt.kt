package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.ui.theme.green
import com.pichurchyk.budgetsaver.ui.theme.red
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Currency
import kotlin.math.pow

fun Money.getColorBasedOnValue(): Color {
    return if (this.amountMinor > BigInteger("0")) {
        green
    } else {
        red
    }
}

fun Money.toMajorWithCurrency(): String {
    val currencyHelper = Currency.getInstance(this.currency)
    val amountBigDecimal = this.amountMinor.toBigDecimal()
    val divisor = BigDecimal.TEN.pow(currencyHelper.defaultFractionDigits)
    val majorAmount = amountBigDecimal.divide(
        divisor,
        currencyHelper.defaultFractionDigits,
        RoundingMode.HALF_EVEN
    )

    val stripped = majorAmount.stripTrailingZeros()
    val formattedAmount = if (stripped.scale() <= 0) {
        stripped.toPlainString()
    } else {
        "%.${currencyHelper.defaultFractionDigits}f".format(majorAmount)
    }

    return "$formattedAmount ${currencyHelper.symbol}"
}

fun Money.toMajor(): Double {
    val currencyHelper = Currency.getInstance(this.currency)

    val divisor = 10.0.pow(currencyHelper.defaultFractionDigits)
    return amountMinor.toDouble() / divisor
}

fun Money.toMajorString(): String {
    val currencyHelper = Currency.getInstance(this.currency)

    val divisor = 10.0.pow(currencyHelper.defaultFractionDigits)

    val value = amountMinor.toDouble() / divisor

    val formatedValue = if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }

    return formatedValue
}

@Composable
fun Money.getAbbreviated(): String {
    val currencyHelper = Currency.getInstance(this.currency)

    val fractionDigits = currencyHelper.defaultFractionDigits
    val divisor = BigDecimal.TEN.pow(fractionDigits.coerceAtLeast(0))
    val majorValue = BigDecimal(this.amountMinor).divide(divisor, 2, RoundingMode.DOWN)

    val trillion = BigDecimal(1_000_000_000_000)
    val billion = BigDecimal(1_000_000_000)
    val million = BigDecimal(1_000_000)
    val thousand = BigDecimal(1_000)

    val formatter = DecimalFormat("###.#")

    val trillionSymbol = stringResource(R.string.trillion_symbol)
    val billionSymbol = stringResource(R.string.billion_symbol)
    val millionSymbol = stringResource(R.string.million_symbol)
    val hundredSymbol = stringResource(R.string.hundred_symbol)

    val abbreviated = when {
        majorValue >= trillion -> "${formatter.format(majorValue.divide(trillion))}$trillionSymbol"
        majorValue >= billion -> "${formatter.format(majorValue.divide(billion))}$billionSymbol"
        majorValue >= million -> "${formatter.format(majorValue.divide(million))}$millionSymbol"
        majorValue >= thousand -> "${formatter.format(majorValue.divide(thousand))}$hundredSymbol"
        else -> formatter.format(majorValue)
    }

    return "${currencyHelper.symbol} $abbreviated"
}
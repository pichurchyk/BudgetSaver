package com.pichurchyk.budgetsaver.domain.model.transaction

import java.util.Currency
import kotlin.math.pow
import kotlin.math.roundToLong

data class Money(
    val amountMinor: Long,
    val currency: Currency
) {
    fun toMajorWithCurrency(): String {
        val divisor = 10.0.pow(currency.defaultFractionDigits)
        val majorAmount = amountMinor / divisor
        return "%.${currency.defaultFractionDigits}f %s".format(majorAmount, currency.symbol)
    }

    fun toMajor(): Double {
        val divisor = 10.0.pow(currency.defaultFractionDigits)
        return amountMinor.toDouble() / divisor
    }

    companion object {
        fun fromMajor(amount: Double, currency: Currency): Money {
            val factor = 10.0.pow(currency.defaultFractionDigits)
            val minorAmount = (amount * factor).roundToLong()
            return Money(minorAmount, currency)
        }
    }
}
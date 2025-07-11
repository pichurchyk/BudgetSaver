package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.domain.model.serializer.BigIntegerSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Currency

@Serializable
data class Money(
    @Serializable(with = BigIntegerSerializer::class)
    val amountMinor: BigInteger,
    val currency: String
) {
    companion object {
        fun fromMajor(amount: BigDecimal, currency: Currency): Money {
            val factor = BigDecimal.TEN.pow(currency.defaultFractionDigits)

            val minorAmountBigDecimal = amount.multiply(factor)
                .setScale(0, RoundingMode.HALF_UP)

            return Money(minorAmountBigDecimal.toBigInteger(), currency.currencyCode)
        }
    }
}
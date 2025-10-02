package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.model.payload.TransactionPayload
import com.pichurchyk.budgetsaver.data.model.response.TransactionResponse
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.ext.toMajor
import com.pichurchyk.budgetsaver.ui.ext.toMajorString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.math.BigDecimal
import java.math.BigInteger
import java.time.ZoneId
import java.util.Currency
import kotlin.text.toBigDecimal

fun TransactionResponse.toDomain(): Transaction {
    val instant = Instant.fromEpochMilliseconds(this.dateMillis)
    val zoneId = TimeZone.of(this.dateTimeZone)

    return Transaction(
        uuid = this.uuid,
        title = this.title,
        notes = this.notes,
        value = Money(
            amountMinor = this.value.toBigInteger(),
            currency = this.currency
        ),
        date = TransactionDate(
            dateInstant = instant,
            timeZone = zoneId
        ),
        mainCategory = this.mainCategory?.toDomain(),
        subCategory = emptyList(),
    )
}

fun TransactionCreation.toPayload(): TransactionPayload {
    val currentInstant = Clock.System.now()
    val zoneId = ZoneId.systemDefault()

    val value = if (this.type == TransactionType.EXPENSES) {
        -this.value.toBigDecimal()
    } else {
        this.value.toBigDecimal()
    }

    return TransactionPayload(
        title = this.title?.ifEmpty { null },
        value = Money.fromMajor(value, this.currency).amountMinor,
        currency = this.currency.currencyCode,
        notes = this.notes,
        dateMillis = currentInstant.toEpochMilliseconds(),
        dateTimeZone = zoneId.id,
        mainCategory = this.mainCategory?.uuid
    )
}

fun Transaction.toTransactionCreation(): TransactionCreation {
    val type =
        if (value.amountMinor >= BigInteger("0")) TransactionType.INCOMES else TransactionType.EXPENSES

    val currency = Currency.getInstance(value.currency)

    return TransactionCreation(
        title = title,
        value = Money(value.amountMinor.abs(), value.currency).toMajorString(),
        currency = currency,
        notes = notes,
        date = TransactionDate(
            Instant.fromEpochMilliseconds(date.dateInstant.toEpochMilliseconds()),
            date.timeZone
        ),
        type = type,
        mainCategory = mainCategory,
        subCategory = emptyList()
    )
}

fun TransactionCreation.toPresetCreation(): TransactionPresetCreation {
    return TransactionPresetCreation(
        title = title,
        notes = notes,
        value = value,
        type = type,
        currency = currency,
        mainCategory = mainCategory,
        subCategory = emptyList(),
    )
}
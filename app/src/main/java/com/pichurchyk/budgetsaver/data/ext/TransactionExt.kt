package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.model.payload.TransactionPayload
import com.pichurchyk.budgetsaver.data.model.response.TransactionResponse
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

fun TransactionResponse.toDomain(): Transaction {

    return Transaction(
        uuid = this.uuid,
        title = this.title,
        notes = this.notes,
        value = Money(
            amountMinor = this.value.toBigInteger(),
            currency = this.currency
        ),
        date = TransactionDate(Instant.fromEpochMilliseconds(this.dateMillis), TimeZone.of(this.dateTimeZone)),
        mainCategory = this.mainCategory.toDomain(),
        subCategory = emptyList(),
    )
}

fun TransactionCreation.toPayload(): TransactionPayload {
    val currentMillis = System.currentTimeMillis()

    val zoneId = java.time.ZoneId.systemDefault()
    val offset = java.time.ZonedDateTime.now(zoneId).offset
    val utcOffset = "UTC" + offset.id

    val value = if (this.type == TransactionType.EXPENSES) -this.value.toBigDecimal() else this.value.toBigDecimal()

    return TransactionPayload(
        title = this.title,
        value = Money.fromMajor(value, this.currency).amountMinor,
        currency = this.currency.currencyCode,
        notes = this.notes,
        dateMillis = currentMillis,
        dateTimeZone = utcOffset,
        mainCategory = checkNotNull(this.mainCategory?.uuid) {
            "Category must not be null"
        }
    )
}
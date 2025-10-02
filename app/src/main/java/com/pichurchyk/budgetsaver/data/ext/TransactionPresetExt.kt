package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.model.payload.TransactionPresetPayload
import com.pichurchyk.budgetsaver.data.model.response.TransactionPresetResponse
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType

fun TransactionPresetResponse.toDomain(): TransactionPreset {

    return TransactionPreset(
        uuid = this.uuid,
        title = this.title,
        value = Money(
            amountMinor = this.value.toBigInteger(),
            currency = this.currency
        ),
        notes = this.notes ?: "",
        mainCategory = this.mainCategory?.toDomain()
    )
}

fun TransactionPresetCreation.toPayload(): TransactionPresetPayload {
    val value =
        if (this.type == TransactionType.EXPENSES) -this.value.toBigDecimal() else this.value.toBigDecimal()

    return TransactionPresetPayload(
        title = this.title,
        value = Money.fromMajor(value, this.currency).amountMinor,
        currency = this.currency.currencyCode,
        notes = this.notes,
        mainCategory = this.mainCategory?.uuid
    )
}
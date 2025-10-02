package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.model.payload.TransactionPresetPayload
import com.pichurchyk.budgetsaver.data.model.response.TransactionPresetResponse
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.ext.toMajor
import java.math.BigInteger
import java.util.Currency
import kotlin.math.abs

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

fun TransactionPreset.toTransactionCreation(): TransactionCreation {
    val type =
        if (value.amountMinor >= BigInteger("0")) TransactionType.INCOMES else TransactionType.EXPENSES

    val currency = Currency.getInstance(value.currency)


    return TransactionCreation(
        title = this.title,
        value = abs(this.value.toMajor()).toString(),
        currency = currency,
        notes = this.notes,
        type = type,
        mainCategory = this.mainCategory,
        subCategory = this.subCategory
    )
}
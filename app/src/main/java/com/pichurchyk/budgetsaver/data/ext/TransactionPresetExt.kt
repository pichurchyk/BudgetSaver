package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.data.ext.category.toDomain
import com.pichurchyk.budgetsaver.data.model.response.TransactionPresetResponse
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionPreset

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
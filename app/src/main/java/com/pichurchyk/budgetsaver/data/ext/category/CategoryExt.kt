package com.pichurchyk.budgetsaver.data.ext.category

import com.pichurchyk.budgetsaver.data.model.payload.TransactionCategoryPayload
import com.pichurchyk.budgetsaver.data.model.response.MainCategoryResponse
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import kotlin.random.Random

fun MainCategoryResponse.toDomain() = TransactionCategory(
    title = this.title,
    emoji = this.emoji,
    color = this.color,
    uuid = this.uuid
)

fun TransactionCategoryCreation.toPayload() = TransactionCategoryPayload(
    title = this.title,
    color = this.color,
    emoji = this.emoji
)
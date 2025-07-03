package com.pichurchyk.budgetsaver.data.ext.category

import com.pichurchyk.budgetsaver.data.model.response.MainCategoryResponse
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import kotlin.random.Random

fun MainCategoryResponse.toDomain() = TransactionCategory(
    title = this.title,
    emoji = this.emoji,
    color = this.color,
    uuid = this.uuid
)
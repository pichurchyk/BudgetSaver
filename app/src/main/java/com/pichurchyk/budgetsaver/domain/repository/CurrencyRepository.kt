package com.pichurchyk.budgetsaver.domain.repository

import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface CurrencyRepository {
    fun getAllCurrencies(): Flow<List<Currency>>
}
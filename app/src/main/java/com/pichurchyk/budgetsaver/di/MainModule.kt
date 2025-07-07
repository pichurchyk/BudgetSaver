package com.pichurchyk.budgetsaver.di

import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferences
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferencesActions
import com.pichurchyk.budgetsaver.data.repository.AuthRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.TransactionsRepositoryImpl
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import com.pichurchyk.budgetsaver.domain.usecase.AddTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.AddTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCaseImpl
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.category.viewmodel.CategorySelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionViewModel
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {

    single<TransactionsRepository> { TransactionsRepositoryImpl(get()) }
    single<TransactionsDataSource> { TransactionsDataSource(get()) }

    viewModelOf(::AuthViewModel)

    single<GetSignedInUserUseCase> { GetSignedInUserUseCaseImpl(get()) }
    single<SignInUseCase> { SignInUseCaseImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { AuthDataSource(get(), get()) }

    single<AuthPreferencesActions> { AuthPreferences(get()) }



    viewModelOf(::DashboardViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::CategorySelectorViewModel)


    single<GetTransactionsUseCase> { GetTransactionsUseCaseImpl(get()) }
    single<GetTransactionsCategoriesUseCase> { GetTransactionsCategoriesUseCaseImpl(get()) }

    single<AddTransactionUseCase> { AddTransactionUseCaseImpl(get()) }
}
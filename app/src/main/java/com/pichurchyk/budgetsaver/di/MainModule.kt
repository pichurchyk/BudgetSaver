package com.pichurchyk.budgetsaver.di

import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferences
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferencesActions
import com.pichurchyk.budgetsaver.data.preferences.SystemPreferences
import com.pichurchyk.budgetsaver.data.repository.AuthRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.SystemRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.TransactionsRepositoryImpl
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import com.pichurchyk.budgetsaver.domain.repository.SystemRepository
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import com.pichurchyk.budgetsaver.domain.usecase.AddFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.AddFavoriteCurrencyUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.AddTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.AddTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.DeleteCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.DeleteCategoryUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.DeleteFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.DeleteFavoriteCurrencyUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.DeleteTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.DeleteTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.EditTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.EditTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.LoadTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCaseImpl
import com.pichurchyk.budgetsaver.ui.MainViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.viewmodel.CategorySelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionViewModel
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthViewModel
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileViewModel
import com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel.AppThemeSelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel.EditTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {

    single<SystemRepository> { SystemRepositoryImpl(get()) }

    single<TransactionsRepository> { TransactionsRepositoryImpl(get()) }
    single<TransactionsDataSource> { TransactionsDataSource(get()) }

    single<GetSignedInUserUseCase> { GetSignedInUserUseCaseImpl(get()) }
    single<SignInUseCase> { SignInUseCaseImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single { AuthDataSource(get(), get(), get()) }

    single<AuthPreferencesActions> { AuthPreferences(get()) }
    single<SystemPreferences> { SystemPreferences(get()) }
    single<SessionManager> { SessionManager() }

    viewModelOf(::AuthViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::CategorySelectorViewModel)
    viewModelOf(::EditTransactionViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AppThemeSelectorViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::FavoriteCurrenciesSelectorViewModel)

    single<GetTransactionsUseCase> { GetTransactionsUseCaseImpl(get()) }
    single<GetTransactionsCategoriesUseCase> { GetTransactionsCategoriesUseCaseImpl(get()) }
    single<DeleteCategoryUseCase> { DeleteCategoryUseCaseImpl(get()) }

    single<AddTransactionUseCase> { AddTransactionUseCaseImpl(get()) }
    single<EditTransactionUseCase> { EditTransactionUseCaseImpl(get()) }
    single<DeleteTransactionUseCase> { DeleteTransactionUseCaseImpl(get()) }

    single<LoadTransactionUseCase> { LoadTransactionUseCaseImpl(get()) }

    single<AddFavoriteCurrencyUseCase> { AddFavoriteCurrencyUseCaseImpl(get(), get()) }
    single<DeleteFavoriteCurrencyUseCase> { DeleteFavoriteCurrencyUseCaseImpl(get(), get()) }
}
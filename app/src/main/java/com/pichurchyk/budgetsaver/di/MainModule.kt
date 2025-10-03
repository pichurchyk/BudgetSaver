package com.pichurchyk.budgetsaver.di

import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.datasource.TransactionsDataSource
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferences
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferencesActions
import com.pichurchyk.budgetsaver.data.preferences.SystemPreferences
import com.pichurchyk.budgetsaver.data.repository.AuthRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.CurrencyRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.EmojiRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.SystemRepositoryImpl
import com.pichurchyk.budgetsaver.data.repository.TransactionsRepositoryImpl
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.repository.EmojiRepository
import com.pichurchyk.budgetsaver.domain.repository.SystemRepository
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import com.pichurchyk.budgetsaver.domain.usecase.category.AddCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.category.AddCategoryUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.currency.AddFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.currency.AddFavoriteCurrencyUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.transaction.AddTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.AddTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.category.DeleteCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.category.DeleteCategoryUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.currency.DeleteFavoriteCurrencyUseCase
import com.pichurchyk.budgetsaver.domain.usecase.currency.DeleteFavoriteCurrencyUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.preset.DeletePresetUseCase
import com.pichurchyk.budgetsaver.domain.usecase.preset.DeletePresetUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.transaction.DeleteTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.DeleteTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.transaction.EditTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.EditTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.preset.GetPresetsUseCase
import com.pichurchyk.budgetsaver.domain.usecase.preset.GetPresetsUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.category.GetTransactionsCategoriesUseCase
import com.pichurchyk.budgetsaver.domain.usecase.category.GetTransactionsCategoriesUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.transaction.GetTransactionsUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.GetTransactionsUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.transaction.LoadTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.LoadTransactionUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.SearchEmojiUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SearchEmojiUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCaseImpl
import com.pichurchyk.budgetsaver.domain.usecase.preset.AddPresetUseCase
import com.pichurchyk.budgetsaver.domain.usecase.preset.AddPresetUseCaseImpl
import com.pichurchyk.budgetsaver.ui.MainViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.viewmodel.CategorySelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionViewModel
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel.AddCategoryViewModel
import com.pichurchyk.budgetsaver.ui.screen.currency.viewmodel.FavoriteCurrenciesSelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel.DashboardViewModel
import com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel.AddPresetViewModel
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
    single<EmojiRepository> { EmojiRepositoryImpl(get()) }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get()) }
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
    viewModelOf(::AddCategoryViewModel)
    viewModelOf(::AddPresetViewModel)

    single<GetTransactionsUseCase> { GetTransactionsUseCaseImpl(get()) }

    single<GetPresetsUseCase> { GetPresetsUseCaseImpl(get()) }
    single<DeletePresetUseCase> { DeletePresetUseCaseImpl(get()) }
    single<AddPresetUseCase> { AddPresetUseCaseImpl(get()) }

    single<GetTransactionsCategoriesUseCase> { GetTransactionsCategoriesUseCaseImpl(get()) }
    single<DeleteCategoryUseCase> { DeleteCategoryUseCaseImpl(get()) }
    single<AddCategoryUseCase> { AddCategoryUseCaseImpl(get()) }

    single<AddTransactionUseCase> { AddTransactionUseCaseImpl(get()) }
    single<EditTransactionUseCase> { EditTransactionUseCaseImpl(get()) }
    single<DeleteTransactionUseCase> { DeleteTransactionUseCaseImpl(get()) }

    single<LoadTransactionUseCase> { LoadTransactionUseCaseImpl(get()) }

    single<AddFavoriteCurrencyUseCase> { AddFavoriteCurrencyUseCaseImpl(get(), get()) }
    single<DeleteFavoriteCurrencyUseCase> { DeleteFavoriteCurrencyUseCaseImpl(get(), get()) }

    single<LoadEmojisUseCase> { LoadEmojisUseCaseImpl(get()) }
    single<SearchEmojiUseCase> { SearchEmojiUseCaseImpl(get()) }
}
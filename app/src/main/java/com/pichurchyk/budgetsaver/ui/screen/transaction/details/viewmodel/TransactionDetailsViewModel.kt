package com.pichurchyk.budgetsaver.ui.screen.transaction.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.RelativeTransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.usecase.GetSingleTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val getTransactionUseCase: GetSingleTransactionUseCase,
    private val initialTransactionId: String
) : ViewModel() {

    private val _viewState: MutableStateFlow<TransactionDetailsViewState> = MutableStateFlow(
        TransactionDetailsViewState(
            transactions = listOf(),
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        loadInitialTransaction()
    }

    fun handleIntent(intent: TransactionDetailsIntent) {
        when (intent) {
            TransactionDetailsIntent.LoadNextTransaction -> {
                loadRelativeTransaction(direction = RelativeTransactionType.NEXT)
            }

            TransactionDetailsIntent.LoadPreviousTransaction -> {
                loadRelativeTransaction(direction = RelativeTransactionType.PREVIOUS)
            }
        }
    }

    private fun loadInitialTransaction() {
        viewModelScope.launch {
            getTransactionUseCase
                .invoke(
                    transactionId = initialTransactionId
                )
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(
                            uiStatus = TransactionDetailsUiStatus.Loading
                        )
                    }
                }
                .catch { e ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            uiStatus = TransactionDetailsUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadInitialTransaction() }
                            )
                        )
                    }
                }
                .collect { transaction ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            transactions = currentState.transactions + transaction,
                            currentTransaction = transaction,
                            uiStatus = TransactionDetailsUiStatus.Idle
                        )
                    }
                }
        }
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            getTransactionUseCase
                .invoke(
                    transactionId = viewState.value.currentTransaction?.uuid ?: initialTransactionId
                )
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(
                            uiStatus = TransactionDetailsUiStatus.Loading
                        )
                    }
                }
                .catch { e ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            uiStatus = TransactionDetailsUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadTransaction() }
                            )
                        )
                    }
                }
                .collect { transaction ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            transactions = currentState.transactions + transaction,
                            currentTransaction = transaction,
                            uiStatus = TransactionDetailsUiStatus.Idle
                        )
                    }
                }
        }
    }

    private fun loadRelativeTransaction(direction: RelativeTransactionType) {
        if (viewState.value.currentTransaction == null) return
        val loadedRelativeTransaction = getRelativeTransactionIfLoaded(direction)

        loadedRelativeTransaction?.let { transaction ->
            _viewState.update { currentState ->
                currentState.copy(
                    currentTransaction = transaction,
                    uiStatus = TransactionDetailsUiStatus.Idle
                )
            }
        } ?: run {
            viewModelScope.launch {
                getTransactionUseCase
                    .invoke(
                        transactionId = viewState.value.currentTransaction!!.uuid,
                        direction = direction
                    )
                    .onStart {
                        _viewState.update { currentState ->
                            currentState.copy(
                                uiStatus = TransactionDetailsUiStatus.Loading
                            )
                        }
                    }
                    .catch { e ->
                        _viewState.update { currentState ->
                            currentState.copy(
                                uiStatus = TransactionDetailsUiStatus.Error(
                                    error = e as DomainException,
                                    lastAction = { loadTransaction() }
                                )
                            )
                        }
                    }
                    .collect { transaction ->
                        _viewState.update { currentState ->
                            val newTransactionsListSorted =
                                (currentState.transactions + transaction).sortedBy { it.date.dateInstant }
                            currentState.copy(
                                transactions = newTransactionsListSorted,
                                uiStatus = TransactionDetailsUiStatus.Idle
                            )
                        }
                    }
            }
        }
    }

    private fun getRelativeTransactionIfLoaded(direction: RelativeTransactionType): Transaction? {
        val currentTransaction = viewState.value.currentTransaction
        val currentTransactionIndex = viewState.value.transactions.indexOf(currentTransaction)
        val allTransactions = viewState.value.transactions

        when (direction) {
            RelativeTransactionType.NEXT -> {
                val nextTransaction = allTransactions.getOrNull(currentTransactionIndex + 1)

                return nextTransaction
            }

            RelativeTransactionType.PREVIOUS -> {
                val previousTransaction = allTransactions.getOrNull(currentTransactionIndex - 1)

                return previousTransaction
            }
        }
    }
}
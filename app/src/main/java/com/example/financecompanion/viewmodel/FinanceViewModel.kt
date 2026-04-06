package com.example.financecompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financecompanion.data.local.BudgetEntity
import com.example.financecompanion.data.local.TransactionEntity
import com.example.financecompanion.data.repository.FinanceRepository
import com.example.financecompanion.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.map

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Int): String = "edit_transaction/$transactionId"
    }
    data object Insights : Screen("insights")
    data object Budget : Screen("budget")
}

data class FinanceUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val budget: BudgetEntity? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    val uiState: StateFlow<FinanceUiState> =
        combine(repository.allTransactions, repository.budget) { transactions, budget ->

            val income = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expense = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            FinanceUiState(
                transactions = transactions,
                budget = budget,
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceUiState()
        )

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: String,
        note: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    category = category,
                    note = note,
                    date = date
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun getTransactionById(transactionId: Int): TransactionEntity? {
        return uiState.value.transactions.find { it.id == transactionId }
    }

    fun saveBudget(limit: Double) {
        viewModelScope.launch {
            val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
            repository.saveBudget(
                BudgetEntity(
                    monthlyLimit = limit,
                    month = currentMonth
                )
            )
        }
    }
}

class FinanceViewModelFactory(
    private val repository: FinanceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
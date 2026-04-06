package com.example.financecompanion.data.repository

import com.example.financecompanion.data.local.BudgetEntity
import com.example.financecompanion.data.local.FinanceDao
import com.example.financecompanion.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val dao: FinanceDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val budget: Flow<BudgetEntity?> = dao.getBudget()

    suspend fun addTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun saveBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        dao.updateBudget(budget)
    }
}


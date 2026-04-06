package com.example.financecompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financecompanion.model.TransactionType
import com.example.financecompanion.viewmodel.FinanceViewModel
import kotlin.math.max
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults

@Composable
fun InsightsScreen(
    viewModel: FinanceViewModel,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()

    val expenseTransactions = uiState.transactions.filter { it.type == TransactionType.EXPENSE }

    val categoryTotals = expenseTransactions
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val highestCategory = categoryTotals.maxByOrNull { it.value }
    val totalExpense = max(uiState.totalExpense, 1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Top Spending Category",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Shopping - ₹500.00",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Monthly Expense Total",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("₹${String.format("%.2f", uiState.totalExpense)}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Spending by Category",
                    style = MaterialTheme.typography.titleMedium
                )

                if (categoryTotals.isEmpty()) {
                    Text("No expense data available")
                } else {
                    categoryTotals.forEach { (category, total) ->
                        val progress = (total / totalExpense).toFloat()

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("$category • ₹${String.format("%.2f", total)}")
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Transaction Summary",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Total Income: ₹${String.format("%.2f", uiState.totalIncome)}")
                Text("Total Expenses: ₹${String.format("%.2f", uiState.totalExpense)}")
                Text("Current Balance: ₹${String.format("%.2f", uiState.balance)}")
            }
        }
    }
}
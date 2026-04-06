package com.example.financecompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financecompanion.model.TransactionType
import com.example.financecompanion.ui.components.TransactionItem
import com.example.financecompanion.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()

    val expenseTransactions = uiState.transactions.filter { it.type == TransactionType.EXPENSE }
    val topCategory = expenseTransactions
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .maxByOrNull { it.value }

    val budgetLimit = uiState.budget?.monthlyLimit ?: 0.0
    val spent = uiState.totalExpense
    val remaining = budgetLimit - spent

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatAmount(amount: Double): String {
        return currencyFormatter.format(amount)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Track your money with clarity and control",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatAmount(uiState.balance),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Updated from your latest transactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (budgetLimit > 0 && spent > budgetLimit) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Smart Spending Alert",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "You exceeded your monthly budget by ${formatAmount(spent - budgetLimit)}",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        } else if (budgetLimit > 0 && spent >= budgetLimit * 0.8) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Budget Warning",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "You are close to your monthly budget limit.",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatAmount(uiState.totalIncome),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Expenses",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatAmount(uiState.totalExpense),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Budget Snapshot",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Monthly Limit: ${formatAmount(budgetLimit)}")
                    Text("Spent: ${formatAmount(spent)}")
                    Text("Remaining: ${formatAmount(remaining)}")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Quick Insight",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = topCategory?.let {
                            "Top spending category: ${it.key} (${formatAmount(it.value)})"
                        } ?: "No expense data available yet"
                    )
                }
            }
        }

        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (uiState.transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Add your first income or expense to start tracking your money.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.transactions.take(5)) { transaction ->
                TransactionItem(transaction = transaction)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
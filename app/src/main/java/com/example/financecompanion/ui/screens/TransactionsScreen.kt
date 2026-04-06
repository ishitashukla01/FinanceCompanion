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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financecompanion.data.local.TransactionEntity
import com.example.financecompanion.model.TransactionType
import com.example.financecompanion.ui.components.TransactionItem
import com.example.financecompanion.viewmodel.FinanceViewModel

@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    paddingValues: PaddingValues,
    onEditTransaction: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredTransactions = uiState.transactions.filter { transaction ->
        val matchesSearch =
            transaction.category.contains(searchQuery, ignoreCase = true) ||
                    transaction.note.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Income" -> transaction.type == TransactionType.INCOME
            "Expense" -> transaction.type == TransactionType.EXPENSE
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Search, filter and manage your financial records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by category or note") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == "All",
                            onClick = { selectedFilter = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = selectedFilter == "Income",
                            onClick = { selectedFilter = "Income" },
                            label = { Text("Income") }
                        )
                        FilterChip(
                            selected = selectedFilter == "Expense",
                            onClick = { selectedFilter = "Expense" },
                            label = { Text("Expense") }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Records",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No matching transactions",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Try changing your search or filter to see more results.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions) { transaction ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { onEditTransaction(transaction.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TransactionItem(transaction = transaction)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { onEditTransaction(transaction.id) }
                        ) {
                            Text("Edit")
                        }
                        TextButton(
                            onClick = { transactionToDelete = transaction }
                        ) {
                            Text("Delete")
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }

    transactionToDelete?.let { selectedTransaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Do you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(selectedTransaction)
                        transactionToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { transactionToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
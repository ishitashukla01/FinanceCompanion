package com.example.financecompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financecompanion.model.TransactionType
import com.example.financecompanion.viewmodel.FinanceViewModel

@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    paddingValues: PaddingValues,
    onSaved: () -> Unit,
    existingTransactionId: Int? = null
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val categories = listOf(
        "Food", "Travel", "Shopping", "Bills", "Salary", "Health", "Other"
    )

    val existingTransaction = existingTransactionId?.let {
        viewModel.getTransactionById(it)
    }

    LaunchedEffect(existingTransactionId) {
        existingTransaction?.let { transaction ->
            amount = transaction.amount.toString()
            category = transaction.category
            note = transaction.note
            selectedType = transaction.type
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (existingTransactionId == null) "Add Transaction" else "Edit Transaction",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (existingTransactionId == null)
                "Record your income or expense in a simple way"
            else
                "Update your existing transaction",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Amount"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Transaction Type",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = { selectedType = TransactionType.INCOME },
                        label = { Text("Income") }
                    )
                    FilterChip(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = { selectedType = TransactionType.EXPENSE },
                        label = { Text("Expense") }
                    )
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Category"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose Category")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                category = item
                                expanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Note"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull()

                        if (amountValue == null || amountValue <= 0) {
                            error = "Please enter a valid amount"
                            return@Button
                        }

                        if (category.isBlank()) {
                            error = "Please select a category"
                            return@Button
                        }

                        error = ""

                        if (existingTransaction != null) {
                            viewModel.updateTransaction(
                                existingTransaction.copy(
                                    amount = amountValue,
                                    type = selectedType,
                                    category = category,
                                    note = note
                                )
                            )
                        } else {
                            viewModel.addTransaction(
                                amount = amountValue,
                                type = selectedType,
                                category = category,
                                note = note
                            )
                        }

                        onSaved()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (existingTransactionId == null) "Save Transaction" else "Update Transaction")
                }
            }
        }
    }
}
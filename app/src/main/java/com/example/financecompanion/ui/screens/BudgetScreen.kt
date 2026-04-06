package com.example.financecompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financecompanion.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    var budgetInput by remember { mutableStateOf("") }

    val budgetLimit = uiState.budget?.monthlyLimit ?: 0.0
    val spent = uiState.totalExpense
    val remaining = budgetLimit - spent
    val progress = if (budgetLimit > 0) (spent / budgetLimit).toFloat() else 0f
    val progressPercent = if (budgetLimit > 0) ((spent / budgetLimit) * 100).toInt() else 0

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatAmount(amount: Double): String {
        return currencyFormatter.format(amount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Monthly Budget Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Stay in control of your monthly spending",
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Set Your Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { budgetInput = it },
                    label = { Text("Budget Limit") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val value = budgetInput.toDoubleOrNull()
                        if (value != null && value > 0) {
                            viewModel.saveBudget(value)
                            budgetInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Budget")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Budget Overview",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Budget: ${formatAmount(budgetLimit)}")
                Text("Spent: ${formatAmount(spent)}")
                Text("Remaining: ${formatAmount(remaining)}")
                Text("Used: $progressPercent%")

                LinearProgressIndicator(
                    progress = { min(progress, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )

                when {
                    budgetLimit == 0.0 -> {
                        Text(
                            text = "Set a budget to start tracking your spending.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    spent > budgetLimit -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Budget Exceeded",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "You exceeded your budget by ${formatAmount(spent - budgetLimit)}",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    spent >= budgetLimit * 0.8 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Budget Warning",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "You are close to your monthly budget limit.",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                    else -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "On Track",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Great job. Your spending is under control.",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
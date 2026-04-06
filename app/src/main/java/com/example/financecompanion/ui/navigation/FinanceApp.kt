package com.example.financecompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financecompanion.ui.screens.AddTransactionScreen
import com.example.financecompanion.ui.screens.BudgetScreen
import com.example.financecompanion.ui.screens.HomeScreen
import com.example.financecompanion.ui.screens.InsightsScreen
import com.example.financecompanion.ui.screens.TransactionsScreen
import com.example.financecompanion.viewmodel.FinanceViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Default.AccountBalanceWallet),
        BottomNavItem(Screen.Transactions.route, "Transactions", Icons.Default.List),
        BottomNavItem(Screen.Insights.route, "Insights", Icons.Default.Analytics),
        BottomNavItem(Screen.Budget.route, "Budget", Icons.Default.PieChart)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction"
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel, paddingValues = innerPadding)
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    viewModel = viewModel,
                    paddingValues = innerPadding,
                    onEditTransaction = { transactionId ->
                        navController.navigate(Screen.EditTransaction.createRoute(transactionId))
                    }
                )
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    paddingValues = innerPadding,
                    onSaved = { navController.popBackStack() },
                    existingTransactionId = null
                )
            }
            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getInt("transactionId")
                AddTransactionScreen(
                    viewModel = viewModel,
                    paddingValues = innerPadding,
                    onSaved = { navController.popBackStack() },
                    existingTransactionId = transactionId
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen(viewModel = viewModel, paddingValues = innerPadding)
            }
            composable(Screen.Budget.route) {
                BudgetScreen(viewModel = viewModel, paddingValues = innerPadding)
            }
        }
    }
}
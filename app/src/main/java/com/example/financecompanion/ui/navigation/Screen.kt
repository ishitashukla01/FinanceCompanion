package com.example.financecompanion.ui.navigation

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
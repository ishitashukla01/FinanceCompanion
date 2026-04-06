package com.example.financecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.financecompanion.data.local.AppDatabase
import com.example.financecompanion.data.repository.FinanceRepository
import com.example.financecompanion.ui.navigation.FinanceApp
import com.example.financecompanion.ui.theme.FinanceCompanionTheme
import com.example.financecompanion.viewmodel.FinanceViewModel
import com.example.financecompanion.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "finance_database"
        ).build()

        val repository = FinanceRepository(database.financeDao())

        setContent {
            FinanceCompanionTheme {
                val financeViewModel: FinanceViewModel = viewModel(
                    factory = FinanceViewModelFactory(repository)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF8F5FC),
                                    Color(0xFFEDE7F6)
                                )
                            )
                        )
                ) {
                    FinanceApp(viewModel = financeViewModel)
                }
            }
        }
    }
}
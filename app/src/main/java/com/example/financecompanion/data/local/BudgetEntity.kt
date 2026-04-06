package com.example.financecompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monthlyLimit: Double,
    val month: String
)
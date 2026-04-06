package com.example.financecompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financecompanion.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val note: String
)
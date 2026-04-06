package com.example.financecompanion.data.local

import androidx.room.TypeConverter
import com.example.financecompanion.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
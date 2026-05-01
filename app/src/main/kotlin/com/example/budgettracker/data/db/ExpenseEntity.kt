package com.example.budgettracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val date: String,
    val month: String
)

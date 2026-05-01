package com.example.budgettracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_budget")
data class IncomeBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expectedIncome: Double,
    val month: String
)

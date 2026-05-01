package com.example.budgettracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val source: String,
    val date: String,
    val month: String
)

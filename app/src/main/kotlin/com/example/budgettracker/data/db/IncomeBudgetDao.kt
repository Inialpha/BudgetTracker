package com.example.budgettracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeBudgetDao {
    @Query("SELECT * FROM income_budget WHERE month = :month LIMIT 1")
    fun getIncomeBudgetByMonth(month: String): Flow<IncomeBudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incomeBudget: IncomeBudgetEntity): Long

    @Update
    suspend fun update(incomeBudget: IncomeBudgetEntity)

    @Delete
    suspend fun delete(incomeBudget: IncomeBudgetEntity)
}

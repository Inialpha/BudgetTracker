package com.example.budgettracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense WHERE month = :month ORDER BY date DESC")
    fun getExpensesByMonth(month: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expense WHERE month = :month")
    fun getTotalExpensesByMonth(month: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT * FROM expense WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?
}

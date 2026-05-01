package com.example.budgettracker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income WHERE month = :month ORDER BY date DESC")
    fun getIncomeByMonth(month: String): Flow<List<IncomeEntity>>

    @Query("SELECT SUM(amount) FROM income WHERE month = :month")
    fun getTotalIncomeByMonth(month: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeEntity): Long

    @Update
    suspend fun update(income: IncomeEntity)

    @Delete
    suspend fun delete(income: IncomeEntity)

    @Query("SELECT * FROM income WHERE id = :id")
    suspend fun getById(id: Long): IncomeEntity?
}

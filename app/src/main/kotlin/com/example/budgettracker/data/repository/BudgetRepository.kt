package com.example.budgettracker.data.repository

import com.example.budgettracker.data.db.*
import kotlinx.coroutines.flow.Flow

class BudgetRepository(
    private val incomeDao: IncomeDao,
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val incomeBudgetDao: IncomeBudgetDao
) {
    fun getIncomeByMonth(month: String): Flow<List<IncomeEntity>> = incomeDao.getIncomeByMonth(month)
    fun getTotalIncomeByMonth(month: String): Flow<Double?> = incomeDao.getTotalIncomeByMonth(month)
    suspend fun insertIncome(income: IncomeEntity) = incomeDao.insert(income)
    suspend fun updateIncome(income: IncomeEntity) = incomeDao.update(income)
    suspend fun deleteIncome(income: IncomeEntity) = incomeDao.delete(income)

    fun getExpensesByMonth(month: String): Flow<List<ExpenseEntity>> = expenseDao.getExpensesByMonth(month)
    fun getTotalExpensesByMonth(month: String): Flow<Double?> = expenseDao.getTotalExpensesByMonth(month)
    suspend fun insertExpense(expense: ExpenseEntity) = expenseDao.insert(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.update(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.delete(expense)

    fun getBudgetsByMonth(month: String): Flow<List<BudgetEntity>> = budgetDao.getBudgetsByMonth(month)
    suspend fun insertBudget(budget: BudgetEntity) = budgetDao.insert(budget)
    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.update(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.delete(budget)

    fun getIncomeBudgetByMonth(month: String): Flow<IncomeBudgetEntity?> = incomeBudgetDao.getIncomeBudgetByMonth(month)
    suspend fun insertIncomeBudget(incomeBudget: IncomeBudgetEntity) = incomeBudgetDao.insert(incomeBudget)
    suspend fun updateIncomeBudget(incomeBudget: IncomeBudgetEntity) = incomeBudgetDao.update(incomeBudget)
    suspend fun deleteIncomeBudget(incomeBudget: IncomeBudgetEntity) = incomeBudgetDao.delete(incomeBudget)
}

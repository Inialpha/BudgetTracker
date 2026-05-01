package com.example.budgettracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.data.db.*
import com.example.budgettracker.data.repository.BudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CategoryComparison(
    val category: String,
    val budgeted: Double,
    val actual: Double,
    val diff: Double
)

data class AnalysisSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val budgetedIncome: Double,
    val incomeDiff: Double,
    val categoryComparisons: List<CategoryComparison>
)

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        database.incomeDao(),
        database.expenseDao(),
        database.budgetDao(),
        database.incomeBudgetDao()
    )

    private val _currentMonth = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    )
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    val incomeList: StateFlow<List<IncomeEntity>> = _currentMonth.flatMapLatest { month ->
        repository.getIncomeByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseList: StateFlow<List<ExpenseEntity>> = _currentMonth.flatMapLatest { month ->
        repository.getExpensesByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = _currentMonth.flatMapLatest { month ->
        repository.getBudgetsByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeBudget: StateFlow<IncomeBudgetEntity?> = _currentMonth.flatMapLatest { month ->
        repository.getIncomeBudgetByMonth(month)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalIncome: StateFlow<Double> = _currentMonth.flatMapLatest { month ->
        repository.getTotalIncomeByMonth(month).map { it ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = _currentMonth.flatMapLatest { month ->
        repository.getTotalExpensesByMonth(month).map { it ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = combine(totalIncome, totalExpenses) { income, expenses ->
        income - expenses
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val analysisSummary: StateFlow<AnalysisSummary> = combine(
        totalIncome, totalExpenses, balance, incomeBudget, budgets, expenseList
    ) { values ->
        val income = values[0] as Double
        val expenses = values[1] as Double
        val bal = values[2] as Double
        @Suppress("UNCHECKED_CAST")
        val ib = values[3] as IncomeBudgetEntity?
        @Suppress("UNCHECKED_CAST")
        val budgetList = values[4] as List<BudgetEntity>
        @Suppress("UNCHECKED_CAST")
        val expList = values[5] as List<ExpenseEntity>

        val budgetedIncome = ib?.expectedIncome ?: 0.0
        val incomeDiff = income - budgetedIncome

        val expensesByCategory = expList.groupBy { it.category }
            .mapValues { (_, v) -> v.sumOf { it.amount } }

        val allCategories = (budgetList.map { it.category } + expensesByCategory.keys).toSet()
        val comparisons = allCategories.map { cat ->
            val budgeted = budgetList.find { it.category == cat }?.budgetAmount ?: 0.0
            val actual = expensesByCategory[cat] ?: 0.0
            CategoryComparison(cat, budgeted, actual, budgeted - actual)
        }

        AnalysisSummary(income, expenses, bal, budgetedIncome, incomeDiff, comparisons)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalysisSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
    )

    fun setMonth(month: String) {
        _currentMonth.value = month
    }

    fun addIncome(amount: Double, source: String, date: String) {
        viewModelScope.launch {
            val month = date.substring(0, 7)
            repository.insertIncome(IncomeEntity(amount = amount, source = source, date = date, month = month))
        }
    }

    fun updateIncome(income: IncomeEntity) {
        viewModelScope.launch { repository.updateIncome(income) }
    }

    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch { repository.deleteIncome(income) }
    }

    fun addExpense(amount: Double, category: String, description: String, date: String) {
        viewModelScope.launch {
            val month = date.substring(0, 7)
            repository.insertExpense(ExpenseEntity(amount = amount, category = category, description = description, date = date, month = month))
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.updateExpense(expense) }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun addBudget(category: String, budgetAmount: Double, month: String) {
        viewModelScope.launch {
            repository.insertBudget(BudgetEntity(category = category, budgetAmount = budgetAmount, month = month))
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch { repository.updateBudget(budget) }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch { repository.deleteBudget(budget) }
    }

    fun setIncomeBudget(expectedIncome: Double, month: String) {
        viewModelScope.launch {
            val existing = incomeBudget.value
            if (existing != null) {
                repository.updateIncomeBudget(existing.copy(expectedIncome = expectedIncome))
            } else {
                repository.insertIncomeBudget(IncomeBudgetEntity(expectedIncome = expectedIncome, month = month))
            }
        }
    }
}

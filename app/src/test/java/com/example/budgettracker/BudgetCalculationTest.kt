package com.example.budgettracker

import com.example.budgettracker.viewmodel.CategoryComparison
import org.junit.Assert.*
import org.junit.Test

class BudgetCalculationTest {

    @Test
    fun testBudgetVsActual_underBudget() {
        val budgeted = 500.0
        val actual = 300.0
        val diff = budgeted - actual
        assertEquals(200.0, diff, 0.01)
        assertTrue(diff > 0)
    }

    @Test
    fun testBudgetVsActual_overBudget() {
        val budgeted = 200.0
        val actual = 350.0
        val diff = budgeted - actual
        assertEquals(-150.0, diff, 0.01)
        assertTrue(diff < 0)
    }

    @Test
    fun testCategoryComparison() {
        val comparison = CategoryComparison("Food", 300.0, 250.0, 50.0)
        assertEquals("Food", comparison.category)
        assertEquals(300.0, comparison.budgeted, 0.01)
        assertEquals(250.0, comparison.actual, 0.01)
        assertEquals(50.0, comparison.diff, 0.01)
    }

    @Test
    fun testIncomeDiff_positive() {
        val budgetedIncome = 3000.0
        val actualIncome = 3500.0
        val diff = actualIncome - budgetedIncome
        assertEquals(500.0, diff, 0.01)
    }

    @Test
    fun testIncomeDiff_negative() {
        val budgetedIncome = 3000.0
        val actualIncome = 2500.0
        val diff = actualIncome - budgetedIncome
        assertEquals(-500.0, diff, 0.01)
    }

    @Test
    fun testBalance_positive() {
        val income = 5000.0
        val expenses = 3000.0
        val balance = income - expenses
        assertEquals(2000.0, balance, 0.01)
    }

    @Test
    fun testBalance_negative() {
        val income = 2000.0
        val expenses = 3000.0
        val balance = income - expenses
        assertEquals(-1000.0, balance, 0.01)
    }
}

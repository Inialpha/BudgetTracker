package com.example.budgettracker

import org.junit.Assert.*
import org.junit.Test

class MonthlyTotalsTest {

    @Test
    fun testTotalIncome_empty() {
        val incomes = emptyList<Double>()
        val total = incomes.sum()
        assertEquals(0.0, total, 0.01)
    }

    @Test
    fun testTotalIncome_multiple() {
        val incomes = listOf(1000.0, 2000.0, 500.0)
        val total = incomes.sum()
        assertEquals(3500.0, total, 0.01)
    }

    @Test
    fun testTotalExpenses_empty() {
        val expenses = emptyList<Double>()
        val total = expenses.sum()
        assertEquals(0.0, total, 0.01)
    }

    @Test
    fun testTotalExpenses_multiple() {
        val expenses = listOf(100.0, 200.0, 50.0, 75.0)
        val total = expenses.sum()
        assertEquals(425.0, total, 0.01)
    }

    @Test
    fun testMonthExtraction() {
        val date = "2024-03-15"
        val month = date.substring(0, 7)
        assertEquals("2024-03", month)
    }

    @Test
    fun testMonthExtraction_december() {
        val date = "2024-12-31"
        val month = date.substring(0, 7)
        assertEquals("2024-12", month)
    }

    @Test
    fun testExpensesByCategory() {
        data class SimpleExpense(val category: String, val amount: Double)
        val expenses = listOf(
            SimpleExpense("Food", 100.0),
            SimpleExpense("Food", 50.0),
            SimpleExpense("Transport", 30.0)
        )
        val byCategory = expenses.groupBy { it.category }.mapValues { (_, v) -> v.sumOf { it.amount } }
        assertEquals(150.0, byCategory["Food"]!!, 0.01)
        assertEquals(30.0, byCategory["Transport"]!!, 0.01)
    }
}

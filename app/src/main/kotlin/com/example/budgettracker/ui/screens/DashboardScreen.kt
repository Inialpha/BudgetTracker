package com.example.budgettracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgettracker.viewmodel.BudgetViewModel
import java.time.YearMonth

@Composable
fun DashboardScreen(viewModel: BudgetViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val summary by viewModel.analysisSummary.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Budget Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                val ym = YearMonth.parse(currentMonth)
                viewModel.setMonth(ym.minusMonths(1).toString())
            }) { Icon(Icons.Filled.ArrowBack, "Previous month") }
            Text(currentMonth, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = {
                val ym = YearMonth.parse(currentMonth)
                viewModel.setMonth(ym.plusMonths(1).toString())
            }) { Icon(Icons.Filled.ArrowForward, "Next month") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("Income", summary.totalIncome, Color(0xFF4CAF50), modifier = Modifier.weight(1f))
            SummaryCard("Expenses", summary.totalExpenses, Color(0xFFF44336), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        SummaryCard("Balance", summary.balance, if (summary.balance >= 0) Color(0xFF2196F3) else Color(0xFFFF5722), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        if (summary.budgetedIncome > 0) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Income Budget", style = MaterialTheme.typography.titleMedium)
                    Text("Budgeted: ${"%.2f".format(summary.budgetedIncome)}")
                    Text("Actual: ${"%.2f".format(summary.totalIncome)}")
                    val diff = summary.incomeDiff
                    Text("Diff: ${"%.2f".format(diff)}", color = if (diff >= 0) Color(0xFF4CAF50) else Color(0xFFF44336))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (summary.categoryComparisons.isNotEmpty()) {
            Text("Budget vs Actual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(summary.categoryComparisons) { comparison ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(comparison.category, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Budgeted: ${"%.2f".format(comparison.budgeted)}")
                                Text("Actual: ${"%.2f".format(comparison.actual)}")
                            }
                            Text(
                                "Remaining: ${"%.2f".format(comparison.diff)}",
                                color = if (comparison.diff >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text("${"%.2f".format(amount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

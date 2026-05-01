package com.example.budgettracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.budgettracker.data.db.BudgetEntity
import com.example.budgettracker.viewmodel.BudgetViewModel

@Composable
fun BudgetScreen(viewModel: BudgetViewModel) {
    val budgets by viewModel.budgets.collectAsState()
    val incomeBudget by viewModel.incomeBudget.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showIncomeBudgetDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<BudgetEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, "Add Budget")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Budget - $currentMonth", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Income Budget", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (incomeBudget != null) "Expected: ${"%.2f".format(incomeBudget!!.expectedIncome)}"
                            else "Not set"
                        )
                    }
                    IconButton(onClick = { showIncomeBudgetDialog = true }) {
                        Icon(if (incomeBudget != null) Icons.Filled.Edit else Icons.Filled.Add, "Set Income Budget")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (budgets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No budget entries for this month")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(budgets) { budget ->
                        BudgetCard(
                            budget = budget,
                            onEdit = { editingBudget = it },
                            onDelete = { viewModel.deleteBudget(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        BudgetDialog(
            title = "Add Budget",
            onDismiss = { showAddDialog = false },
            onConfirm = { category, amount ->
                viewModel.addBudget(category, amount, currentMonth)
                showAddDialog = false
            }
        )
    }

    if (showIncomeBudgetDialog) {
        IncomeBudgetDialog(
            currentValue = incomeBudget?.expectedIncome?.toString() ?: "",
            onDismiss = { showIncomeBudgetDialog = false },
            onConfirm = { amount ->
                viewModel.setIncomeBudget(amount, currentMonth)
                showIncomeBudgetDialog = false
            }
        )
    }

    editingBudget?.let { budget ->
        BudgetDialog(
            title = "Edit Budget",
            initialCategory = budget.category,
            initialAmount = budget.budgetAmount.toString(),
            onDismiss = { editingBudget = null },
            onConfirm = { category, amount ->
                viewModel.updateBudget(budget.copy(category = category, budgetAmount = amount))
                editingBudget = null
            }
        )
    }
}

@Composable
fun BudgetCard(budget: BudgetEntity, onEdit: (BudgetEntity) -> Unit, onDelete: (BudgetEntity) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(budget.category, style = MaterialTheme.typography.titleMedium)
                Text("${"%.2f".format(budget.budgetAmount)}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEdit(budget) }) { Icon(Icons.Filled.Edit, "Edit") }
            IconButton(onClick = { onDelete(budget) }) { Icon(Icons.Filled.Delete, "Delete") }
        }
    }
}

@Composable
fun BudgetDialog(
    title: String,
    initialCategory: String = "",
    initialAmount: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf(initialCategory) }
    var amount by remember { mutableStateOf(initialAmount) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it; amountError = false }, label = { Text("Budget Amount") }, isError = amountError, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt != null && category.isNotBlank()) {
                    onConfirm(category, amt)
                } else {
                    amountError = amt == null
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun IncomeBudgetDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(currentValue) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Income Budget") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; amountError = false },
                label = { Text("Expected Income") },
                isError = amountError,
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt != null) {
                    onConfirm(amt)
                } else {
                    amountError = true
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

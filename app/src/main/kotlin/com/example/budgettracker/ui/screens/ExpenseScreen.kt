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
import com.example.budgettracker.data.db.ExpenseEntity
import com.example.budgettracker.viewmodel.BudgetViewModel
import java.time.LocalDate

@Composable
fun ExpenseScreen(viewModel: BudgetViewModel) {
    val expenseList by viewModel.expenseList.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Expenses - $currentMonth", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (expenseList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expense entries for this month")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(expenseList) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onEdit = { editingExpense = it },
                            onDelete = { viewModel.deleteExpense(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ExpenseDialog(
            title = "Add Expense",
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, category, description, date ->
                viewModel.addExpense(amount, category, description, date)
                showAddDialog = false
            }
        )
    }

    editingExpense?.let { expense ->
        ExpenseDialog(
            title = "Edit Expense",
            initialAmount = expense.amount.toString(),
            initialCategory = expense.category,
            initialDescription = expense.description,
            initialDate = expense.date,
            onDismiss = { editingExpense = null },
            onConfirm = { amount, category, description, date ->
                viewModel.updateExpense(expense.copy(amount = amount, category = category, description = description, date = date, month = date.substring(0, 7)))
                editingExpense = null
            }
        )
    }
}

@Composable
fun ExpenseCard(expense: ExpenseEntity, onEdit: (ExpenseEntity) -> Unit, onDelete: (ExpenseEntity) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category, style = MaterialTheme.typography.titleMedium)
                if (expense.description.isNotBlank()) Text(expense.description, style = MaterialTheme.typography.bodySmall)
                Text(expense.date, style = MaterialTheme.typography.bodySmall)
                Text("${"%.2f".format(expense.amount)}", style = MaterialTheme.typography.titleSmall)
            }
            IconButton(onClick = { onEdit(expense) }) { Icon(Icons.Filled.Edit, "Edit") }
            IconButton(onClick = { onDelete(expense) }) { Icon(Icons.Filled.Delete, "Delete") }
        }
    }
}

@Composable
fun ExpenseDialog(
    title: String,
    initialAmount: String = "",
    initialCategory: String = "",
    initialDescription: String = "",
    initialDate: String = LocalDate.now().toString(),
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String, String) -> Unit
) {
    var amount by remember { mutableStateOf(initialAmount) }
    var category by remember { mutableStateOf(initialCategory) }
    var description by remember { mutableStateOf(initialDescription) }
    var date by remember { mutableStateOf(initialDate) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it; amountError = false }, label = { Text("Amount") }, isError = amountError, singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true)
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt != null && category.isNotBlank() && date.isNotBlank()) {
                    onConfirm(amt, category, description, date)
                } else {
                    amountError = amt == null
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

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
import com.example.budgettracker.data.db.IncomeEntity
import com.example.budgettracker.viewmodel.BudgetViewModel
import java.time.LocalDate

@Composable
fun IncomeScreen(viewModel: BudgetViewModel) {
    val incomeList by viewModel.incomeList.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingIncome by remember { mutableStateOf<IncomeEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, "Add Income")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Income - $currentMonth", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (incomeList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No income entries for this month")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(incomeList) { income ->
                        IncomeCard(
                            income = income,
                            onEdit = { editingIncome = it },
                            onDelete = { viewModel.deleteIncome(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        IncomeDialog(
            title = "Add Income",
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, source, date ->
                viewModel.addIncome(amount, source, date)
                showAddDialog = false
            }
        )
    }

    editingIncome?.let { income ->
        IncomeDialog(
            title = "Edit Income",
            initialAmount = income.amount.toString(),
            initialSource = income.source,
            initialDate = income.date,
            onDismiss = { editingIncome = null },
            onConfirm = { amount, source, date ->
                viewModel.updateIncome(income.copy(amount = amount, source = source, date = date, month = date.substring(0, 7)))
                editingIncome = null
            }
        )
    }
}

@Composable
fun IncomeCard(income: IncomeEntity, onEdit: (IncomeEntity) -> Unit, onDelete: (IncomeEntity) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(income.source, style = MaterialTheme.typography.titleMedium)
                Text(income.date, style = MaterialTheme.typography.bodySmall)
                Text("${"%.2f".format(income.amount)}", style = MaterialTheme.typography.titleSmall)
            }
            IconButton(onClick = { onEdit(income) }) { Icon(Icons.Filled.Edit, "Edit") }
            IconButton(onClick = { onDelete(income) }) { Icon(Icons.Filled.Delete, "Delete") }
        }
    }
}

@Composable
fun IncomeDialog(
    title: String,
    initialAmount: String = "",
    initialSource: String = "",
    initialDate: String = LocalDate.now().toString(),
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amount by remember { mutableStateOf(initialAmount) }
    var source by remember { mutableStateOf(initialSource) }
    var date by remember { mutableStateOf(initialDate) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = false },
                    label = { Text("Amount") },
                    isError = amountError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Source") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt != null && source.isNotBlank() && date.isNotBlank()) {
                    onConfirm(amt, source, date)
                } else {
                    amountError = amt == null
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

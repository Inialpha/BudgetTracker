package com.example.budgettracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.budgettracker.ui.screens.*
import com.example.budgettracker.viewmodel.BudgetViewModel

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Income : Screen("income", "Income")
    object Expenses : Screen("expenses", "Expenses")
    object Budget : Screen("budget", "Budget")
}

@Composable
fun BudgetTrackerApp(viewModel: BudgetViewModel) {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.Income, Screen.Expenses, Screen.Budget)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val icon = when (screen) {
                        Screen.Dashboard -> Icons.Filled.Dashboard
                        Screen.Income -> Icons.Filled.AttachMoney
                        Screen.Expenses -> Icons.Filled.ShoppingCart
                        Screen.Budget -> Icons.Filled.AccountBalance
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(Screen.Income.route) { IncomeScreen(viewModel) }
            composable(Screen.Expenses.route) { ExpenseScreen(viewModel) }
            composable(Screen.Budget.route) { BudgetScreen(viewModel) }
        }
    }
}

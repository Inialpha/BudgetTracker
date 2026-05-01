package com.example.budgettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettracker.ui.navigation.BudgetTrackerApp
import com.example.budgettracker.ui.theme.BudgetTrackerTheme
import com.example.budgettracker.viewmodel.BudgetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetTrackerTheme {
                val viewModel: BudgetViewModel = viewModel()
                BudgetTrackerApp(viewModel)
            }
        }
    }
}

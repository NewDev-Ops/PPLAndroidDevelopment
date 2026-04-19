package com.pocketpal.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pocketpal.ui.components.AddTransactionSheet
import com.pocketpal.ui.screens.home.AddTransactionState
import com.pocketpal.ui.screens.home.AddTransactionViewModel
import com.pocketpal.ui.screens.home.HomeScreen
import com.pocketpal.ui.screens.home.HomeViewModel
import com.pocketpal.ui.screens.analytics.AnalyticsScreen
import com.pocketpal.ui.screens.analytics.AnalyticsViewModel
import com.pocketpal.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Analytics : Screen("analytics", "Analytics", Icons.Default.PieChart)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Analytics,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketPalNavHost(
    addTransactionViewModel: AddTransactionViewModel,
    homeViewModel: HomeViewModel,
    analyticsViewModel: AnalyticsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val state by addTransactionViewModel.state.collectAsState()
    val expenseCategories by addTransactionViewModel.categories.collectAsState()
    val incomeCategories by addTransactionViewModel.incomeCategories.collectAsState()
    
    var showAddSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = homeViewModel, transactionViewModel = addTransactionViewModel)
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
    
    // Add Transaction Bottom Sheet
    AddTransactionSheet(
        isVisible = showAddSheet,
        onDismiss = {
            showAddSheet = false
            addTransactionViewModel.resetState()
        },
        state = state,
        expenseCategories = expenseCategories,
        incomeCategories = incomeCategories,
        onAmountChange = addTransactionViewModel::updateAmount,
        onCategoryChange = addTransactionViewModel::updateCategory,
        onTypeChange = addTransactionViewModel::updateType,
        onNoteChange = addTransactionViewModel::updateNote,
        onDateChange = addTransactionViewModel::updateDate,
        onSave = {
            addTransactionViewModel.saveTransaction {
                showAddSheet = false
                addTransactionViewModel.resetState()
            }
        }
    )
}
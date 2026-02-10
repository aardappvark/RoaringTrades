package com.roaringtrades.game.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.roaringtrades.game.ui.screens.GameOverScreen
import com.roaringtrades.game.ui.screens.GarageScreen
import com.roaringtrades.game.ui.screens.MarketScreen
import com.roaringtrades.game.ui.screens.StatusScreen
import com.roaringtrades.game.ui.screens.TravelScreen
import com.roaringtrades.game.ui.viewmodel.GameViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Market : Screen("market", "Market", Icons.Filled.ShoppingCart)
    object Travel : Screen("travel", "Travel", Icons.Filled.DirectionsCar)
    object Garage : Screen("garage", "Garage", Icons.Filled.LocalShipping)
    object Status : Screen("status", "Status", Icons.Filled.Leaderboard)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Market, Screen.Travel, Screen.Garage, Screen.Status)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gameState by viewModel.gameState.collectAsState()

    // Show game over screen
    if (gameState.gameOver) {
        GameOverScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Day ${gameState.day}/${gameState.maxDays}  \u2022  \$${String.format("%,d", gameState.cash)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    val badgeCount = if (screen == Screen.Status) gameState.unclaimedCount else 0
                    NavigationBarItem(
                        icon = {
                            if (badgeCount > 0) {
                                BadgedBox(badge = { Badge { Text("$badgeCount") } }) {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Market.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Market.route) {
                MarketScreen(viewModel = viewModel)
            }
            composable(Screen.Travel.route) {
                TravelScreen(
                    viewModel = viewModel,
                    onTraveled = {
                        navController.navigate(Screen.Market.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Garage.route) {
                GarageScreen(viewModel = viewModel)
            }
            composable(Screen.Status.route) {
                StatusScreen(viewModel = viewModel)
            }
        }
    }
}

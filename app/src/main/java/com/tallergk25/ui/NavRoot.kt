package com.tallergk25.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavRoot() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "orders") {
        composable("orders") {
            OrdersScreen(
                onNewOrder = { id -> nav.navigate("orderDetail/$id") },
                onOrderClick = { id -> nav.navigate("orderDetail/$id") }
            )
        }
        composable(
            route = "orderDetail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            val orderId = backStack.arguments?.getLong("orderId") ?: 0L
            OrderDetailScreen(orderId = orderId)
        }
    }
}

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

    NavHost(
        navController = nav,
        startDestination = "orders"
    ) {
        composable("orders") {
            OrdersScreen(
                onOrderClick = { id -> nav.navigate("orderDetail/$id") }
            )
        }
        composable(
            "orderDetail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(orderId = id)
        }
    }
}

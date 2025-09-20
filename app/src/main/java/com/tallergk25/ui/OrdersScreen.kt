package com.tallergk25.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tallergk25.data.FileRepo

@Composable
fun OrdersScreen(
    onNewOrder: (Long) -> Unit,
    onOrderClick: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    var orders by remember { mutableStateOf(repo.listOrders()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Órdenes de trabajo") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val id = repo.newOrder()
                orders = repo.listOrders()
                onNewOrder(id)
            }) { Text("+") }
        }
    ) { p ->
        LazyColumn(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            items(orders) { o ->
                Card(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .clickable { onOrderClick(o.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("OT #${o.id}")
                        Text("Cliente: ${o.customer.name}")
                        Text("Vehículo: ${o.vehicle.brand} ${o.vehicle.model} - ${o.vehicle.plate}")
                    }
                }
            }
        }
    }
}

package com.tallergk25.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tallergk25.data.FileRepo

@Composable
fun OrderDetailScreen(orderId: Long) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }
    var order by remember { mutableStateOf(repo.getOrder(orderId)) }

    if (order == null) {
        Text("Orden no encontrada")
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Orden #${order!!.id}") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Cliente: ${order!!.customer.name}")
            Text("Vehículo: ${order!!.vehicle.brand} ${order!!.vehicle.model}")
            Text("Matrícula: ${order!!.vehicle.plate}")

            Divider()

            Text("Servicios: %.2f €".format(order!!.subtotalServices))
            Text("Piezas: %.2f €".format(order!!.subtotalParts))
            Text("Base: %.2f €".format(order!!.totalBase))
            Text("Total con IVA: %.2f €".format(order!!.totalWithVat))
        }
    }
}

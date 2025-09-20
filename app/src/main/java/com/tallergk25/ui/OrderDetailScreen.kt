package com.tallergk25.ui

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tallergk25.data.FileRepo
import java.io.File

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
                .verticalScroll(rememberScrollState())
        ) {
            Text("Cliente: ${order!!.customer.name}")
            Text("Vehículo: ${order!!.vehicle.brand} ${order!!.vehicle.model}")
            Text("Matrícula: ${order!!.vehicle.plate}")

            Spacer(Modifier.height(16.dp))

            // Foto del coche (si existe)
            if (order!!.photos.isNotEmpty()) {
                val photo = order!!.photos.first()
                Image(
                    painter = rememberAsyncImagePainter(File(photo.path)),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200dp)
                )
            } else {
                Text("Sin fotos aún")
            }

            Spacer(Modifier.height(16.dp))

            // Totales
            Text("Servicios: %.2f €".format(order!!.subtotalServices))
            Text("Piezas: %.2f €".format(order!!.subtotalParts))
            Text("Base: %.2f €".format(order!!.totalBase))
            Text("Total con IVA: %.2f €".format(order!!.totalWithVat))
        }
    }
}

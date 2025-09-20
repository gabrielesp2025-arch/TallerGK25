package com.tallergk25.ui

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tallergk25.data.*
import com.tallergk25.util.createImageFile
import com.tallergk25.util.uriForFile
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(orderId: String) {
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { FileRepo(app) }

    val id = orderId.toLongOrNull() ?: repo.listOrders().firstOrNull()?.id ?: return
    var order by remember { mutableStateOf(repo.getOrder(id)) } ?: return

    // Cámara: estado para fichero pendiente
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    // Launchers para cada etapa de foto
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) {
            pendingPhotoFile?.let { f ->
                repo.addPhoto(id, PhotoStage.BEFORE, f.absolutePath)
                order = repo.getOrder(id)
            }
        }
        pendingPhotoFile = null
    }

    fun takePhoto() {
        val f = createImageFile(app, "OT${id}_BEFORE")
        val uri = uriForFile(app, f)
        pendingPhotoFile = f
        launcher.launch(uri)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("OT #$id") }) }
    ) { p ->
        val o = order ?: return@Scaffold
        Column(
            Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cliente: ${o.customer.name}")
            Text("Vehículo: ${o.vehicle.brand} ${o.vehicle.model} (${o.vehicle.plate})")

            Divider()
            Text("Selecciona vehículo", style = MaterialTheme.typography.titleMedium)

            // 1) Marca
            SimpleDropdown(
                label = "Marca",
                options = CarCatalog.brands(app),
                selected = o.vehicle.brand
            ) { selectedBrand ->
                repo.updateVehicle(id, brand = selectedBrand, model = "", year = null, engineCode = null)
                order = repo.getOrder(id)
            }

            // 2) Modelo (depende de la marca)
            SimpleDropdown(
                label = "Modelo",
                options = CarCatalog.modelsFor(app, order?.vehicle?.brand),
                selected = order?.vehicle?.model
            ) { selectedModel ->
                repo.updateVehicle(id, model = selectedModel, year = null, engineCode = null)
                order = repo.getOrder(id)
            }

            // 3) Año (depende de marca+modelo)
            val yearOptions = CarCatalog.yearsFor(app, order?.vehicle?.brand, order?.vehicle?.model)
                .map { it.toString() }
            SimpleDropdown(
                label = "Año",
                options = yearOptions,
                selected = order?.vehicle?.year?.toString()
            ) { selectedYearStr ->
                val selectedYear = selectedYearStr.toInt()
                val code = CarCatalog.engineCodeFor(
                    app,
                    brand = order?.vehicle?.brand ?: return@SimpleDropdown,
                    model = order?.vehicle?.model ?: return@SimpleDropdown,
                    year = selectedYear
                )
                repo.updateVehicle(id, year = selectedYear, engineCode = code)
                order = repo.getOrder(id)
            }

            val v = order!!.vehicle
            Text("Seleccionado: ${v.brand} ${v.model} ${v.year ?: ""} ${v.engineCode?.let { "· Motor: $it" } ?: ""}")

            Divider()
            Text("Fotos (demo: solo 'Antes')", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { takePhoto() }) { Text("Tomar foto ANTES") }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(o.photos) { ph ->
                    val bm = BitmapFactory.decodeFile(ph.path)
                    if (bm != null) {
                        Image(
                            bitmap = bm.asImageBitmap(),
                            contentDescription = ph.stage.name,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
            }

            Divider()
            Text("Totales", style = MaterialTheme.typography.titleMedium)
            Text("Servicios: %.2f €".format(o.subtotalServices))
            Text("Piezas: %.2f €".format(o.subtotalParts))
            Text("Base: %.2f €".format(o.totalBase))
            Text("IVA ${o.vatPct.toInt()}%: %.2f €".format(o.totalBase * o.vatPct / 100.0))
            Text("TOTAL: %.2f €".format(o.totalWithVat))
        }
    }
}

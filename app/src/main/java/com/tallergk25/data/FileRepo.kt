package com.tallergk25.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class FileRepo(private val ctx: Context) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val file: File get() = File(ctx.filesDir, "taller_data.json")

    private var seq = AtomicLong(System.currentTimeMillis())
    private var state: StorageState = StorageState()

    init {
        load()
        // Si no hay órdenes, crea una de demo para que la app muestre algo
        if (state.orders.isEmpty()) {
            newOrder()
        }
    }

    @Synchronized
    fun load() {
        state = if (file.exists()) {
            runCatching { json.decodeFromString<StorageState>(file.readText()) }
                .getOrElse { StorageState() }
        } else StorageState()
    }

    @Synchronized
    fun save() {
        file.writeText(json.encodeToString(state))
    }

    @Synchronized
    fun listOrders(): List<Order> = state.orders.sortedByDescending { it.id }

    @Synchronized
    fun getOrder(id: Long): Order? = state.orders.find { it.id == id }

    @Synchronized
    fun newOrder(): Long {
        val id = seq.incrementAndGet()
        val cust = Customer(id = seq.incrementAndGet(), name = "Cliente Demo")
        val veh = Vehicle(
            id = seq.incrementAndGet(),
            customerId = cust.id,
            brand = "Genérico",
            model = "Modelo",
            plate = "0000-XXX"
        )
        val o = Order(id = id, customer = cust, vehicle = veh)
        state = state.copy(orders = state.orders + o)
        save()
        return id
    }

    @Synchronized
    fun updateVehicle(
        orderId: Long,
        brand: String? = null,
        model: String? = null,
        year: Int? = null,
        plate: String? = null,
        engineCode: String? = null
    ) {
        val o = getOrder(orderId) ?: return
        val v = o.vehicle.copy(
            brand = brand ?: o.vehicle.brand,
            model = model ?: o.vehicle.model,
            year = year ?: o.vehicle.year,
            plate = plate ?: o.vehicle.plate,
            engineCode = engineCode ?: o.vehicle.engineCode
        )
        update(o.copy(vehicle = v))
    }

    @Synchronized
    private fun update(n: Order) {
        state = state.copy(orders = state.orders.map { if (it.id == n.id) n else it })
        save()
    }
}

@Serializable
data class StorageState(
    val orders: List<Order> = emptyList()
)

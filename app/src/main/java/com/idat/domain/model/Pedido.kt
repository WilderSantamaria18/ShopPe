package com.idat.domain.model

import java.util.Date

data class Pedido(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "Pendiente", // Pendiente, Procesando, En camino, Entregado, Cancelados
    val total: Double = 0.0,
    val items: List<ItemCarrito> = emptyList(),
    val userId: String = ""
)

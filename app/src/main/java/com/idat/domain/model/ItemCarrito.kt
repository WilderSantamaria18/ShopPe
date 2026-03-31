package com.idat.domain.model

data class ItemCarrito(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val categoria: String,
    val imagen: String,
    val cantidad: Int = 1
)

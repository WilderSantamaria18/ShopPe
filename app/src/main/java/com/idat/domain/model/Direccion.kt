package com.idat.domain.model

data class Direccion(
    val id: String = "",
    val tag: String = "", // e.g., "Casa", "Oficina", "Departamento"
    val direccion: String = "",
    val destinatario: String = "",
    val telefono: String = "",
    val esPredeterminada: Boolean = false,
    val tipoIcono: String = "home" // "home", "work", "apartment"
)

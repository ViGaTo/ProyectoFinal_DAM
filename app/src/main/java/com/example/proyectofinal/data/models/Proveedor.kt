package com.example.proyectofinal.data.models

import java.io.Serializable

data class Proveedor(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val estado: String = "",
    val calificacion: Int = 0,
    val notas: String = ""
): Serializable

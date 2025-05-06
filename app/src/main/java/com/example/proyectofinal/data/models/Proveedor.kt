package com.example.proyectofinal.data.models

import java.io.Serializable

data class Proveedor(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val estado: String = "",
    val calificacion: Float = 0f,
    val notas: String = ""
): Serializable

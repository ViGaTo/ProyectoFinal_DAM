package com.example.proyectofinal.data.models

import java.io.Serializable

data class Producto(
    val id: Int = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val categoria: String = "",
    val cantidad: Int = 0
): Serializable

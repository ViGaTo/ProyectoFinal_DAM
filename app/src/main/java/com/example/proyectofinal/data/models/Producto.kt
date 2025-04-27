package com.example.proyectofinal.data.models

data class Producto(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val imagen: String,
    val categoria: String,
    val cantidad: Int
)

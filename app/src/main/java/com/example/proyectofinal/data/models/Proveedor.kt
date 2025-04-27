package com.example.proyectofinal.data.models

data class Proveedor(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val estado: String,
    val calificacion: Int,
    val notas: String
)

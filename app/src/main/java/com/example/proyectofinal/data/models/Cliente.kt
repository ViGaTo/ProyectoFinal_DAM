package com.example.proyectofinal.data.models

data class Cliente(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val frecuencia: String,
    val notas: String
)

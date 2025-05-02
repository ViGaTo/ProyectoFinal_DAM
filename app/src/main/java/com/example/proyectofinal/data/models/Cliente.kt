package com.example.proyectofinal.data.models

import java.io.Serializable

data class Cliente(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val frecuencia: String = "",
    val notas: String = ""
): Serializable

package com.example.proyectofinal.data.models

data class Usuario(
    val email: String = "",
    var admin: Boolean = false,
    var activado: Boolean = true
)

package com.example.proyectofinal.data.models

data class Salida(
    val id: Int,
    val id_producto: String,
    val id_cliente: Int,
    val cantidad_producto: Int,
    val precio: Double,
    val fecha_salida: String,
    val estado: String,
    val email_autor: String,
    val email_ultimoAutor: String
)

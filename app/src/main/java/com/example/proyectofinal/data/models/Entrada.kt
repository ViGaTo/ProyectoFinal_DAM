package com.example.proyectofinal.data.models

data class Entrada(
    val id_producto: String,
    val id_proveedor: Int,
    val cantidad_producto: Int,
    val precio: Double,
    val fecha_entrada: String,
    val hora_entrada: String,
    val estado: String,
    val email_autor: String,
    val email_ultimoAutor: String
)

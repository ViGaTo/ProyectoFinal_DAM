package com.example.proyectofinal.data.models

import java.io.Serializable

data class Entrada(
    val nombre: String = "",
    val id_producto: String = "",
    val email_proveedor: String = "",
    val cantidad_producto: Int = 0,
    val precio: Double = 0.0,
    val fecha_entrada: String = "",
    val hora_entrada: String = "",
    val estado: String = "",
    val notas: String = "",
    val email_autor: String = "",
    val email_ultimoAutor: String = ""
): Serializable

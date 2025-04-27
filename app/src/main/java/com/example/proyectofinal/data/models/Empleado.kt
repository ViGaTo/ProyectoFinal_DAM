package com.example.proyectofinal.data.models

import java.io.Serializable

data class Empleado(
    val id: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val telefono: String = "",
    val ciudad: String = "",
    val fecha_nacimiento: String = "",
    val estado: String = "",
    val puesto: String = "",
    val salario: Float = 0F,
    val fechaContratacion: String = "",
    val fechaFinContrato: String = "",
    val notas: String = ""
): Serializable
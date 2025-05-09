package com.example.proyectofinal.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductoAPI(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val titulo: String,
    @SerializedName("description") val descripcion: String,
    @SerializedName("price") val precio: Double,
    @SerializedName("image") val imagen: String,
    @SerializedName("category") val categoria: String,
): Serializable

package com.example.proyectofinal.data.providers.api

import com.example.proyectofinal.data.models.ProductoAPI
import retrofit2.Response
import retrofit2.http.GET

interface ProductosInterfaz {
    @GET("products/")
    suspend fun getProductos(): Response<List<ProductoAPI>>
}
package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.data.providers.repository.ListaProductoRepository

class ListaProductoViewModel {
    val repository = ListaProductoRepository()
    val _productos = MutableLiveData<List<Producto>>()

    val productos = _productos
    init {
        repository.getListaProducto { lista ->
            _productos.postValue(lista)
        }
    }

    fun getListaProductoBuscador(busqueda: String) {
        repository.getProductoBuscador(busqueda) { lista ->
            _productos.postValue(lista)
        }
    }

    fun deleteProducto(producto: Producto) {
        repository.deleteProducto(producto)
    }
}
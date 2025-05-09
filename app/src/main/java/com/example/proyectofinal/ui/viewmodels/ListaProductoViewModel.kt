package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.data.providers.repository.ListaProductoRepository

class ListaProductoViewModel: ViewModel() {
    private val repository = ListaProductoRepository()
    private val _productos = MutableLiveData<List<Producto>>()

    val productos: LiveData<List<Producto>> = _productos
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

    fun addProducto(producto: Producto) {
        repository.addProducto(producto)
    }

    fun deleteProducto(producto: Producto) {
        repository.deleteProducto(producto)
    }
}
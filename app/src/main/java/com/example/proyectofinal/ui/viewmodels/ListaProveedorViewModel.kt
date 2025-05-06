package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.data.providers.repository.ListaProveedorRepository

class ListaProveedorViewModel: ViewModel() {
    private val repository = ListaProveedorRepository()
    private val _proveedores = MutableLiveData<List<Proveedor>>()

    val proveedores: MutableLiveData<List<Proveedor>> = _proveedores
    init {
        repository.getListaProveedor { lista ->
            _proveedores.postValue(lista)
        }
    }

    fun getListaProveedorBuscador(busqueda: String) {
        repository.getProveedorBuscador(busqueda) { lista ->
            _proveedores.postValue(lista)
        }
    }

    fun deleteProveedor(proveedor: Proveedor) {
        repository.deleteProveedor(proveedor)
    }
}
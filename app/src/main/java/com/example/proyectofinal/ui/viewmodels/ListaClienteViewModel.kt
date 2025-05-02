package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.providers.repository.ListaClienteRepository

class ListaClienteViewModel {
    val repository = ListaClienteRepository()
    val _clientes = MutableLiveData<List<Cliente>>()

    val clientes: MutableLiveData<List<Cliente>> = _clientes
    init {
        repository.getListaCliente { lista ->
            _clientes.postValue(lista)
        }
    }

    fun getListaClienteBuscador(busqueda: String) {
        repository.getClienteBuscador(busqueda) { lista ->
            _clientes.postValue(lista)
        }
    }

    fun deleteCliente(cliente: Cliente) {
        repository.deleteCliente(cliente)
    }
}
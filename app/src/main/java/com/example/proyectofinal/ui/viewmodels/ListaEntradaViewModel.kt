package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.data.providers.repository.ListaEntradaRepository

class ListaEntradaViewModel: ViewModel() {
    private val repository = ListaEntradaRepository()
    private val _entradas = MutableLiveData<List<Entrada>>()
    private val _entradasProveedor = MutableLiveData<List<Entrada>>()

    val entradas: LiveData<List<Entrada>> = _entradas
    val entradasProveedor: LiveData<List<Entrada>> = _entradasProveedor
    init {
        repository.getListaEntrada("Pendiente") { lista ->
            _entradas.postValue(lista)
        }
    }

    fun getListaEntradaByProveedor(emailProveedor: String) {
        repository.getListaEntradaByProveedor(emailProveedor) { lista ->
            _entradasProveedor.postValue(lista)
        }
    }

    fun getListaEntradaBuscador(busqueda: String, estado: String) {
        repository.getListaEntradaBuscador(busqueda, estado) { lista ->
            _entradas.postValue(lista)
        }
    }

    fun getListaEntrada(estado: String) {
        repository.getListaEntrada(estado) { lista ->
            _entradas.postValue(lista)
        }
    }

    fun deleteEntrada(entrada: Entrada) {
        repository.deleteEntrada(entrada)
    }
}
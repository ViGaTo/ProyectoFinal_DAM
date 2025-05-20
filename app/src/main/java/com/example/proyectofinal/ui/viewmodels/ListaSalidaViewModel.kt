package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.data.providers.repository.ListaSalidaRepository

class ListaSalidaViewModel: ViewModel() {
    private val repository = ListaSalidaRepository()
    private val _salidas = MutableLiveData<List<Salida>>()
    private val _salidasCliente = MutableLiveData<List<Salida>>()

    val salidas: LiveData<List<Salida>> = _salidas
    val salidasCliente: LiveData<List<Salida>> = _salidasCliente
    init {
        repository.getListaSalida("Pendiente") { lista ->
            _salidas.postValue(lista)
        }
    }

    fun getListaSalidaByCliente(emailCliente: String) {
        repository.getListaSalidaByCliente(emailCliente) { lista ->
            _salidasCliente.postValue(lista)
        }
    }

    fun getListaSalidaBuscador(busqueda: String, estado: String) {
        repository.getListaSalidaBuscador(busqueda, estado) { lista ->
            _salidas.postValue(lista)
        }
    }

    fun getListaSalida(estado: String) {
        repository.getListaSalida(estado) { lista ->
            _salidas.postValue(lista)
        }
    }

    fun deleteSalida(salida: Salida) {
        repository.deleteSalida(salida)
    }
}
package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.providers.repository.ListaEmpleadoRepository

class ListaEmpleadoViewModel: ViewModel() {
    private val repository = ListaEmpleadoRepository()
    private val _empleados = MutableLiveData<List<Empleado>>()
    private val _empleadosCompleta = MutableLiveData<List<Empleado>>()

    val empleados: LiveData<List<Empleado>> = _empleados
    val empleadosCompleta: LiveData<List<Empleado>> = _empleadosCompleta
    init {
        repository.getListaEmpleado("Activo") { lista ->
            _empleados.postValue(lista)
        }

        repository.getListaEmpleado { lista ->
            _empleadosCompleta.postValue(lista)
        }
    }

    fun getListaEmpleadoBuscador(busqueda: String, estado: String) {
        repository.getListaEmpleadoBuscador(busqueda, estado) { lista ->
            _empleados.postValue(lista)
        }
    }

    fun getListaEmpleado(estado: String) {
        repository.getListaEmpleado(estado) { lista ->
            _empleados.postValue(lista)
        }
    }

    fun deleteEmpleado(empleado: Empleado) {
        repository.deleteEmpleado(empleado)
    }
}
package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.providers.repository.ListaEmpleadoRepository

class ListaEmpleadoViewModel: ViewModel() {
    private val repository = ListaEmpleadoRepository()
    private val _empleados = MutableLiveData<List<Empleado>>()

    val empleados: LiveData<List<Empleado>> = _empleados
    init {
        repository.getListaEmpleado { lista ->
            _empleados.postValue(lista)
        }
    }

    fun deleteEmpleado(empleado: Empleado) {
        repository.deleteEmpleado(empleado)
    }
}
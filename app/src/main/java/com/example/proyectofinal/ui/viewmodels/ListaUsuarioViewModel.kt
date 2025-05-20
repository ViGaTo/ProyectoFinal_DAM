package com.example.proyectofinal.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.providers.repository.ListaUsuarioRepository

class ListaUsuarioViewModel: ViewModel() {
    private val repository = ListaUsuarioRepository()

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    init {
        repository.getListaUsuario { lista ->
            _usuarios.postValue(lista)
        }
    }

    fun getUsuarioByEmail(email: String, callback: (Usuario?) -> Unit) {
        repository.getUsuarioByEmail(email) { usuario ->
            callback(usuario)
        }
    }

    fun addUsuario(email: String){
        repository.addUsuario(Usuario(
            email = email
        ))
    }

    fun activamientoUsuario(usuario: Usuario){
        repository.activamientoUsuario(usuario)
    }

    fun editarUsuario(usuario: Usuario){
        repository.editarUsuario(usuario)
    }
}
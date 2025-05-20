package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaClienteRepository {
    val database = FirebaseDatabase.getInstance().getReference("clientes")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaCliente(callback: (List<Cliente>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Cliente>()
                for (nodo in snapshot.children) {
                    val cliente = nodo.getValue(Cliente::class.java)
                    if (cliente != null) {
                        lista.add(cliente)
                    }
                }

                lista.sortBy { it.nombre }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getClientes(datos: (MutableList<Cliente>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Cliente>()
                for (nodo in snapshot.children) {
                    val cliente = nodo.getValue(Cliente::class.java)
                    if (cliente != null) {
                        lista.add(cliente)
                    }
                }

                lista.sortBy { it.nombre }
                datos(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getClienteBuscador(busqueda: String, callback: (List<Cliente>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Cliente>()
                for (nodo in snapshot.children) {
                    val cliente = nodo.getValue(Cliente::class.java)
                    if (cliente != null && cliente.nombre.contains(busqueda, ignoreCase = true)) {
                        lista.add(cliente)
                    }
                }

                lista.sortBy { it.nombre }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun deleteCliente(cliente: Cliente) {
        database.child(cliente.email.encodeEmail()).removeValue()
            .addOnCompleteListener {
                System.out.println("Cliente eliminado")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar cliente")
            }
    }
}
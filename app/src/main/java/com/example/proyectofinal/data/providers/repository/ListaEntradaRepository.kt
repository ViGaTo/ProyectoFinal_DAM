package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Entrada
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaEntradaRepository {
    val database = FirebaseDatabase.getInstance().getReference("entradas")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaEntrada(estado: String, callback: (List<Entrada>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Entrada>()
                for (nodo in snapshot.children) {
                    val entrada = nodo.getValue(Entrada::class.java)
                    if (entrada != null && entrada.estado == estado) {
                        lista.add(entrada)
                    }
                }

                lista.sortBy { it.fecha_entrada }
                lista.sortByDescending { it.hora_entrada }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getListaEntradaByProveedor(emailProveedor: String, callback: (List<Entrada>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Entrada>()
                for (nodo in snapshot.children) {
                    val entrada = nodo.getValue(Entrada::class.java)
                    if (entrada != null && entrada.email_proveedor == emailProveedor) {
                        lista.add(entrada)
                    }
                }

                lista.sortBy { it.fecha_entrada }
                lista.sortByDescending { it.hora_entrada }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getListaEntradaBuscador(busqueda: String, estado: String, callback: (List<Entrada>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Entrada>()
                for (nodo in snapshot.children) {
                    val entrada = nodo.getValue(Entrada::class.java)
                    if (entrada != null && entrada.id_producto.contains(busqueda, true) && entrada.estado == estado) {
                        lista.add(entrada)
                    }
                }

                lista.sortBy { it.fecha_entrada }
                lista.sortByDescending { it.hora_entrada }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun deleteEntrada(entrada: Entrada) {
        database.child(entrada.nombre).removeValue()
            .addOnCompleteListener {
                System.out.println("Entrada eliminada")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar la entrada")
            }
    }
}
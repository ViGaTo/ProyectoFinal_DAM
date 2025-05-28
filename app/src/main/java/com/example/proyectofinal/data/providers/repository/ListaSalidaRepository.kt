package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Salida
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaSalidaRepository {
    val database = FirebaseDatabase.getInstance().getReference("salidas")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaSalida(estado: String, callback: (List<Salida>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Salida>()
                for (nodo in snapshot.children) {
                    val salida = nodo.getValue(Salida::class.java)
                    if (salida != null && salida.estado == estado) {
                        lista.add(salida)
                    }
                }

                lista.sortBy { it.fecha_salida }
                lista.sortByDescending { it.hora_salida }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getListaSalidaByCliente(emailCliente: String, callback: (List<Salida>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Salida>()
                for (nodo in snapshot.children) {
                    val salida = nodo.getValue(Salida::class.java)
                    if (salida != null && salida.email_cliente == emailCliente) {
                        lista.add(salida)
                    }
                }

                lista.sortBy { it.fecha_salida }
                lista.sortByDescending { it.hora_salida }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getListaSalidaBuscador(busqueda: String, estado: String, callback: (List<Salida>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Salida>()
                for (nodo in snapshot.children) {
                    val salida = nodo.getValue(Salida::class.java)
                    if (salida != null && salida.id_producto.contains(busqueda, true) && salida.estado == estado) {
                        lista.add(salida)
                    }
                }

                lista.sortBy { it.fecha_salida }
                lista.sortByDescending { it.hora_salida }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun deleteSalida(salida: Salida) {
        database.child(salida.nombre).removeValue()
            .addOnCompleteListener {
                System.out.println("Salida eliminada")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar la salida")
            }
    }
}
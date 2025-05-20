package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaProveedorRepository {
    val database = FirebaseDatabase.getInstance().getReference("proveedores")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaProveedor(callback: (List<Proveedor>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Proveedor>()
                for (nodo in snapshot.children) {
                    val proveedor = nodo.getValue(Proveedor::class.java)
                    if (proveedor != null) {
                        lista.add(proveedor)
                    }
                }

                lista.sortBy { it.estado }
                lista.sortBy { it.nombre }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getProveedorBuscador(busqueda: String, callback: (List<Proveedor>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Proveedor>()
                for (nodo in snapshot.children) {
                    val proveedor = nodo.getValue(Proveedor::class.java)
                    if (proveedor != null && proveedor.nombre.contains(busqueda, ignoreCase = true)) {
                        lista.add(proveedor)
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

    fun deleteProveedor(proveedor: Proveedor) {
        database.child(proveedor.email.encodeEmail()).removeValue()
            .addOnCompleteListener {
                System.out.println("Proveedor eliminado")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar proveedor")
            }
    }
}
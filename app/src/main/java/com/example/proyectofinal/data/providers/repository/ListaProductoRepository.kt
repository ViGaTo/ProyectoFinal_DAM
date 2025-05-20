package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Producto
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaProductoRepository {
    val database = FirebaseDatabase.getInstance().getReference("productos")
    val auth: FirebaseAuth = Firebase.auth

    fun getListaProducto(callback: (List<Producto>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Producto>()
                for (nodo in snapshot.children) {
                    val producto = nodo.getValue(Producto::class.java)
                    if (producto != null) {
                        lista.add(producto)
                    }
                }

                lista.sortBy { it.titulo }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getProductos(datos: (MutableList<Producto>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Producto>()
                for (nodo in snapshot.children) {
                    val producto = nodo.getValue(Producto::class.java)
                    if (producto != null) {
                        lista.add(producto)
                    }
                }

                lista.sortBy { it.titulo }
                datos(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun getProductoBuscador(busqueda: String, callback: (List<Producto>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Producto>()
                for (nodo in snapshot.children) {
                    val producto = nodo.getValue(Producto::class.java)
                    if (producto != null && (producto.titulo.contains(busqueda, ignoreCase = true) || producto.categoria.contains(busqueda, ignoreCase = true))) {
                        lista.add(producto)
                    }
                }

                lista.sortBy { it.titulo }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun addProducto(producto: Producto) {
        database.child(producto.id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    database.child(producto.id.toString()).setValue(producto)
                        .addOnCompleteListener {
                            System.out.println("Producto añadido")
                        }
                        .addOnFailureListener {
                            System.out.println("Error al añadir producto")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun deleteProducto(producto: Producto) {
        database.child(producto.id.toString()).removeValue()
            .addOnCompleteListener {
                System.out.println("Producto eliminado")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar producto")
            }
    }
}
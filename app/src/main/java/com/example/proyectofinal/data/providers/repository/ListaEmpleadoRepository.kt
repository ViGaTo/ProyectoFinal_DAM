package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Empleado
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaEmpleadoRepository {
    val database = FirebaseDatabase.getInstance().getReference("empleados")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaEmpleado(callback: (List<Empleado>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Empleado>()
                for (nodo in snapshot.children) {
                    val empleado = nodo.getValue(Empleado::class.java)
                    if (empleado != null) {
                        lista.add(empleado)
                    }
                }
                // Ordenar la lista por su estado y su nombre
                lista.sortBy { it.nombre }
                lista.sortBy { it.estado }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun deleteEmpleado(empleado: Empleado) {
        database.child(empleado.id).removeValue()
            .addOnCompleteListener {
                System.out.println("Empleado eliminado")
            }
            .addOnFailureListener {
                System.out.println("Error al eliminar empleado")
            }
    }
}
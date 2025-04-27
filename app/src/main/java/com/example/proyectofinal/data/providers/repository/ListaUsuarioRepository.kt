package com.example.proyectofinal.data.providers.repository

import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaUsuarioRepository {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios")
    var auth: FirebaseAuth = Firebase.auth

    fun getListaUsuario(callback: (List<Usuario>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Usuario>()
                for (nodo in snapshot.children) {
                    val usuario = nodo.getValue(Usuario::class.java)
                    if (usuario != null) {
                        lista.add(usuario)
                    }
                }
                lista.sortBy { it.email }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })
    }

    fun addUsuario(usuario: Usuario) {
        database.child(usuario.email.encodeEmail()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    database.child(usuario.email.encodeEmail()).setValue(Usuario(usuario.email, false))
                        .addOnCompleteListener {
                            System.out.println("Usuario añadido")
                        }
                        .addOnFailureListener {
                            System.out.println("Error al añadir usuario")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Error al leer realtime: ${error.message}")
            }
        })

        auth.createUserWithEmailAndPassword(usuario.email, "123456789")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    System.out.println("Login del usuario creado")
                }
            }

            .addOnFailureListener {
                System.out.println("Error al crear login del usuario")
            }
    }

    fun activamientoUsuario(usuario: Usuario){
        if(usuario.activado){
            database.child(usuario.email.encodeEmail()).setValue(Usuario(usuario.email, usuario.admin, false))
                .addOnSuccessListener {
                    System.out.println("Usuario activado")
                }
                .addOnFailureListener {
                    System.out.println("Error al activar usuario")
                }
        }else{
            database.child(usuario.email.encodeEmail()).setValue(Usuario(usuario.email, usuario.admin, true))
                .addOnSuccessListener {
                    System.out.println("Usuario desactivado")
                }
                .addOnFailureListener {
                    System.out.println("Error al desactivar usuario")
                }
        }
    }

    fun editarUsuario(usuario: Usuario) {
        if(usuario.admin){
            database.child(usuario.email.encodeEmail()).setValue(Usuario(usuario.email, false))
                .addOnSuccessListener {
                    System.out.println("Usuario editado")
                }
                .addOnFailureListener {
                    System.out.println("Error al editar usuario")
                }
        }else{
            database.child(usuario.email.encodeEmail()).setValue(Usuario(usuario.email, true))
                .addOnSuccessListener {
                    System.out.println("Usuario editado")
                }
                .addOnFailureListener {
                    System.out.println("Error al editar usuario")
                }
        }
    }
}
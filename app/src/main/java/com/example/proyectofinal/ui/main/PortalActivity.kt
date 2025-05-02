package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityPortalBinding
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PortalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPortalBinding
    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    private var usuario = ""
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPortalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        preferences = Preferences(this)
        obtenerUsuario()
        database = FirebaseDatabase.getInstance().getReference("usuarios")
        setListeners()
    }

    private fun setListeners() {
        binding.menu.setCheckedItem(R.id.item_inicio)

        binding.menu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_inicio -> {
                    startActivity(Intent(this, PortalActivity::class.java))
                    true
                }
                R.id.item_usuarios -> {
                    startActivity(Intent(this, UsuarioActivity::class.java))
                    true
                }
                R.id.item_empleados -> {
                    startActivity(Intent(this, EmpleadoActivity::class.java))
                    true
                }
                R.id.item_salir -> {
                    auth.signOut()
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtenerUsuario() {
        binding.tvBienvenida.text = "Bienvenido " +  auth.currentUser?.email.toString()
        val admin = preferences.isAdmin()
    }
}
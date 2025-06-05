package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityMainBinding
import com.example.proyectofinal.utils.Preferences
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var contrasena = ""

    private lateinit var preferences: Preferences
    private lateinit var database: DatabaseReference
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("usuarios")
        preferences = Preferences(this)
        binding.tvUbicacion.paint.isUnderlineText = true
        setListeners()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvUbicacion.setOnClickListener {
            iniciarActivityMapa()
        }
    }

    private fun iniciarActivityMapa() {
        startActivity(Intent(this, MapaActivity::class.java))
    }

    private fun comprobarCampos(): Boolean {
        binding.tlEmail.isErrorEnabled = false
        binding.tlPassword.isErrorEnabled = false

        email = binding.etEmail.text.toString().trim()
        contrasena = binding.etPassword.text.toString().trim()
        var error = false

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tlEmail.error="ERROR. Debe poner un email válido."
            error = true
        }

        if(contrasena.length < 8){
            binding.tlPassword.error="ERROR. La contraseña debe tener al menos ocho caracteres"
            error = true
        }

        if(error){
            return false
        }else {
            return true
        }
    }

    private fun login() {
        if(!comprobarCampos()) return

        auth.signInWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    database.child(email.encodeEmail()).get().addOnSuccessListener {
                        if (it.exists()) {
                            if(it.child(("activado")).value == false){
                                Toast.makeText(this, "ERROR: Usuario desactivado", Toast.LENGTH_SHORT).show()
                            }else{
                                isAdmin = it.child("admin").value as Boolean
                                preferences.setAdmin(isAdmin)
                                iniciarActivityPortal()
                            }
                        }
                    }
                }
            }

            .addOnFailureListener {
                Toast.makeText(this, "ERROR: El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun iniciarActivityPortal() {
        startActivity(Intent(this, PortalActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if(usuario!=null){
            usuario.email?.let {
                database.child(it.encodeEmail()).get().addOnSuccessListener {
                    if (it.exists()) {
                        if(it.child(("activado")).value == false){
                            Toast.makeText(this, "ERROR: Usuario desactivado", Toast.LENGTH_SHORT).show()
                        }else{
                            isAdmin = it.child("admin").value as Boolean
                            preferences.setAdmin(isAdmin)
                            iniciarActivityPortal()
                        }
                    }
                }
            }
        }
    }
}
package com.example.proyectofinal.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityVideoBinding
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var mediaController: MediaController

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaController = MediaController(this)
        reproducirVideo()

        auth = Firebase.auth
        preferences = Preferences(this)
        obtenerCredenciales()

        setListeners()

        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_video)
        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }
    }

    private fun obtenerCredenciales(){
        val admin = preferences.isAdmin()

        if(!admin){
            binding.menu.menu.removeItem(R.id.item_admin)
        }
    }

    private fun setListeners() {
        binding.btnPortal.setOnClickListener {
            finish()
        }

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
                R.id.item_proveedores -> {
                    startActivity(Intent(this, ProveedorActivity::class.java))
                    true
                }
                R.id.item_clientes -> {
                    startActivity(Intent(this, ClienteActivity::class.java))
                    true
                }
                R.id.item_productos -> {
                    startActivity(Intent(this, ProductoActivity::class.java))
                    true
                }
                R.id.item_inventario_entradas -> {
                    startActivity(Intent(this, EntradaActivity::class.java))
                    true
                }
                R.id.item_inventario_salidas -> {
                    startActivity(Intent(this, SalidaActivity::class.java))
                    true
                }
                R.id.item_documentacion -> {
                    startActivity(Intent(this, PdfActivity::class.java))
                    true
                }
                R.id.item_video -> {
                    startActivity(Intent(this, VideoActivity::class.java))
                    true
                }
                R.id.item_cerrar_sesion -> {
                    auth.signOut()
                    preferences.limpiarPreferencias()
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.item_salir -> {
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun reproducirVideo() {
        val uri = Uri.parse("android.resource://$packageName/${R.raw.video}")
        try{
            binding.videoView.setVideoURI(uri)
            binding.videoView.requestFocus()
            binding.videoView.start()
        }catch (e: Exception){
            System.out.println("Error al reproducir video")
        }
        binding.videoView.setMediaController(mediaController)
        mediaController.setAnchorView(binding.videoView)
    }
}
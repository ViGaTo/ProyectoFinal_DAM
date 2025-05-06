package com.example.proyectofinal.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityInfoProveedorBinding

class InfoProveedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoProveedorBinding

    private var nombre = ""
    private var telefono = ""
    private var direccion = ""
    private var email = ""
    private var ciudad = ""
    private var calificacion = 0F
    private var estado = ""
    private var notas = ""

    private var info = false
    private var proveedor = Proveedor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInfoProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setListeners()
        recogerDatos()
        ponerDatos()
        //setRecycler()
    }

    private fun ponerDatos() {
        if (info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_proveedor_info)
            binding.etNombre.setText(proveedor.nombre)
            binding.etNombre.isEnabled = false
            binding.etTelefono.setText(proveedor.telefono)
            binding.etTelefono.isEnabled = false
            binding.etDireccion.setText(proveedor.direccion)
            binding.etDireccion.isEnabled = false
            binding.etEmail.setText(proveedor.email)
            binding.etEmail.isEnabled = false
            binding.etCiudad.setText(proveedor.ciudad)
            binding.etCiudad.isEnabled = false
            binding.rbCalificacion.rating = proveedor.calificacion
            binding.rbCalificacion.isEnabled = false
            binding.etNotas.setText(proveedor.notas)
            binding.etNotas.isEnabled = false
            binding.cbActivo.isChecked = proveedor.estado == "Activo"
            binding.cbActivo.isEnabled = false
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("INFO")) {
                info = true
                proveedor = bundle.getSerializable("INFO") as Proveedor
            } else {
                info = false
            }
        }
    }

    private fun setListeners() {
        binding.btnVolverFormulario.setOnClickListener {
            finish()
        }
    }
}
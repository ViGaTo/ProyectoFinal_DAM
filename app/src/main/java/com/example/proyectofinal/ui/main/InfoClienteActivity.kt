package com.example.proyectofinal.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityInfoClienteBinding

class InfoClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoClienteBinding

    private var nombre = ""
    private var email = ""
    private var telefono = ""
    private var direccion = ""
    private var ciudad = ""
    private var frecuencia = ""
    private var notas = ""

    private var info = false
    private var cliente = Cliente()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInfoClienteBinding.inflate(layoutInflater)
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
        if(info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_cliente_info)
            binding.etNombre.setText(cliente.nombre)
            binding.etNombre.isEnabled = false
            binding.etTelefono.setText(cliente.telefono)
            binding.etTelefono.isEnabled = false
            binding.etDireccion.setText(cliente.direccion)
            binding.etDireccion.isEnabled = false
            binding.etEmail.setText(cliente.email)
            binding.etEmail.isEnabled = false
            binding.etCiudad.setText(cliente.ciudad)
            binding.etCiudad.isEnabled = false
            binding.etFrecuencia.setText(cliente.frecuencia)
            binding.etFrecuencia.isEnabled = false
            binding.etNotas.setText(cliente.notas)
            binding.etNotas.isEnabled = false
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("INFO")) {
                info = true
                cliente = bundle.getSerializable("INFO") as Cliente
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
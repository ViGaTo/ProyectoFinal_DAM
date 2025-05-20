package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityInfoProveedorBinding
import com.example.proyectofinal.ui.adapters.EntradaAdapter
import com.example.proyectofinal.ui.adapters.EntradaProveedorAdapter
import com.example.proyectofinal.ui.viewmodels.ListaEntradaViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InfoProveedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoProveedorBinding
    private lateinit var adapter: EntradaProveedorAdapter
    private lateinit var database: DatabaseReference

    private var lista = mutableListOf<Entrada>()
    private val viewModel: ListaEntradaViewModel by viewModels()

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
        database = FirebaseDatabase.getInstance().getReference("entradas")
        viewModel.entradasProveedor.observe(this, { entradas ->
            if (entradas.isNullOrEmpty()) {
                binding.tvLista.visibility = View.VISIBLE
            } else {
                binding.tvLista.visibility = View.GONE
            }
            adapter.actualizarLista(entradas)
        })

        setListeners()
        recogerDatos()
        ponerDatos()
        setRecycler()
        viewModel.getListaEntradaByProveedor(proveedor.email)
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = EntradaProveedorAdapter(lista, {item -> infoEntrada(item)})
        binding.recycler.adapter = adapter
    }

    private fun infoEntrada(item: Entrada) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioInfoEntradaActivity(bundle)
    }

    private fun irFormularioInfoEntradaActivity(bundle: Bundle) {
        val intent = Intent(this, FormularioEntradaActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
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
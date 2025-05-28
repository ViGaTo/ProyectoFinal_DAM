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
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.databinding.ActivityInfoClienteBinding
import com.example.proyectofinal.ui.adapters.SalidaClienteAdapter
import com.example.proyectofinal.ui.viewmodels.ListaEntradaViewModel
import com.example.proyectofinal.ui.viewmodels.ListaSalidaViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InfoClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoClienteBinding
    private lateinit var adapter: SalidaClienteAdapter
    private lateinit var database: DatabaseReference

    private var lista = mutableListOf<Salida>()
    private val viewModel: ListaSalidaViewModel by viewModels()

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
        database = FirebaseDatabase.getInstance().getReference("salidas")
        viewModel.salidasCliente.observe(this, { salidas ->
            if (salidas.isNullOrEmpty()) {
                binding.tvLista.visibility = View.VISIBLE
            } else {
                binding.tvLista.visibility = View.GONE
            }
            adapter.actualizarLista(salidas)
        })

        setListeners()
        recogerDatos()
        ponerDatos()
        setRecycler()
        viewModel.getListaSalidaByCliente(cliente.email)
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = SalidaClienteAdapter(lista, {item -> infoSalida(item)})
        binding.recycler.adapter = adapter
    }

    private fun infoSalida(item: Salida) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioInfoSalidaActivity(bundle)
    }

    private fun irFormularioInfoSalidaActivity(bundle: Bundle?=null) {
        val intent = Intent(this, FormularioSalidaActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun ponerDatos() {
        if(info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_cliente_info)
            binding.etNombre.setText(cliente.nombre)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etTelefono.setText(cliente.telefono)
            binding.etTelefono.isEnabled = false
            binding.etTelefono.setTextColor(resources.getColor(R.color.black))
            binding.etDireccion.setText(cliente.direccion)
            binding.etDireccion.isEnabled = false
            binding.etDireccion.setTextColor(resources.getColor(R.color.black))
            binding.etEmail.setText(cliente.email)
            binding.etEmail.isEnabled = false
            binding.etEmail.setTextColor(resources.getColor(R.color.black))
            binding.etCiudad.setText(cliente.ciudad)
            binding.etCiudad.isEnabled = false
            binding.etCiudad.setTextColor(resources.getColor(R.color.black))
            binding.etFrecuencia.setText(cliente.frecuencia)
            binding.etFrecuencia.isEnabled = false
            binding.etFrecuencia.setTextColor(resources.getColor(R.color.black))
            binding.etNotas.setText(cliente.notas)
            binding.etNotas.isEnabled = false
            binding.etNotas.setTextColor(resources.getColor(R.color.black))
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
package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.databinding.ActivitySalidaBinding
import com.example.proyectofinal.ui.adapters.SalidaAdapter
import com.example.proyectofinal.ui.viewmodels.ListaSalidaViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SalidaActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySalidaBinding
    private lateinit var adapter: SalidaAdapter
    private lateinit var database: DatabaseReference
    private var lista = mutableListOf<Salida>()
    private val viewModel: ListaSalidaViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalidaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().getReference("salidas")
        viewModel.salidas.observe(this, { salidas ->
            if (salidas.isNullOrEmpty()) {
                binding.tvLista.visibility = View.VISIBLE
            } else {
                binding.tvLista.visibility = View.GONE
            }
            adapter.actualizarLista(salidas)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_inventario_salidas)

        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }

        setListeners()
        setRecycler()
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = SalidaAdapter(lista, {item -> deleteSalida(item) }, { item -> updateSalida(item) }, { item -> infoSalida(item) })
        binding.recycler.adapter = adapter
    }

    private fun infoSalida(item: Salida) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioActivity(bundle)
    }

    private fun updateSalida(item: Salida) {
        val bundle = Bundle().apply {
            putSerializable("EDITAR", item)
        }
        irFormularioActivity(bundle)
    }

    private fun deleteSalida(item: Salida) {
        viewModel.deleteSalida(item)
    }

    private fun setListeners() {
        binding.btnNuevo.setOnClickListener {
            irFormularioActivity()
        }

        binding.btnPortal.setOnClickListener {
            finish()
        }

        binding.btnActivo.setOnClickListener {
            binding.searchView.setQuery("", false)
            if(binding.btnActivo.text == "Completadas"){
                binding.btnActivo.text = "Pendientes"
                binding.btnActivo.setBackgroundColor(resources.getColor(R.color.color_pendiente))
                viewModel.getListaSalida("Completada")
            } else {
                binding.btnActivo.text = "Completadas"
                binding.btnActivo.setBackgroundColor(resources.getColor(R.color.color_terminado))
                viewModel.getListaSalida("Pendiente")
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if(binding.btnActivo.text == "Completadas"){
                        viewModel.getListaSalidaBuscador(query, "Pendiente")
                    } else {
                        viewModel.getListaSalidaBuscador(query, "Completada")
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if(binding.btnActivo.text == "Completadas"){
                        viewModel.getListaSalidaBuscador(newText, "Pendiente")
                    } else {
                        viewModel.getListaSalidaBuscador(newText, "Completada")
                    }
                }
                return false
            }
        })

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
                R.id.item_clientes -> {
                    startActivity(Intent(this, ClienteActivity::class.java))
                    true
                }
                R.id.item_proveedores -> {
                    startActivity(Intent(this, ProveedorActivity::class.java))
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

    private fun irFormularioActivity(bundle: Bundle?=null) {
        val intent = Intent(this, FormularioSalidaActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
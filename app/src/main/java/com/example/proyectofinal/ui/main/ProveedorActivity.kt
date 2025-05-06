package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityProveedorBinding
import com.example.proyectofinal.ui.adapters.ProveedorAdapter
import com.example.proyectofinal.ui.viewmodels.ListaProveedorViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProveedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProveedorBinding
    private lateinit var adapter: ProveedorAdapter
    private lateinit var database: DatabaseReference
    private var lista = mutableListOf<Proveedor>()
    private val viewModel: ListaProveedorViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProveedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("proveedores")
        viewModel.proveedores.observe(this, { proveedores ->
            if (proveedores.isNullOrEmpty()) {
                binding.tvLista.visibility = android.view.View.VISIBLE
            } else {
                binding.tvLista.visibility = android.view.View.GONE
            }
            adapter.actualizarLista(proveedores)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_proveedores)

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

        adapter = ProveedorAdapter(lista, {item -> deleteProveedor(item)}, {item -> updateProveedor(item)}, {item -> infoProveedor(item)})
        binding.recycler.adapter = adapter
    }

    private fun setListeners() {
        binding.btnNuevo.setOnClickListener {
            irFormularioActivity()
        }

        binding.btnPortal.setOnClickListener {
            finish()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.getListaProveedorBuscador(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.getListaProveedorBuscador(newText)
                }
                return false
            }
        })

        binding.menu.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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
                R.id.item_salir -> {
                    auth.signOut()
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun infoProveedor(item: Proveedor) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoActivity(bundle)
    }

    private fun irInfoActivity(bundle: Bundle?=null) {
        val intent = Intent(this, InfoProveedorActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun updateProveedor(item: Proveedor) {
        val bundle = Bundle().apply {
            putSerializable("EDITAR", item)
        }
        irFormularioActivity(bundle)
    }

    private fun irFormularioActivity(bundle: Bundle?=null) {
        val intent = Intent(this, FormularioProveedorActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun deleteProveedor(item: Proveedor) {
        viewModel.deleteProveedor(item)
    }
}
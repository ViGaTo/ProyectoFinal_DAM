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
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.databinding.ActivityClienteBinding
import com.example.proyectofinal.ui.adapters.ClienteAdapter
import com.example.proyectofinal.ui.adapters.ProveedorAdapter
import com.example.proyectofinal.ui.viewmodels.ListaClienteViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClienteBinding
    private lateinit var adapter: ClienteAdapter
    private lateinit var database: DatabaseReference
    private var lista = mutableListOf<Cliente>()
    private val viewModel: ListaClienteViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("clientes")
        viewModel.clientes.observe(this, { clientes ->
            if (clientes.isNullOrEmpty()) {
                binding.tvLista.visibility = android.view.View.VISIBLE
            } else {
                binding.tvLista.visibility = android.view.View.GONE
            }
            adapter.actualizarLista(clientes)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_clientes)
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

        adapter = ClienteAdapter(lista, {item -> deleteCliente(item) }, {item -> updateCliente(item) }, {item -> infoCliente(item) })
        binding.recycler.adapter = adapter
    }

    private fun infoCliente(item: Cliente) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoActivity(bundle)
    }

    private fun irInfoActivity(bundle: Bundle?=null) {
        val intent = Intent(this, InfoClienteActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun updateCliente(item: Cliente) {
        val bundle = Bundle().apply {
            putSerializable("EDITAR", item)
        }
        irFormularioActivity(bundle)
    }

    private fun deleteCliente(item: Cliente) {
        viewModel.deleteCliente(item)
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
                    viewModel.getListaClienteBuscador(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.getListaClienteBuscador(newText)
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
        val intent = Intent(this, FormularioClienteActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
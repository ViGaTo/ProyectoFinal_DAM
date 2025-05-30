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
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.databinding.ActivityEmpleadoBinding
import com.example.proyectofinal.ui.adapters.EmpleadoAdapter
import com.example.proyectofinal.ui.viewmodels.ListaEmpleadoViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EmpleadoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpleadoBinding
    private lateinit var adapter: EmpleadoAdapter
    private lateinit var database: DatabaseReference
    private var lista = mutableListOf<Empleado>()
    private val viewModel: ListaEmpleadoViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpleadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("empleados")
        viewModel.empleados.observe(this, { empleados ->
            if (empleados.isNullOrEmpty()) {
                binding.tvLista.visibility = View.VISIBLE
            } else {
                binding.tvLista.visibility = View.GONE
            }
            adapter.actualizarLista(empleados)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        obtenerCredenciales()

        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_usuarios)
        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }

        setListeners()
        setRecycler()
    }

    private fun obtenerCredenciales(){
        val admin = preferences.isAdmin()

        if(!admin){
            binding.menu.menu.removeItem(R.id.item_admin)
        }
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = EmpleadoAdapter(lista, {item -> deleteEmpleado(item)}, {item -> updateEmpleado(item)}, {item -> infoEmpleado(item)})
        binding.recycler.adapter = adapter
    }

    private fun infoEmpleado(item: Empleado) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioActivity(bundle)
    }

    private fun updateEmpleado(item: Empleado) {
        val bundle = Bundle().apply {
            putSerializable("EDITAR", item)
        }
        irFormularioActivity(bundle)
    }

    private fun deleteEmpleado(item: Empleado) {
        viewModel.deleteEmpleado(item)
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
            if(binding.btnActivo.text == "Inactivo"){
                binding.btnActivo.text = "Activo"
                binding.btnActivo.setBackgroundColor(resources.getColor(R.color.activo))
                viewModel.getListaEmpleado("Inactivo")
            } else {
                binding.btnActivo.text = "Inactivo"
                binding.btnActivo.setBackgroundColor(resources.getColor(R.color.inactivo))
                viewModel.getListaEmpleado("Activo")
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if(binding.btnActivo.text == "Inactivo"){
                        viewModel.getListaEmpleadoBuscador(query, "Activo")
                    } else {
                        viewModel.getListaEmpleadoBuscador(query, "Inactivo")
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if(binding.btnActivo.text == "Inactivo"){
                        viewModel.getListaEmpleadoBuscador(newText, "Activo")
                    } else {
                        viewModel.getListaEmpleadoBuscador(newText, "Inactivo")
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

    private fun irFormularioActivity(bundle: Bundle?=null) {
        val intent = Intent(this, FormularioEmpleadoActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.databinding.ActivityEntradaBinding
import com.example.proyectofinal.ui.adapters.EntradaAdapter
import com.example.proyectofinal.ui.viewmodels.ListaEntradaViewModel
import com.example.proyectofinal.ui.viewmodels.ListaProductoViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EntradaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntradaBinding
    private lateinit var adapter: EntradaAdapter
    private lateinit var database: DatabaseReference
    private lateinit var databaseProducto: DatabaseReference

    private var lista = mutableListOf<Entrada>()
    private val viewModel: ListaEntradaViewModel by viewModels()
    private val viewModelProducto: ListaProductoViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().getReference("entradas")
        databaseProducto = FirebaseDatabase.getInstance().getReference("productos")
        viewModel.entradas.observe(this, { entradas ->
            if (entradas.isNullOrEmpty()) {
                binding.tvLista.visibility = View.VISIBLE
            } else {
                binding.tvLista.visibility = View.GONE
            }
            adapter.actualizarLista(entradas)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_inventario_entradas)

        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }

        setListeners()
        setRecycler()
    }

    private fun obtenerProducto(idProducto: String): Producto? {
        var producto: Producto? = null
        viewModelProducto.productos.observe(this) { productos ->
            for (p in productos) {
                if (p.id == idProducto.toInt()) {
                    producto = p
                    break
                }
            }
        }
        return producto
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = EntradaAdapter(lista, {item -> deleteEntrada(item) }, {item -> updateEntrada(item)}, {item -> infoEntrada(item)})
        binding.recycler.adapter = adapter
    }

    private fun infoEntrada(item: Entrada) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioActivity(bundle)
    }

    private fun updateEntrada(item: Entrada) {
        val bundle = Bundle().apply {
            putSerializable("EDITAR", item)
        }
        irFormularioActivity(bundle)
    }

    private fun deleteEntrada(item: Entrada) {
        val producto = obtenerProducto(item.id_producto)
        if(producto != null) {
            val productoEditado = Producto(producto.id, producto.titulo, producto.descripcion, producto.precio, producto.imagen, producto.categoria, producto.cantidad - item.cantidad_producto)
            databaseProducto.child(productoEditado.id.toString()).setValue(productoEditado).addOnSuccessListener {
                viewModel.deleteEntrada(item)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al eliminar la entrada", Toast.LENGTH_SHORT).show()
            }
        }
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
                viewModel.getListaEntrada("Completada")
            } else {
                binding.btnActivo.text = "Completadas"
                binding.btnActivo.setBackgroundColor(resources.getColor(R.color.color_terminado))
                viewModel.getListaEntrada("Pendiente")
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if(binding.btnActivo.text == "Completadas"){
                        viewModel.getListaEntradaBuscador(query, "Pendiente")
                    } else {
                        viewModel.getListaEntradaBuscador(query, "Completada")
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if(binding.btnActivo.text == "Completadas"){
                        viewModel.getListaEntradaBuscador(newText, "Pendiente")
                    } else {
                        viewModel.getListaEntradaBuscador(newText, "Completada")
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
        val intent = Intent(this, FormularioEntradaActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
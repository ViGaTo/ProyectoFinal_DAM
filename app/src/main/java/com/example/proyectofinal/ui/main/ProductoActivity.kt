package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.data.providers.api.ObjectApiClient.apiClient
import com.example.proyectofinal.databinding.ActivityProductoBinding
import com.example.proyectofinal.ui.adapters.ProductoAdapter
import com.example.proyectofinal.ui.viewmodels.ListaProductoViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductoBinding
    private lateinit var adapter: ProductoAdapter
    private lateinit var database: DatabaseReference
    val lista = mutableListOf<Producto>()
    private val viewModel: ListaProductoViewModel by viewModels()

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("productos")
        viewModel.productos.observe(this, { productos ->
            if (productos.isNullOrEmpty()) {
                binding.tvLista.visibility = android.view.View.VISIBLE
            } else {
                binding.tvLista.visibility = android.view.View.GONE
            }
            adapter.actualizarLista(productos)
        })
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)
        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_productos)
        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }
        obtenerProductosAPI()
        setRecycler()
        setListeners()
    }

    private fun setListeners() {
        binding.btnPortal.setOnClickListener {
            finish()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.getListaProductoBuscador(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.getListaProductoBuscador(newText)
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

    private fun setRecycler() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.recycler.layoutManager = layoutManager

        adapter = ProductoAdapter(lista, {item -> updateProducto(item)})
        binding.recycler.adapter = adapter
    }

    private fun updateProducto(item: Producto) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoActivity(bundle)
    }

    private fun irInfoActivity(bundle: Bundle?= null) {
        val intent = Intent(this, InfoProductoActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun obtenerProductosAPI() {
        lifecycleScope.launch {
            val datos = apiClient.getProductos()
            val listaAPI = datos.body()?: emptyList()
            withContext(Dispatchers.Main) {
                if (lista.isEmpty()) {
                    if (listaAPI != null) {
                        for (producto in listaAPI) {
                            val nuevoProducto = Producto(
                                id = producto.id,
                                titulo = producto.titulo,
                                descripcion = producto.descripcion,
                                precio = producto.precio,
                                imagen = producto.imagen,
                                categoria = producto.categoria,
                                cantidad = 0
                            )
                            viewModel.addProducto(nuevoProducto)
                        }
                    }
                } else {
                    if (listaAPI != null) {
                        for (producto in listaAPI) {
                            val nuevoProducto = Producto(
                                id = producto.id,
                                titulo = producto.titulo,
                                descripcion = producto.descripcion,
                                precio = producto.precio,
                                imagen = producto.imagen,
                                categoria = producto.categoria,
                                cantidad = 0
                            )
                            val productoLista = lista.find { it.id == nuevoProducto.id }
                            if (productoLista == null) {
                                viewModel.addProducto(nuevoProducto)
                            }
                        }

                        for (producto in lista) {
                            val productoListaAPI = listaAPI.find { it.id == producto.id }
                            if (productoListaAPI == null) {
                                viewModel.deleteProducto(producto)
                            }
                        }
                    }
                }
            }
        }
    }
}
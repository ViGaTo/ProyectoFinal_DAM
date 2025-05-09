package com.example.proyectofinal.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.databinding.ActivityInfoProductoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class InfoProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoProductoBinding

    private var nombre = ""
    private var precio = 0.0
    private var descripcion = ""
    private var categoria = ""
    private var cantidad = 0

    private var info = false
    private var producto = Producto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInfoProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setListeners()
        recogerDatos()
        ponerDatos()
    }

    private fun ponerDatos() {
        binding.tvTitulo.text = getString(R.string.tv_titulo_producto_info)
        binding.etNombre.setText(producto.titulo)
        binding.etNombre.isEnabled = false
        binding.etPrecio.setText(producto.precio.toString())
        binding.etPrecio.isEnabled = false
        binding.etDescripcion.setText(producto.descripcion)
        binding.etDescripcion.isEnabled = false
        binding.etCategoria.setText(producto.categoria)
        binding.etCategoria.isEnabled = false
        binding.etCantidad.setText(producto.cantidad.toString())
        binding.etCantidad.isEnabled = false
        Picasso.get().load(producto.imagen).into(binding.ivProducto)
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("INFO")) {
                info = true
                producto = bundle.getSerializable("INFO") as Producto
            } else {
                info = false
            }
        }
    }

    private fun setListeners() {
        binding.btnCancelar.setOnClickListener {
            finish()
        }
        binding.btnEditar.setOnClickListener {
            binding.etCantidad.isEnabled = true
            binding.btnEditar.isEnabled = false
            binding.btnEditar.visibility = View.GONE
            binding.btnEditar.isClickable = false
            binding.btnAceptar.visibility = View.VISIBLE
            binding.btnAceptar.isEnabled = true
            binding.btnAceptar.isClickable = true
        }

        binding.btnAceptar.setOnClickListener {
            binding.tvTitulo.visibility = View.GONE
            binding.tvTituloEditar.visibility = View.VISIBLE
            editarProducto()
        }
    }

    private fun editarProducto() {
        if (!datosCorrectos()) return

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("productos")
        database.child(producto.id.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val producto = Producto(producto.id, producto.titulo, producto.descripcion, producto.precio, producto.imagen, producto.categoria, cantidad)
                    database.child(producto.id.toString()).setValue(producto)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tlCantidad.error = "ERROR. No se ha podido editar el producto"
            }
        })
    }

    private fun datosCorrectos(): Boolean {
        cantidad = binding.etCantidad.text.toString().toIntOrNull() ?: 0

        if (cantidad < 0) {
            binding.etCantidad.error = "ERROR. La cantidad no puede ser negativa"
            return false
        }

        return true
    }
}
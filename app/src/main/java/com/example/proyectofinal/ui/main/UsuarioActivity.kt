package com.example.proyectofinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.databinding.ActivityUsuarioBinding
import com.example.proyectofinal.ui.adapters.UsuarioAdapter
import com.example.proyectofinal.ui.viewmodels.ListaUsuarioViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class UsuarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsuarioBinding
    private val viewModel: ListaUsuarioViewModel by viewModels()
    private val adapter = UsuarioAdapter(listOf<Usuario>(), { item -> activamientoUsuario(item)}, { item -> editarUsuario(item)})
    private lateinit var database: DatabaseReference
    private var usuario = ""

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("usuarios")
        viewModel.usuarios.observe(this, { lista ->
            adapter.actualizarLista(lista)
        })
        auth = Firebase.auth
        preferences = Preferences(this)
        obtenerCredenciales()

        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_usuarios)
        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }

        setRecycler()
        setListeners()
    }

    private fun obtenerCredenciales(){
        val admin = preferences.isAdmin()

        if(!admin){
            binding.menu.menu.removeItem(R.id.item_admin)
        }
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            addUsuario()
        }

        binding.btnVolverViewmodel.setOnClickListener {
            finish()
        }

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
                R.id.item_proveedores -> {
                    startActivity(Intent(this, ProveedorActivity::class.java))
                    true
                }
                R.id.item_clientes -> {
                    startActivity(Intent(this, ClienteActivity::class.java))
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

        binding.menu.setCheckedItem(R.id.item_usuarios)
    }

    private fun addUsuario() {
        usuario = binding.etUsuario.text.toString().trim()
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(usuario).matches()){
            binding.etUsuario.error = "ERROR. El email no es válido."
            return
        }
        viewModel.addUsuario(usuario)
        binding.etUsuario.setText("")
        Toast.makeText(this, "Usuario $usuario añadido", Toast.LENGTH_SHORT).show()
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvLista.layoutManager = layoutManager
        binding.rvLista.adapter = adapter
    }

    private fun activamientoUsuario(item: Usuario) {
        viewModel.activamientoUsuario(item)
        Toast.makeText(this, "El usuario ${item.email} ha cambiado su activación", Toast.LENGTH_SHORT).show()
    }

    private fun editarUsuario(item: Usuario) {
        viewModel.editarUsuario(item)
        Toast.makeText(this, "Usuario ${item.email} editado", Toast.LENGTH_SHORT).show()
    }
}
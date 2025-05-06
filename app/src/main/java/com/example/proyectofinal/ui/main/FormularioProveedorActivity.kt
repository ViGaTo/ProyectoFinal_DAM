package com.example.proyectofinal.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityFormularioProveedorBinding
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FormularioProveedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioProveedorBinding

    private var nombre = ""
    private var telefono = ""
    private var direccion = ""
    private var email = ""
    private var ciudad = ""
    private var calificacion = 0F
    private var estado = ""
    private var notas = ""

    private var editando = false
    private var proveedor = Proveedor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioProveedorBinding.inflate(layoutInflater)
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
        if(editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_proveedor_editar)
            binding.etNombre.setText(proveedor.nombre)
            binding.etTelefono.setText(proveedor.telefono)
            binding.etDireccion.setText(proveedor.direccion)
            binding.etEmail.setText(proveedor.email)
            binding.etEmail.isEnabled = false
            binding.etCiudad.setText(proveedor.ciudad)
            binding.rbCalificacion.rating = proveedor.calificacion
            binding.cbActivo.isChecked = proveedor.estado == "Activo"
            binding.etNotas.setText(proveedor.notas)
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("EDITAR")) {
                editando = true
                proveedor = bundle.getSerializable("EDITAR") as Proveedor
            } else {
                editando = false
            }
        }
    }

    private fun setListeners() {
        binding.btnAceptar.setOnClickListener {
            addItem()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun addItem() {
        if(!datosCorrectos()) return

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("proveedores")
        database.child(email.encodeEmail()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && !editando) {
                    binding.tlEmail.error = "ERROR. El email ya está registrado."
                } else {
                    val item = Proveedor(nombre, email, telefono, direccion, ciudad, estado, calificacion, notas)
                    database.child(email.encodeEmail()).setValue(item).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        binding.tlEmail.error = "ERROR. No se ha podido guardar el proveedor."
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString().trim()
        telefono = binding.etTelefono.text.toString().trim()
        direccion = binding.etDireccion.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        ciudad = binding.etCiudad.text.toString().trim()
        calificacion = binding.rbCalificacion.rating
        estado = if(binding.cbActivo.isChecked) "Activo" else "Inactivo"
        notas = binding.etNotas.text.toString().trim()

        if(nombre.length < 3) {
            binding.tlNombre.error = "ERROR. El nombre debe tener al menos tres caracteres"
            return false
        }

        if(telefono.length < 9) {
            binding.tlTelefono.error = "ERROR. El teléfono debe tener al menos nueve dígitos"
            return false
        }

        if(direccion.length < 5) {
            binding.tlDireccion.error = "ERROR. La dirección debe tener al menos cinco caracteres"
            return false
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tlEmail.error = "ERROR. Debe poner un email válido."
            return false
        }

        if(ciudad.length < 3) {
            binding.tlCiudad.error = "ERROR. La ciudad debe tener al menos tres caracteres"
            return false
        }

        return true
    }
}
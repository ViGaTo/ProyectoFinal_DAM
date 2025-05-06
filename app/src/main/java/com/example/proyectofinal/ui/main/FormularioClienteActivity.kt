package com.example.proyectofinal.ui.main

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityFormularioClienteBinding
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FormularioClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioClienteBinding

    private var nombre = ""
    private var email = ""
    private var telefono = ""
    private var direccion = ""
    private var ciudad = ""
    private var frecuencia = ""
    private var notas = ""

    private var editando = false
    private var cliente = Cliente()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setListeners()
        recogerDatos()
        ponerDatos()
        habilitarDropdown()
    }

    private fun habilitarDropdown() {
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.et_frecuencia)
        val opciones = resources.getStringArray(R.array.frecuencia)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun ponerDatos() {
        if (editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_cliente_editar)
            binding.etNombre.setText(cliente.nombre)
            binding.etEmail.setText(cliente.email)
            binding.etEmail.isEnabled = false
            binding.etTelefono.setText(cliente.telefono)
            binding.etDireccion.setText(cliente.direccion)
            binding.etCiudad.setText(cliente.ciudad)
            binding.etFrecuencia.setText(cliente.frecuencia)
            binding.etNotas.setText(cliente.notas)
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("EDITAR")) {
                editando = true
                cliente = bundle.getSerializable("EDITAR") as Cliente
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

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("clientes")
        database.child(email.encodeEmail()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && !editando) {
                    binding.tlEmail.error = "ERROR. El email ya está registrado."
                } else {
                    val item = Cliente(nombre, email, telefono, direccion, ciudad, frecuencia, notas)
                    database.child(email.encodeEmail()).setValue(item).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        binding.tlEmail.error = "ERROR. No se ha podido guardar el cliente."
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString()
        email = binding.etEmail.text.toString()
        telefono = binding.etTelefono.text.toString()
        direccion = binding.etDireccion.text.toString()
        ciudad = binding.etCiudad.text.toString()
        frecuencia = binding.etFrecuencia.text.toString()
        notas = binding.etNotas.text.toString()

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

        if(frecuencia != "Semanal" && frecuencia != "Mensual" && frecuencia != "Anual") {
            binding.tlFrecuencia.error = "ERROR. La frecuencia debe ser Semanal, Mensual o Anual"
            return false
        }

        return true
    }
}
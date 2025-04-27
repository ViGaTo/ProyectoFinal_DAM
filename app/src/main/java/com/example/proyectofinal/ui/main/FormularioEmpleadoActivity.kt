package com.example.proyectofinal.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.databinding.ActivityFormularioEmpleadoBinding
import com.example.proyectofinal.utils.encodeEmail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FormularioEmpleadoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioEmpleadoBinding

    private var nombre = ""
    private var apellidos = ""
    private var email = ""
    private var telefono = ""
    private var ciudad = ""
    private var fecha_nacimiento = ""
    private var estado = ""
    private var puesto = ""
    private var salario = 0.0
    private var fechaContratacion = ""
    private var fechaFinContrato = ""
    private var notas = ""

    private var editando = false
    private var info = false
    private var empleado = Empleado()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioEmpleadoBinding.inflate(layoutInflater)
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
        if (editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_empleado_editar)
            binding.etNombre.setText(empleado.nombre)
            binding.etApellido.setText(empleado.apellidos)
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(empleado.email)
            binding.etTelefono.setText(empleado.telefono)
            binding.etCiudad.setText(empleado.ciudad)
            binding.etFechaNacimiento.setText(empleado.fecha_nacimiento)
            binding.cbActivo.isChecked = if(empleado.estado == "Activo") true else false
            binding.etPuesto.setText(empleado.puesto)
            binding.etSalario.setText(empleado.salario.toString())
            binding.etFechaContratacion.setText(empleado.fechaContratacion)
            binding.etFechaFin.setText(empleado.fechaFinContrato)
            binding.etNotas.setText(empleado.notas)
        } else if (info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_empleado_info)
            binding.etNombre.setText(empleado.nombre)
            binding.etNombre.isEnabled = false
            binding.etApellido.setText(empleado.apellidos)
            binding.etApellido.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(empleado.email)
            binding.etTelefono.setText(empleado.telefono)
            binding.etTelefono.isEnabled = false
            binding.etCiudad.setText(empleado.ciudad)
            binding.etCiudad.isEnabled = false
            binding.etFechaNacimiento.setText(empleado.fecha_nacimiento)
            binding.etFechaNacimiento.isEnabled = false
            binding.cbActivo.isChecked = if(empleado.estado == "Activo") true else false
            binding.etPuesto.setText(empleado.puesto)
            binding.etPuesto.isEnabled = false
            binding.etSalario.setText(empleado.salario.toString())
            binding.etSalario.isEnabled = false
            binding.etFechaContratacion.setText(empleado.fechaContratacion)
            binding.etFechaContratacion.isEnabled = false
            binding.etFechaFin.setText(empleado.fechaFinContrato)
            binding.etFechaFin.isEnabled = false
            binding.etNotas.setText(empleado.notas)
            binding.etNotas.isEnabled = false
            binding.btnAceptar.isEnabled = false
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("EDITAR")) {
                editando = true
                empleado = bundle.getSerializable("EDITAR") as Empleado
            } else if (bundle.containsKey("INFO")) {
                info = true
                empleado = bundle.getSerializable("INFO") as Empleado
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

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString().trim()
        apellidos = binding.etApellido.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        telefono = binding.etTelefono.text.toString().trim()
        ciudad = binding.etCiudad.text.toString().trim()
        fecha_nacimiento = binding.etFechaNacimiento.text.toString().trim()
        estado = if(binding.cbActivo.isChecked) "Activo" else "Inactivo"
        puesto = binding.etPuesto.text.toString().trim()
        salario = binding.etSalario.text.toString().toDoubleOrNull() ?: 0.0
        fechaContratacion = binding.etFechaContratacion.text.toString().trim()
        fechaFinContrato = binding.etFechaFin.text.toString().trim()
        notas = binding.etNotas.text.toString().trim()

        if(nombre.length < 3) {
           binding.etNombre.error = "ERROR. El nombre debe tener al menos 3 caracteres."
            return false
        }

        if(apellidos.length < 5) {
            binding.etApellido.error = "ERROR. Los apellidos deben tener al menos 5 caracteres."
            return false
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "ERROR. El email no es válido."
            return false
        }

        if(telefono.length < 9) {
            binding.etTelefono.error = "ERROR. El teléfono debe tener al menos 9 caracteres."
            return false
        }

        if(ciudad.length < 3) {
            binding.etCiudad.error = "ERROR. La ciudad debe tener al menos 3 caracteres."
            return false
        }

        if(fecha_nacimiento.isEmpty()) {
            binding.etFechaNacimiento.error = "ERROR. La fecha de nacimiento no puede estar vacía."
            return false
        }

        if(puesto != "Informático" && puesto != "Gestión" && puesto != "Administrativo" && puesto != "Comercial" && puesto != "Logística") {
            binding.etPuesto.error = "ERROR. El puesto no es válido."
            return false
        }

        if(salario < 1000 || salario > 10000) {
            binding.etSalario.error = "ERROR. El salario debe estar entre 1000 y 10000."
            return false
        }

        if(fechaContratacion.isEmpty()) {
            binding.etFechaContratacion.error = "ERROR. La fecha de contratación no puede estar vacía."
            return false
        }

        if(fechaFinContrato.isEmpty()) {
            binding.etFechaFin.error = "ERROR. La fecha de fin de contrato no puede estar vacía."
            return false
        }

        return true
    }

    private fun addItem() {
        if(!datosCorrectos()) return

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("empleados")
        //Comprueba
    }
}
package com.example.proyectofinal.ui.main

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.databinding.ActivityFormularioEmpleadoBinding
import com.example.proyectofinal.ui.viewmodels.ListaUsuarioViewModel
import com.example.proyectofinal.utils.encodeEmail
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormularioEmpleadoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioEmpleadoBinding

    private val viewModel: ListaUsuarioViewModel by viewModels()

    private var nombre = ""
    private var email = ""
    private var telefono = ""
    private var ciudad = ""
    private var fecha_nacimiento = ""
    private var estado = ""
    private var puesto = ""
    private var salario = 0F
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
        habilitarDropdown()
        habilitarCalendario()
    }

    private fun habilitarCalendario() {
        val etFechaNacimiento = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento)
        val etFechaContratacion = findViewById<TextInputEditText>(R.id.et_fecha_contratacion)
        val etFechaFin = findViewById<TextInputEditText>(R.id.et_fecha_fin)
        etFechaNacimiento.setOnClickListener{
            val calendar = Calendar.getInstance()
            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)
            val dia = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFechaNacimiento.setText(selectedDate)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }

        etFechaContratacion.setOnClickListener{
            val calendar = Calendar.getInstance()
            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)
            val dia = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFechaContratacion.setText(selectedDate)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }

        etFechaFin.setOnClickListener{
            val calendar = Calendar.getInstance()
            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)
            val dia = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFechaFin.setText(selectedDate)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }
    }

    private fun habilitarDropdown() {
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.et_puesto)
        val opciones = resources.getStringArray(R.array.tipos)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)

        if(info){
            autoCompleteTextView.setText(empleado.puesto, false)
        } else {
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun ponerDatos() {
        if (editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_empleado_editar)
            binding.etNombre.setText(empleado.nombreApellidos)
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(empleado.email)
            binding.etEmail.setTextColor(resources.getColor(R.color.black))
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
            binding.etNombre.setText(empleado.nombreApellidos)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(empleado.email)
            binding.etEmail.setTextColor(resources.getColor(R.color.black))
            binding.etTelefono.setText(empleado.telefono)
            binding.etTelefono.isEnabled = false
            binding.etTelefono.setTextColor(resources.getColor(R.color.black))
            binding.etCiudad.setText(empleado.ciudad)
            binding.etCiudad.isEnabled = false
            binding.etCiudad.setTextColor(resources.getColor(R.color.black))
            binding.etFechaNacimiento.setText(empleado.fecha_nacimiento)
            binding.etFechaNacimiento.isEnabled = false
            binding.etFechaNacimiento.setTextColor(resources.getColor(R.color.black))
            binding.cbActivo.isChecked = if(empleado.estado == "Activo") true else false
            binding.cbActivo.isEnabled = false
            binding.etPuesto.setText(empleado.puesto)
            binding.etPuesto.isEnabled = false
            binding.etPuesto.setTextColor(resources.getColor(R.color.black))
            binding.etSalario.setText(empleado.salario.toString())
            binding.etSalario.isEnabled = false
            binding.etSalario.setTextColor(resources.getColor(R.color.black))
            binding.etFechaContratacion.setText(empleado.fechaContratacion)
            binding.etFechaContratacion.isEnabled = false
            binding.etFechaContratacion.setTextColor(resources.getColor(R.color.black))
            binding.etFechaFin.setText(empleado.fechaFinContrato)
            binding.etFechaFin.isEnabled = false
            binding.etFechaFin.setTextColor(resources.getColor(R.color.black))
            binding.etNotas.setText(empleado.notas)
            binding.etNotas.isEnabled = false
            binding.etNotas.setTextColor(resources.getColor(R.color.black))
            binding.btnAceptar.isEnabled = false
            binding.btnVolverFormulario.isEnabled = true
            binding.btnVolverFormulario.isClickable = true
            binding.btnVolverFormulario.visibility = View.VISIBLE
            binding.btnCancelar.isEnabled = false
            binding.btnCancelar.isClickable = false
            binding.btnCancelar.visibility = View.INVISIBLE
            binding.btnAceptar.isEnabled = false
            binding.btnAceptar.isClickable = false
            binding.btnAceptar.visibility = View.INVISIBLE
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

        binding.btnVolverFormulario.setOnClickListener {
            finish()
        }
    }

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        telefono = binding.etTelefono.text.toString().trim()
        ciudad = binding.etCiudad.text.toString().trim()
        fecha_nacimiento = binding.etFechaNacimiento.text.toString().trim()
        estado = if(binding.cbActivo.isChecked) "Activo" else "Inactivo"
        puesto = binding.etPuesto.text.toString().trim()
        salario = binding.etSalario.text.toString().toFloat()
        fechaContratacion = binding.etFechaContratacion.text.toString().trim()
        fechaFinContrato = binding.etFechaFin.text.toString().trim()
        notas = binding.etNotas.text.toString().trim()

        if(nombre.length < 9) {
           binding.tlNombre.error = "ERROR. El nombre completo debe tener al menos nueve caracteres."
            return false
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tlEmail.error = "ERROR. Debe poner un email válido."
            return false
        }

        if(telefono.length < 9) {
            binding.tlTelefono.error = "ERROR. El teléfono debe tener al menos nueve digitos."
            return false
        }

        if(ciudad.length < 3) {
            binding.tlCiudad.error = "ERROR. La ciudad debe tener al menos 3 caracteres."
            return false
        }

        if(fecha_nacimiento.isEmpty()) {
            binding.tlFechaNacimiento.error = "ERROR. La fecha de nacimiento no puede estar vacía."
            return false
        }

        if(puesto != "Informático" && puesto != "Gestión" && puesto != "Administrativo" && puesto != "Comercial" && puesto != "Logística") {
            binding.tlPuesto.error = "ERROR. El puesto no es válido."
            return false
        }

        if(salario < 1000 || salario > 10000) {
            binding.tlSalario.error = "ERROR. El salario debe estar entre 1000 y 10000."
            return false
        }

        if(fechaContratacion.isEmpty()) {
            binding.tlFechaContratacion.error = "ERROR. La fecha de contratación no puede estar vacía."
            return false
        }

        if(fechaFinContrato.isEmpty()) {
            binding.tlFechaFin.error = "ERROR. La fecha de fin de contrato no puede estar vacía."
            return false
        }

        val fechaNacimiento = fecha_nacimiento.split("/").map { it.toInt() }
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val fechaActualSplit = fechaActual.split("/").map { it.toInt() }
        val edad = fechaActualSplit[2] - fechaNacimiento[2]
        if (edad < 16 || (edad == 16 && (fechaActualSplit[1] < fechaNacimiento[1] || (fechaActualSplit[1] == fechaNacimiento[1] && fechaActualSplit[0] < fechaNacimiento[0])))) {
            binding.tlFechaNacimiento.error = "ERROR. Debe tener al menos 16 años."
            return false
        }

        val fechaInicio = fechaContratacion.split("/").map { it.toInt() }
        val fechaFinal = fechaFinContrato.split("/").map { it.toInt() }
        if (fechaFinal[2] < fechaInicio[2] || (fechaFinal[2] == fechaInicio[2] && fechaFinal[1] < fechaInicio[1]) || (fechaFinal[2] == fechaInicio[2] && fechaFinal[1] == fechaInicio[1] && fechaFinal[0] < fechaInicio[0])) {
            binding.tlFechaContratacion.error = "ERROR. La fecha de fin de contrato no puede ser anterior a la fecha de contratación."
            return false
        }

        return true
    }

    private fun addItem() {
        if (!datosCorrectos()) return

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("empleados")
        database.child(email.encodeEmail()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && !editando) {
                    binding.tlEmail.error = "ERROR. El email ya está registrado."
                } else {
                    val item = Empleado(nombre, email, telefono, ciudad, fecha_nacimiento, estado, puesto, salario, fechaContratacion, fechaFinContrato, notas)
                    database.child(email.encodeEmail()).setValue(item).addOnSuccessListener {
                        comprobarYCrearUsuario()
                        finish()
                    }.addOnFailureListener {
                        binding.tlEmail.error = "ERROR. No se ha podido guardar el empleado."
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun comprobarYCrearUsuario() {
        if(!obtenerUsuario(email)) {
            viewModel.addUsuario(email)
        }
    }

    private fun obtenerUsuario(email: String): Boolean{
        var existe = false
        viewModel.getUsuarioByEmail(email) { usuario ->
            if (usuario != null) {
                existe = true
            }
        }
        return existe
    }
}
package com.example.proyectofinal.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Cliente
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.databinding.ActivityFormularioSalidaBinding
import com.example.proyectofinal.ui.viewmodels.ListaClienteViewModel
import com.example.proyectofinal.ui.viewmodels.ListaEmpleadoViewModel
import com.example.proyectofinal.ui.viewmodels.ListaProductoViewModel
import com.example.proyectofinal.utils.Preferences
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class FormularioSalidaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioSalidaBinding
    private lateinit var databaseProducto: DatabaseReference
    private lateinit var databaseCliente: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: Preferences

    private val viewModelProducto: ListaProductoViewModel by viewModels()
    private val viewModelCliente: ListaClienteViewModel by viewModels()
    private val viewModelEmpleado: ListaEmpleadoViewModel by viewModels()

    private var nombre = ""
    private var producto = ""
    private var cliente = ""
    private var cantidad_producto = 0
    private var precio = 0.0
    private var fecha_salida = ""
    private var hora_salida = ""
    private var estado = ""
    private var notas = ""

    private var editando = false
    private var info = false
    private var salida = Salida()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioSalidaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseProducto = FirebaseDatabase.getInstance().getReference("productos")
        databaseCliente = FirebaseDatabase.getInstance().getReference("clientes")

        auth = Firebase.auth
        preferences = Preferences(this)

        recogerDatos()
        ponerDatos()
        actualizarPrecio()
        habilitarDropdown()
        habilitarHora()
        habilitarCalendario()
        habilitarEnlaces()
        setListeners()
    }

    private fun habilitarEnlaces() {
        binding.tvAutor.setOnClickListener {
            viewModelEmpleado.empleadosCompleta.observe(this) { empleados ->
                val autor = empleados.find { it.email == salida.email_autor }
                if (autor != null) {
                    infoEmpleado(autor)
                } else {
                    Toast.makeText(this, "No se encontró el autor", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvUltimoAutor.setOnClickListener {
            viewModelEmpleado.empleadosCompleta.observe(this) { empleados ->
                val ultimoAutor = empleados.find { it.email == salida.email_ultimoAutor }
                if (ultimoAutor != null) {
                    infoEmpleado(ultimoAutor)
                } else {
                    Toast.makeText(this, "No se encontró el último autor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarPrecio() {
        var cantidad = binding.etCantidad.text.toString().toIntOrNull() ?: 0

        if(binding.etProductoSalida.text.isNotEmpty()) {
            viewModelProducto.productos.observe(this) { productos ->
                for (producto in productos) {
                    if (producto.titulo == binding.etProductoSalida.text.toString()) {
                        binding.etPrecio.setText((producto.precio * cantidad.toString().toDouble()).toString())
                        break
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.etCantidad.setOnFocusChangeListener { _, _ ->
            actualizarPrecio()
        }

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

    private fun updateProducto(item: Producto) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoProductoActivity(bundle)
    }

    private fun infoCliente(item: Cliente) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoActivity(bundle)
    }

    private fun irInfoProductoActivity(bundle: Bundle?= null) {
        val intent = Intent(this, InfoProductoActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun irInfoActivity(bundle: Bundle?=null) {
        val intent = Intent(this, InfoClienteActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun infoEmpleado(item: Empleado) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irFormularioInfoEmpleadoActivity(bundle)
    }

    private fun irFormularioInfoEmpleadoActivity(bundle: Bundle?=null) {
        val intent = Intent(this, FormularioEmpleadoActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
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

    private fun obtenerCliente(emailCliente: String): Cliente? {
        var cliente: Cliente? = null
        viewModelCliente.clientes.observe(this) { clientes ->
            for (c in clientes) {
                if (c.email == emailCliente) {
                    cliente = c
                    break
                }
            }
        }
        return cliente
    }

    private fun addItem() {
        if (!datosCorrectos()) return

        producto = obtenerProductoId(binding.etProductoSalida.text.toString())
        cliente = obtenerClienteEmail(binding.etCliente.text.toString())

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("salidas")
        database.child(nombre).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && !editando) {
                    binding.tlNombre.error = "ERROR. El nombre de la salida ya está registrado."
                } else if (editando) {
                    val item = Salida(nombre, producto, cliente, cantidad_producto, precio, fecha_salida, hora_salida, estado, notas, salida.email_autor, auth.currentUser?.email.toString())
                    database.child(nombre).setValue(item).addOnSuccessListener {
                        var productoEditar = obtenerProducto(producto)
                        if(productoEditar != null) {
                            var cantidadEditada = productoEditar.cantidad + salida.cantidad_producto

                            val itemProducto = Producto(productoEditar.id, productoEditar.titulo, productoEditar.descripcion, productoEditar.precio, productoEditar.imagen, productoEditar.categoria, cantidadEditada - cantidad_producto)
                            databaseProducto.child(producto).setValue(itemProducto).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                binding.tlNombre.error = "ERROR. No se ha podido guardar la salida."
                            }
                        }
                    }.addOnFailureListener {
                        binding.tlNombre.error = "ERROR. No se ha podido guardar la salida."
                    }
                } else {
                    val item = Salida(nombre, producto, cliente, cantidad_producto, precio, fecha_salida, hora_salida, estado, notas, auth.currentUser?.email.toString(), auth.currentUser?.email.toString())
                    database.child(nombre).setValue(item).addOnSuccessListener {
                        var productoEditar = obtenerProducto(producto)
                        if(productoEditar != null) {
                            val itemProducto = Producto(productoEditar.id, productoEditar.titulo, productoEditar.descripcion, productoEditar.precio, productoEditar.imagen, productoEditar.categoria, productoEditar.cantidad - cantidad_producto)
                            databaseProducto.child(producto).setValue(itemProducto).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                binding.tlNombre.error = "ERROR. No se ha podido guardar la salida."
                            }
                        }
                    }.addOnFailureListener {
                        binding.tlNombre.error = "ERROR. No se ha podido guardar la salida."
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString()
        producto = binding.etProductoSalida.text.toString()
        cliente = binding.etCliente.text.toString()
        cantidad_producto = binding.etCantidad.text.toString().toIntOrNull() ?: 0
        precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
        fecha_salida = binding.etFechaSalida.text.toString()
        hora_salida = binding.etHoraSalida.text.toString()
        estado = if (binding.cbActivo.isChecked) "Completada" else "Pendiente"
        notas = binding.etNotas.text.toString()

        var productoObtenido = obtenerProductoId(binding.etProductoSalida.text.toString())
        var item = obtenerProducto(productoObtenido)
        if(item != null) {
            if(editando) {
                var cantidadEditada = item.cantidad + salida.cantidad_producto

                if (cantidadEditada - cantidad_producto < 0) {
                    binding.tlCantidad.error = "ERROR. El producto no cuenta con suficiente stock."
                    return false
                }
            }else{
                if (item.cantidad - cantidad_producto < 0) {
                    binding.tlCantidad.error = "ERROR. El producto no cuenta con suficiente stock."
                    return false
                }
            }
        }

        if(nombre.length < 3){
            binding.tlNombre.error = "ERROR. El nombre de la salida debe tener al menos 3 caracteres."
            return false
        }

        if(producto.isEmpty()){
            binding.tlProductoSalida.error = "ERROR. El producto no puede estar vacío."
            return false
        }

        if(cliente.isEmpty()){
            binding.tlCliente.error = "ERROR. El proveedor no puede estar vacío."
            return false
        }

        if(cantidad_producto <= 0){
            binding.tlCantidad.error = "ERROR. La cantidad de producto debe ser superior a cero."
            return false
        }

        if(precio <= 0.0){
            binding.tlPrecio.error = "ERROR. El precio del producto debe ser superior a cero."
            return false
        }

        if(fecha_salida.isEmpty()){
            binding.tlFechaSalida.error = "ERROR. La fecha de salida no puede estar vacía."
            return false
        }

        if(hora_salida.isEmpty()){
            binding.tlHoraSalida.error = "ERROR. La hora de salida no puede estar vacía."
            return false
        }



        return true
    }

    private fun obtenerProductoId(titulo: String): String {
        var idProducto = ""
        viewModelProducto.productos.observe(this) { productos ->
            for (producto in productos) {
                if (producto.titulo == titulo) {
                    idProducto = producto.id.toString()
                    break
                }
            }
        }

        return idProducto
    }

    private fun obtenerClienteEmail(nombre: String): String {
        var emailCliente = ""
        viewModelCliente.clientes.observe(this) { clientes ->
            for (cliente in clientes) {
                if (cliente.nombre == nombre) {
                    emailCliente = cliente.email
                    break
                }
            }
        }

        return emailCliente
    }

    private fun habilitarCalendario() {
        val etFechaSalida = findViewById<TextInputEditText>(R.id.et_fecha_salida)
        etFechaSalida.setOnClickListener{
            val calendar = Calendar.getInstance()
            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)
            val dia = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFechaSalida.setText(selectedDate)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }
    }

    private fun habilitarHora() {
        val etHoraSalida = findViewById<TextInputEditText>(R.id.et_hora_salida)
        etHoraSalida.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minutos = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etHoraSalida.setText(selectedTime)
                },
                hora, minutos, true
            )
            timePickerDialog.show()
        }
    }

    private fun habilitarDropdown() {
        val autoCompleteProducto = findViewById<AutoCompleteTextView>(R.id.et_producto_salida)
        val autoCompleteCliente = findViewById<AutoCompleteTextView>(R.id.et_cliente)

        viewModelProducto.productos.observe(this) { productos ->
            val listaNombresProducto = productos.map { it.titulo  }
            if(! info) {
                val adapterProducto = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    listaNombresProducto
                )
                autoCompleteProducto.setAdapter(adapterProducto)
            }

            if (editando || info) {
                autoCompleteProducto.setText(
                    productos.firstOrNull { it.id.toString() == salida.id_producto }?.titulo ?: "",
                    false
                )
            }
        }

        viewModelCliente.clientes.observe(this) { clientes ->
            val listaNombresCliente = clientes.map { it.nombre }
            if(! info) {
                val adapterCliente = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    listaNombresCliente
                )
                autoCompleteCliente.setAdapter(adapterCliente)
            }

            if (editando || info) {
                autoCompleteCliente.setText(
                    clientes.firstOrNull { it.email == salida.email_cliente }?.nombre ?: "",
                    false
                )
            }
        }
    }

    private fun ponerDatos() {
        if(editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_salida_editar)
            binding.etNombre.setText(salida.nombre)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etCantidad.setText(salida.cantidad_producto.toString())
            binding.etPrecio.setText(salida.precio.toString())
            binding.etFechaSalida.setText(salida.fecha_salida)
            binding.etHoraSalida.setText(salida.hora_salida)
            binding.cbActivo.isChecked = if(salida.estado == "Completada") true else false
            binding.etNotas.setText(salida.notas)
        } else if (info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_salida_info)
            binding.etNombre.setText(salida.nombre)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etCantidad.setText(salida.cantidad_producto.toString())
            binding.etCantidad.isEnabled = false
            binding.etCantidad.setTextColor(resources.getColor(R.color.black))
            binding.etPrecio.setText(salida.precio.toString())
            binding.etPrecio.isEnabled = false
            binding.etPrecio.setTextColor(resources.getColor(R.color.black))
            binding.etFechaSalida.setText(salida.fecha_salida)
            binding.etFechaSalida.isEnabled = false
            binding.etFechaSalida.setTextColor(resources.getColor(R.color.black))
            binding.etHoraSalida.setText(salida.hora_salida)
            binding.etHoraSalida.isEnabled = false
            binding.etHoraSalida.setTextColor(resources.getColor(R.color.black))
            binding.cbActivo.isChecked = if(salida.estado == "Completada") true else false
            binding.cbActivo.isEnabled = false
            binding.cbActivo.setTextColor(resources.getColor(R.color.black))
            binding.etNotas.setText(salida.notas)
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
            if(preferences.isAdmin()) {
                binding.tvCreado.visibility = View.VISIBLE
                binding.tvAutor.text = salida.email_autor
                binding.tvAutor.visibility = View.VISIBLE

                binding.tvUltimaModificacion.visibility = View.VISIBLE
                binding.tvUltimoAutor.text = salida.email_ultimoAutor
                binding.tvUltimoAutor.visibility = View.VISIBLE

                binding.tvAutor.isClickable = true
                binding.tvAutor.isEnabled = true
                binding.tvUltimoAutor.isClickable = true
                binding.tvUltimoAutor.isEnabled = true
                binding.tvAutor.setTextColor(resources.getColor(R.color.colorPrimary))
                binding.tvAutor.paint.isUnderlineText = true
                binding.tvUltimoAutor.setTextColor(resources.getColor(R.color.colorPrimary))
                binding.tvUltimoAutor.paint.isUnderlineText = true
            }

            binding.etProductoSalida.setOnClickListener{
                updateProducto(obtenerProducto(salida.id_producto)!!)
            }

            binding.etCliente.setOnClickListener{
                infoCliente(obtenerCliente(salida.email_cliente)!!)
            }
        }
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("EDITAR")) {
                editando = true
                salida = bundle.getSerializable("EDITAR") as Salida
            } else if (bundle.containsKey("INFO")) {
                info = true
                salida = bundle.getSerializable("INFO") as Salida
            }
        }
    }
}
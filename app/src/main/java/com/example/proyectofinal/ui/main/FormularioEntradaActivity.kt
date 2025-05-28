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
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ActivityFormularioEntradaBinding
import com.example.proyectofinal.ui.viewmodels.ListaEmpleadoViewModel
import com.example.proyectofinal.ui.viewmodels.ListaProductoViewModel
import com.example.proyectofinal.ui.viewmodels.ListaProveedorViewModel
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

class FormularioEntradaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioEntradaBinding
    private lateinit var databaseProducto: DatabaseReference
    private lateinit var databaseProveedor: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: Preferences

    private val viewModelProducto: ListaProductoViewModel by viewModels()
    private val viewModelProveedor: ListaProveedorViewModel by viewModels()
    private val viewModelEmpleado: ListaEmpleadoViewModel by viewModels()

    private var nombre = ""
    private var producto = ""
    private var proveedor = ""
    private var cantidad_producto = 0
    private var precio = 0.0
    private var fecha_entrada = ""
    private var hora_entrada = ""
    private var estado = ""
    private var notas = ""

    private var editando = false
    private var info = false
    private var entrada = Entrada()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseProducto = FirebaseDatabase.getInstance().getReference("productos")
        databaseProveedor = FirebaseDatabase.getInstance().getReference("proveedores")

        auth = Firebase.auth
        preferences = Preferences(this)

        recogerDatos()
        ponerDatos()
        habilitarDropdown()
        habilitarHora()
        habilitarCalendario()
        habilitarEnlaces()
        setListeners()
    }

    private fun habilitarEnlaces() {
        binding.tvAutor.setOnClickListener {
            viewModelEmpleado.empleadosCompleta.observe(this) { empleados ->
                val autor = empleados.find { it.email == entrada.email_autor }
                if (autor != null) {
                    infoEmpleado(autor)
                } else {
                    Toast.makeText(this, "No se encontró el autor", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvUltimoAutor.setOnClickListener {
            viewModelEmpleado.empleadosCompleta.observe(this) { empleados ->
                val ultimoAutor = empleados.find { it.email == entrada.email_ultimoAutor }
                if (ultimoAutor != null) {
                    infoEmpleado(ultimoAutor)
                } else {
                    Toast.makeText(this, "No se encontró el último autor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun habilitarCalendario() {
        val etFechaEntrada = findViewById<TextInputEditText>(R.id.et_fecha_entrada)
        etFechaEntrada.setOnClickListener{
            val calendar = Calendar.getInstance()
            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH)
            val dia = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFechaEntrada.setText(selectedDate)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }
    }

    private fun habilitarHora() {
        val etHoraEntrada = findViewById<TextInputEditText>(R.id.et_hora_entrada)
        etHoraEntrada.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minutos = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etHoraEntrada.setText(selectedTime)
                },
                hora, minutos, true
            )
            timePickerDialog.show()
        }
    }

    private fun habilitarDropdown() {
        val autoCompleteProducto = findViewById<AutoCompleteTextView>(R.id.et_producto_entrada)
        val autoCompleteProveedor = findViewById<AutoCompleteTextView>(R.id.et_proveedor)

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
                    productos.firstOrNull { it.id.toString() == entrada.id_producto }?.titulo ?: "",
                    false
                )
            }
        }

        viewModelProveedor.proveedores.observe(this) { proveedores ->
            val listaEmailsProveedor = proveedores.map { it.nombre }
            if(! info) {
                val adapterProveedor = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    listaEmailsProveedor
                )
                autoCompleteProveedor.setAdapter(adapterProveedor)
            }

            if (editando || info) {
                autoCompleteProveedor.setText(
                    proveedores.firstOrNull { it.email == entrada.email_proveedor }?.nombre ?: "",
                    false
                )
            }
        }
    }

    private fun ponerDatos() {
        if(editando) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_entrada_editar)
            binding.etNombre.setText(entrada.nombre)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etCantidad.setText(entrada.cantidad_producto.toString())
            binding.etPrecio.setText(entrada.precio.toString())
            binding.etFechaEntrada.setText(entrada.fecha_entrada)
            binding.etHoraEntrada.setText(entrada.hora_entrada)
            binding.cbActivo.isChecked = if(entrada.estado == "Completada") true else false
            binding.etNotas.setText(entrada.notas)
        } else if (info) {
            binding.tvTitulo.text = getString(R.string.tv_titulo_entrada_info)
            binding.etNombre.setText(entrada.nombre)
            binding.etNombre.isEnabled = false
            binding.etNombre.setTextColor(resources.getColor(R.color.black))
            binding.etCantidad.setText(entrada.cantidad_producto.toString())
            binding.etCantidad.isEnabled = false
            binding.etCantidad.setTextColor(resources.getColor(R.color.black))
            binding.etPrecio.setText(entrada.precio.toString())
            binding.etPrecio.isEnabled = false
            binding.etPrecio.setTextColor(resources.getColor(R.color.black))
            binding.etFechaEntrada.setText(entrada.fecha_entrada)
            binding.etFechaEntrada.isEnabled = false
            binding.etFechaEntrada.setTextColor(resources.getColor(R.color.black))
            binding.etHoraEntrada.setText(entrada.hora_entrada)
            binding.etHoraEntrada.isEnabled = false
            binding.etHoraEntrada.setTextColor(resources.getColor(R.color.black))
            binding.cbActivo.isChecked = if(entrada.estado == "Completada") true else false
            binding.cbActivo.isEnabled = false
            binding.etNotas.setText(entrada.notas)
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
                binding.tvAutor.text = entrada.email_autor
                binding.tvAutor.visibility = View.VISIBLE

                binding.tvUltimaModificacion.visibility = View.VISIBLE
                binding.tvUltimoAutor.text = entrada.email_ultimoAutor
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

            binding.etProductoEntrada.setOnClickListener{
                updateProducto(obtenerProducto(entrada.id_producto)!!)
            }

            binding.etProveedor.setOnClickListener{
                infoProveedor(obtenerProveedor(entrada.email_proveedor)!!)
            }
        }
    }

    private fun infoProveedor(item: Proveedor) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoProveedorActivity(bundle)
    }

    private fun irInfoProveedorActivity(bundle: Bundle?=null) {
        val intent = Intent(this, InfoProveedorActivity::class.java)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun updateProducto(item: Producto) {
        val bundle = Bundle().apply {
            putSerializable("INFO", item)
        }
        irInfoProductoActivity(bundle)
    }

    private fun irInfoProductoActivity(bundle: Bundle?= null) {
        val intent = Intent(this, InfoProductoActivity::class.java)
        if (bundle != null) {
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

    private fun obtenerProveedor(emailProveedor: String): Proveedor? {
        var proveedor: Proveedor? = null
        viewModelProveedor.proveedores.observe(this) { proveedores ->
            for (p in proveedores) {
                if (p.email == emailProveedor) {
                    proveedor = p
                    break
                }
            }
        }
        return proveedor
    }

    private fun recogerDatos() {
        val bundle = intent.extras
        if (bundle != null) {
            if(bundle.containsKey("EDITAR")) {
                editando = true
                entrada = bundle.getSerializable("EDITAR") as Entrada
            } else if (bundle.containsKey("INFO")) {
                info = true
                entrada = bundle.getSerializable("INFO") as Entrada
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

    private fun addItem() {
        if (!datosCorrectos()) return

        producto = obtenerProductoId(binding.etProductoEntrada.text.toString())
        proveedor = obtenerProveedorEmail(binding.etProveedor.text.toString())

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("entradas")
        database.child(nombre).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && !editando) {
                    binding.tlNombre.error = "ERROR. El nombre de la entrada ya está registrado."
                } else if (editando) {
                    val item = Entrada(nombre, producto, proveedor, cantidad_producto, precio, fecha_entrada, hora_entrada, estado, notas, entrada.email_autor, auth.currentUser?.email.toString())
                    database.child(nombre).setValue(item).addOnSuccessListener {
                        var productoEditar = obtenerProducto(producto)
                        if(productoEditar != null) {
                            var cantidadEditada = productoEditar.cantidad - entrada.cantidad_producto

                            if(cantidadEditada < 0) {
                                cantidadEditada = 0
                            }

                            val itemProducto = Producto(productoEditar.id, productoEditar.titulo, productoEditar.descripcion, productoEditar.precio, productoEditar.imagen, productoEditar.categoria, cantidadEditada + cantidad_producto)
                            databaseProducto.child(producto).setValue(itemProducto).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                binding.tlNombre.error = "ERROR. No se ha podido guardar la entrada."
                            }
                        }
                    }.addOnFailureListener {
                        binding.tlNombre.error = "ERROR. No se ha podido guardar la entrada."
                    }
                } else {
                    val item = Entrada(nombre, producto, proveedor, cantidad_producto, precio, fecha_entrada, hora_entrada, estado, notas, auth.currentUser?.email.toString(), auth.currentUser?.email.toString())
                    database.child(nombre).setValue(item).addOnSuccessListener {
                        var productoEditar = obtenerProducto(producto)
                        if(productoEditar != null) {
                            val itemProducto = Producto(productoEditar.id, productoEditar.titulo, productoEditar.descripcion, productoEditar.precio, productoEditar.imagen, productoEditar.categoria, productoEditar.cantidad + cantidad_producto)
                            databaseProducto.child(producto).setValue(itemProducto).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                binding.tlNombre.error = "ERROR. No se ha podido guardar la entrada."
                            }
                        }
                    }.addOnFailureListener {
                        binding.tlNombre.error = "ERROR. No se ha podido guardar la entrada."
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun obtenerProveedorEmail(nombre: String): String {
        var emailProveedor = ""
        viewModelProveedor.proveedores.observe(this) { proveedores ->
            for (proveedor in proveedores) {
                if (proveedor.nombre == nombre) {
                    emailProveedor = proveedor.email
                    break
                }
            }
        }

        return emailProveedor
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

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString()
        producto = binding.etProductoEntrada.text.toString()
        proveedor = binding.etProveedor.text.toString()
        cantidad_producto = binding.etCantidad.text.toString().toIntOrNull() ?: 0
        precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
        fecha_entrada = binding.etFechaEntrada.text.toString()
        hora_entrada = binding.etHoraEntrada.text.toString()
        estado = if (binding.cbActivo.isChecked) "Completada" else "Pendiente"
        notas = binding.etNotas.text.toString()

        if(nombre.length < 3){
            binding.tlNombre.error = "ERROR. El nombre de la entrada debe tener al menos 3 caracteres."
            return false
        }

        if(producto.isEmpty()){
            binding.tlProductoEntrada.error = "ERROR. El producto no puede estar vacío."
            return false
        }

        if(proveedor.isEmpty()){
            binding.tlProveedor.error = "ERROR. El proveedor no puede estar vacío."
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

        if(fecha_entrada.isEmpty()){
            binding.tlFechaEntrada.error = "ERROR. La fecha de entrada no puede estar vacía."
            return false
        }

        if(hora_entrada.isEmpty()){
            binding.tlHoraEntrada.error = "ERROR. La hora de entrada no puede estar vacía."
            return false
        }

        return true
    }
}
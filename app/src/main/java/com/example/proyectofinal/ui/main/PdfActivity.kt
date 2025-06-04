package com.example.proyectofinal.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityPdfBinding
import com.example.proyectofinal.databinding.ActivitySalidaBinding
import com.example.proyectofinal.utils.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.io.InputStream

class PdfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfBinding

    private lateinit var imagenPdf: ImageView
    private lateinit var etNumeroPagina: EditText
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button

    private var documento: PDDocument? = null
    private var renderer: PDFRenderer? = null
    private var paginaActual = 0
    private var paginaMaxima = 0
    private var actualizandoTexto = false

    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        PDFBoxResourceLoader.init(applicationContext)

        auth = Firebase.auth
        preferences = Preferences(this)

        iniciarElementos()
        setListeners()
        obtenerCredenciales()

        val menu = binding.menu.menu
        val item = menu.findItem(R.id.item_documentacion)
        item?.let {
            it.isChecked = true
            binding.menu.setCheckedItem(it.itemId)
        }
    }

    private fun obtenerCredenciales(){
        val admin = preferences.isAdmin()

        if(!admin){
            binding.menu.menu.removeItem(R.id.item_admin)
            cargarPDF(R.raw.guia_usuario)
        }else{
            cargarPDF(R.raw.guia_admin)
        }
    }

    private fun iniciarElementos() {
        imagenPdf = binding.pdfImageView
        btnAnterior = binding.btnAnterior
        btnSiguiente = binding.btnSiguiente
        etNumeroPagina = binding.etNumero
    }

    private fun cargarPDF(pdf: Int) {
        val inputStream: InputStream = resources.openRawResource(pdf)
        documento = PDDocument.load(inputStream)
        renderer = documento?.let { PDFRenderer(it) }
        paginaActual = 0
        paginaMaxima = documento?.numberOfPages ?: 0
        mostrarPagina()
    }

    private fun actualizarBotones() {
        documento?.let {
            binding.btnAnterior.isEnabled = paginaActual > 0
            binding.btnSiguiente.isEnabled = paginaActual < it.numberOfPages - 1
        }
    }

    private fun mostrarPagina() {
        renderer?.let { pdfRenderer ->
            val bitmap: Bitmap = pdfRenderer.renderImage(paginaActual, 1f)
            imagenPdf.setImageBitmap(bitmap)
            actualizarBotones()

            actualizandoTexto = true
            etNumeroPagina.setText((paginaActual).toString())
            etNumeroPagina.setSelection(etNumeroPagina.text.length)
            actualizandoTexto = false
        }
    }

    private fun setListeners() {
        binding.btnSiguiente.setOnClickListener {
            siguientePagina()
        }

        binding.btnAnterior.setOnClickListener {
            paginaAnterior()
        }

        etNumeroPagina.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!actualizandoTexto) {
                    val num = s.toString().toIntOrNull()
                    irAPagina(num)
                }
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
    }

    private fun irAPagina(indice: Int?) {
        val total = documento?.numberOfPages ?: 0
        if (indice == null || indice < 0 || indice >= total) {
            Toast.makeText(this, "Número de página inválido", Toast.LENGTH_SHORT).show()
        } else {
            paginaActual = indice
            mostrarPagina()
        }
    }

    private fun paginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--
            mostrarPagina()
        }
    }

    private fun siguientePagina() {
        documento?.let {
            if (paginaActual < it.numberOfPages - 1) {
                paginaActual++
                mostrarPagina()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeDocument()
    }

    private fun closeDocument() {
        paginaActual = 0
        documento?.close()
        documento = null
        renderer = null
    }
}

package com.example.proyectofinal.ui.main

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
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

        iniciarElementos()
        setListeners()
        cargarPDF(R.raw.guia_usuario)
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

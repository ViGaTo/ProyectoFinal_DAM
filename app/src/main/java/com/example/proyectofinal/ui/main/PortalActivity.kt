package com.example.proyectofinal.ui.main

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityPortalBinding
import com.example.proyectofinal.utils.Preferences
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PortalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPortalBinding
    private lateinit var preferences: Preferences
    private lateinit var auth: FirebaseAuth

    private lateinit var databaseProducto: DatabaseReference
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private var usuario = ""
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPortalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseProducto = FirebaseDatabase.getInstance().getReference("productos")
        auth = Firebase.auth
        preferences = Preferences(this)
        obtenerCredenciales()

        pieChart = binding.pieChart
        barChart = binding.barChart

        obtenerUsuario()
        database = FirebaseDatabase.getInstance().getReference("usuarios")
        cargarMasStock()
        setListeners()
    }

    private fun cargarMenosStock() {
        pieChart.visibility = View.GONE
        barChart.visibility = View.VISIBLE

        databaseProducto.orderByChild("cantidad")
            .limitToFirst(5)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("Graficas", "snapshot size = ${snapshot.childrenCount}")

                val entradas = mutableListOf<BarEntry>()
                val etiquetas  = mutableListOf<String>()
                var i = 0f

                snapshot.children.forEach { doc ->
                    val titulo = doc.child("titulo").getValue(String::class.java) ?: "?"
                    val stock  = doc.child("cantidad").getValue(Long::class.java)?.toFloat() ?: 0f
                    entradas.add(BarEntry(i, stock))
                    etiquetas.add(titulo)
                    i += 1f
                }

                mostrarBarChart(entradas, etiquetas, "Cinco productos con menos stock")
            }
            .addOnFailureListener { e ->
                Log.e("Graficas", "Error leyendo Firebase", e)
            }
    }

    private fun cargarMasStock(){
        pieChart.visibility = View.GONE
        barChart.visibility = View.VISIBLE

        databaseProducto.orderByChild("cantidad")
            .limitToLast(5)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("Graficas", "snapshot size = ${snapshot.childrenCount}")

                val entradas = mutableListOf<BarEntry>()
                val etiquetas  = mutableListOf<String>()
                var i = 0f

                snapshot.children.forEach { doc ->
                    val titulo = doc.child("titulo").getValue(String::class.java) ?: "?"
                    val stock  = doc.child("cantidad").getValue(Long::class.java)?.toFloat() ?: 0f
                    entradas.add(BarEntry(i, stock))
                    etiquetas.add(titulo)
                    i += 1f
                }

                mostrarBarChart(entradas, etiquetas, "Cinco productos con más stock")
            }
            .addOnFailureListener { e ->
                Log.e("Graficas", "Error leyendo Firebase", e)
            }
    }

    private fun cargarPorCategoria() {
        barChart.visibility = View.GONE
        pieChart.visibility = View.VISIBLE

        databaseProducto.get().addOnSuccessListener { snapshot ->
            val acumulado = mutableMapOf<String, Float>()
            snapshot.children.forEach { doc ->
                val cat   = doc.child("categoria").getValue(String::class.java) ?: "Sin categoría"
                val stock = doc.child("cantidad").getValue(Long::class.java)?.toFloat() ?: 0f
                acumulado[cat] = acumulado.getOrDefault(cat, 0f) + stock
            }

            val entries = acumulado.map { (cat, total) ->
                PieEntry(total, cat)
            }

            mostrarPieChart(entries, "Stock por categoría")
        }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarPieChart(entries: List<PieEntry>, centerText: String) {
        if (entries.isEmpty()) return

        val customColors = listOf(
            R.color.color_ropa_hombre,
            R.color.joyeria,
            R.color.electronicos,
            R.color.color_ropa_mujer
        )

        val palette = customColors
            .map { resources.getColor(it, null) }
            .take(entries.size)
            .toList()

        val dataSet = PieDataSet(entries, "").apply {
            colors            = palette
            sliceSpace        = 2f
            valueTextSize     = 12f
            valueFormatter    = PercentFormatter(pieChart)
        }

        val data = PieData(dataSet).apply {
            setValueTextColor(resources.getColor(R.color.black, null))
        }

        pieChart.apply {
            this.data = data
            setUsePercentValues(true)
            description.isEnabled = false
            this.centerText = centerText
            setCenterTextSize(16f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)

            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                xEntrySpace = 8f
                yEntrySpace = 4f
                isWordWrapEnabled = true
            }

            animateY(800)
            invalidate()
        }
    }

    private fun mostrarBarChart(entries: List<BarEntry>, labels: List<String>, labelDataSet: String) {
        if (entries.isEmpty()) return

        val palette = ColorTemplate.COLORFUL_COLORS
            .map { it }
            .toList()
            .take(entries.size)

        val dataSet = BarDataSet(entries, labelDataSet).apply {
            setDrawValues(true)
            valueTextSize = 12f
            colors = palette
        }
        val barData = BarData(dataSet).apply {
            barWidth = 0.9f
        }

        val legendEntries = labels.mapIndexed { i, name ->
            LegendEntry().apply {
                formColor = palette[i]
                label     = name
            }
        }

        barChart.apply {
            data = barData

            xAxis.apply {
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            axisLeft.apply {
                granularity = 1f
            }
            axisRight.isEnabled = false

            description.isEnabled = false

            barChart.description.apply {
                text = labelDataSet
                textSize = 16f
                typeface  = Typeface.DEFAULT_BOLD
                textColor = resources.getColor(R.color.black, null)
                setPosition(barChart.width/2F, 40f)
                isEnabled = true
                textAlign = Paint.Align.CENTER
            }

            legend.apply {
                isEnabled = true
                setCustom(legendEntries)
                orientation = Legend.LegendOrientation.HORIZONTAL
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                setDrawInside(false)
                isWordWrapEnabled = true
                yEntrySpace = 4f
                xEntrySpace = 8f
            }

            dataSet.isHighlightEnabled = false
            dataSet.setDrawIcons(false)
            barChart.highlightValues(null)
            barChart.setTouchEnabled(false)

            barData.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
            animateY(800)
        }
    }

    private fun setListeners() {
        binding.btnCategorias.setOnClickListener {
            cargarPorCategoria()
        }

        binding.btnMasStock.setOnClickListener {
            cargarMasStock()
        }

        binding.btnMenosStock.setOnClickListener {
            cargarMenosStock()
        }

        binding.btnProveedores.setOnClickListener {
            startActivity(Intent(this, ProveedorActivity::class.java))
        }

        binding.btnClientes.setOnClickListener {
            startActivity(Intent(this, ClienteActivity::class.java))
        }

        binding.btnEntradas.setOnClickListener {
            startActivity(Intent(this, EntradaActivity::class.java))
        }

        binding.btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        binding.btnSalidas.setOnClickListener {
            startActivity(Intent(this, SalidaActivity::class.java))
        }

        binding.btnEmpleados.setOnClickListener {
            startActivity(Intent(this, EmpleadoActivity::class.java))
        }

        binding.btnUsuarios.setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        binding.menu.setCheckedItem(R.id.item_inicio)

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
                R.id.item_clientes -> {
                    startActivity(Intent(this, ClienteActivity::class.java))
                    true
                }
                R.id.item_proveedores -> {
                    startActivity(Intent(this, ProveedorActivity::class.java))
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
                R.id.item_cerrar_sesion -> {
                    auth.signOut()
                    preferences.limpiarPreferencias()
                    startActivity(Intent(this, MainActivity::class.java))
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
                R.id.item_salir -> {
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtenerUsuario() {
        binding.tvBienvenida.text = "Bienvenido " +  auth.currentUser?.email.toString()
    }

    private fun obtenerCredenciales(){
        val admin = preferences.isAdmin()

        if(!admin){
            binding.btnUsuarios.visibility = View.GONE
            binding.btnEmpleados.visibility = View.GONE
            binding.espaciador2.visibility = View.GONE

            binding.menu.menu.removeItem(R.id.item_admin)
        }
    }
}
package com.example.proyectofinal.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapaBinding
    private lateinit var mapa: GoogleMap

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permisos ->
        if(
            permisos[Manifest.permission.ACCESS_FINE_LOCATION]==true
            ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION]==true
        ) {
            gestionarLocalizacion()
        }else{
            Toast.makeText(this, "El usuario denego los permisos...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iniciarFragment()
        setListeners()
    }

    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun iniciarFragment() {
        val fragment = SupportMapFragment()
        fragment.getMapAsync(this)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fv_mapa, fragment)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.uiSettings.isZoomControlsEnabled = true
        ponerMarcador(LatLng(36.8564908,-2.4533063))
        gestionarLocalizacion()
    }

    private fun gestionarLocalizacion() {
        if(!::mapa.isInitialized) return
        if(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            mapa.isMyLocationEnabled = true
            mapa.uiSettings.isMyLocationButtonEnabled = true
        }else{
            preguntarPermisos()
        }
    }

    private fun preguntarPermisos() {
        if(
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            pedirConfirmacion()
        }else{
            escogerPermisos()
        }
    }

    private fun escogerPermisos() {
        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun pedirConfirmacion() {
        AlertDialog.Builder(this).setTitle("Permisos de ubicación").setMessage("Para saber su ubicación.")
            .setNegativeButton("Cancelar"){
                    dialog, _-> dialog.dismiss()
            }
            .setCancelable(false)
            .setPositiveButton("Aceptar"){
                    dialog,_->startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
                dialog.dismiss()
            }
            .create()
            .dismiss()
    }

    private fun ponerMarcador(coord: LatLng) {
        val marker = MarkerOptions().position(coord).title("Sortex")
        mapa.addMarker(marker)
        iniciarAnimacion(coord, 15f)
    }

    private fun iniciarAnimacion(coord: LatLng, zoom: Float) {
        mapa.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coord, zoom),
            5000,
            null
        )
    }

    override fun onRestart() {
        super.onRestart()
        gestionarLocalizacion()
    }
}
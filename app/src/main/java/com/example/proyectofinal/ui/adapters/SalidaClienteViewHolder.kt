package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.databinding.SalidaClienteLayoutBinding

class SalidaClienteViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = SalidaClienteLayoutBinding.bind(v)

    fun render(salida: Salida, infoSalida: (Salida) -> Unit) {
        binding.tvNombre.text = salida.nombre + " " + salida.precio
        binding.tvFecha.text = salida.fecha_salida + " - " + salida.hora_salida

        if(salida.estado == "Completada"){
            binding.layout.setBackgroundResource(R.color.color_fondo_terminado)
        }

        binding.btnInfo.setOnClickListener {
            infoSalida(salida)
        }
    }
}

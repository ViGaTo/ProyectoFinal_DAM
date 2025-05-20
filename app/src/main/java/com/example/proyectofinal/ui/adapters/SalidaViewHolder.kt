package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Salida
import com.example.proyectofinal.databinding.SalidaLayoutBinding

class SalidaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = SalidaLayoutBinding.bind(v)

    fun render(salida: Salida, deleteSalida: (Salida) -> Unit, updateSalida: (Salida) -> Unit, infoSalida: (Salida) -> Unit
    ) {
        binding.tvNombre.text = salida.nombre + " " + salida.precio
        binding.tvFecha.text = salida.fecha_salida + " - " + salida.hora_salida

        binding.btnDelete.setOnClickListener {
            deleteSalida(salida)
        }

        binding.btnUpdate.setOnClickListener {
            updateSalida(salida)
        }

        binding.btnInfo.setOnClickListener {
            infoSalida(salida)
        }
    }

}

package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.databinding.EntradaProveedorLayoutBinding

class EntradaProveedorViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = EntradaProveedorLayoutBinding.bind(v)

    fun render(entrada: Entrada, infoEntrada: (Entrada) -> Unit
    ) {
        binding.tvNombre.text = entrada.nombre + " " + entrada.precio
        binding.tvFecha.text = entrada.fecha_entrada + " - " + entrada.hora_entrada

        if(entrada.estado == "Completada"){
            binding.layout.setBackgroundResource(R.color.color_fondo_terminado)
        }

        binding.btnInfo.setOnClickListener {
            infoEntrada(entrada)
        }
    }

}

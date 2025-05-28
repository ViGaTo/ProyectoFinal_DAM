package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Entrada
import com.example.proyectofinal.databinding.EntradaLayoutBinding

class EntradaViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = EntradaLayoutBinding.bind(v)

    fun render(entrada: Entrada, deleteEntrada: (Entrada) -> Unit, updateEntrada: (Entrada) -> Unit, infoEntrada: (Entrada) -> Unit
    ) {
        binding.tvNombre.text = entrada.nombre + " " + entrada.precio + "€"
        binding.tvFecha.text = entrada.fecha_entrada + " - " + entrada.hora_entrada

        binding.btnDelete.setOnClickListener {
            deleteEntrada(entrada)
        }

        binding.btnUpdate.setOnClickListener {
            updateEntrada(entrada)
        }

        binding.btnInfo.setOnClickListener {
            infoEntrada(entrada)
        }
    }
}

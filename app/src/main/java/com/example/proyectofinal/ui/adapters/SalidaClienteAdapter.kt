package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Salida

class SalidaClienteAdapter(var lista: List<Salida>, private val infoSalida: (Salida) -> Unit) : RecyclerView.Adapter<SalidaClienteViewHolder>() {
    fun actualizarLista(newList: List<Salida>) {
        lista = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalidaClienteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.salida_cliente_layout, parent, false)
        return SalidaClienteViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: SalidaClienteViewHolder, position: Int) {
        holder.render(lista[position], infoSalida)
    }
}
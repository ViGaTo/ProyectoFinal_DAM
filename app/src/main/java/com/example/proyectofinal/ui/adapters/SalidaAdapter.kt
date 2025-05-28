package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Salida

class SalidaAdapter(var lista: List<Salida>, private val deleteSalida: (Salida) -> Unit, private val updateSalida: (Salida) -> Unit, private val infoSalida: (Salida) -> Unit):
    RecyclerView.Adapter<SalidaViewHolder>() {
    fun actualizarLista(newList: List<Salida>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalidaViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.salida_layout, parent, false)
        return SalidaViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: SalidaViewHolder, position: Int) {
        holder.render(lista[position], deleteSalida, updateSalida, infoSalida)
    }
}
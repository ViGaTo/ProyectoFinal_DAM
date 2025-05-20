package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Entrada

class EntradaProveedorAdapter(var lista: List<Entrada>, private val infoEntrada: (Entrada) -> Unit): RecyclerView.Adapter<EntradaProveedorViewHolder>() {
    fun actualizarLista(newList: List<Entrada>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaProveedorViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entrada_proveedor_layout, parent, false)
        return EntradaProveedorViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: EntradaProveedorViewHolder, position: Int) {
        holder.render(lista[position], infoEntrada)
    }
}
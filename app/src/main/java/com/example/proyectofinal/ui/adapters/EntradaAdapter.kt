package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Entrada

class EntradaAdapter(var lista: List<Entrada>, private val deleteEntrada: (Entrada) -> Unit, private val updateEntrada: (Entrada) -> Unit, private val infoEntrada: (Entrada) -> Unit):
    RecyclerView.Adapter<EntradaViewHolder>() {
    fun actualizarLista(newList: List<Entrada>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entrada_layout, parent, false)
        return EntradaViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {
        holder.render(lista[position], deleteEntrada, updateEntrada, infoEntrada)
    }
}
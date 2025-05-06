package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Proveedor

class ProveedorAdapter(var lista: List<Proveedor>, private val deleteProveedor: (Proveedor) -> Unit, private val updateProveedor: (Proveedor) -> Unit, private val infoProveedor: (Proveedor) -> Unit):
    RecyclerView.Adapter<ProveedorViewHolder>() {
    fun actualizarLista(newList: List<Proveedor>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.proveedor_layout, parent, false)
        return ProveedorViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ProveedorViewHolder, position: Int) {
        holder.render(lista[position], deleteProveedor, updateProveedor, infoProveedor)
    }
}
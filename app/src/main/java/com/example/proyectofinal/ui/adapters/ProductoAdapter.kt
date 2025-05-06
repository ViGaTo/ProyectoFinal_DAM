package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Producto

class ProductoAdapter(var lista: List<Producto>, private val updateProducto: (Producto) -> Unit): RecyclerView.Adapter<ProductoViewHolder>() {
    fun actualizarLista(newList: List<Producto>) {
        lista = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.producto_layout, parent, false)
        return ProductoViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.render(lista[position], updateProducto)
    }

}
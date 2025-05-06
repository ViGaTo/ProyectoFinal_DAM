package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.databinding.ProductoLayoutBinding
import com.squareup.picasso.Picasso

class ProductoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = ProductoLayoutBinding.bind(v)

    fun render(producto: Producto, updateProducto: (Producto) -> Unit) {
        binding.tvNombre.text = producto.titulo + " | " + producto.precio.toString() + " €"
        binding.tvDescripcion.text = producto.descripcion
        binding.tvCantidad.text = producto.cantidad.toString() + " unidades"
        binding.tvCategoria.text = producto.categoria
        Picasso.get().load(producto.imagen).into(binding.ivProducto)

        binding.layoutProducto.setOnClickListener {
            updateProducto(producto)
        }
    }
}

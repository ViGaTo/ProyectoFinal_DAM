package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Producto
import com.example.proyectofinal.databinding.ProductoLayoutBinding
import com.squareup.picasso.Picasso

class ProductoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = ProductoLayoutBinding.bind(v)

    fun render(producto: Producto, updateProducto: (Producto) -> Unit) {
        binding.tvNombre.text = producto.titulo + " | " + producto.precio.toString() + " €"
        binding.tvCantidad.text = producto.cantidad.toString() + " ud/s"
        binding.tvCategoria.text = producto.categoria
        if(producto.categoria == "men's clothing"){
            binding.tvCategoria.setBackgroundResource(R.color.color_ropa_hombre)
        }else if(producto.categoria == "women's clothing"){
            binding.tvCategoria.setBackgroundResource(R.color.color_ropa_mujer)
        }else if(producto.categoria == "jewelery"){
            binding.tvCategoria.setBackgroundResource(R.color.joyeria)
        }else if(producto.categoria == "electronics"){
            binding.tvCategoria.setBackgroundResource(R.color.electronicos)
        }else{
            binding.tvCategoria.setBackgroundResource(R.color.color_defecto)
        }

        Picasso.get().load(producto.imagen).into(binding.ivProducto)

        binding.layoutProducto.setOnClickListener {
            updateProducto(producto)
        }
    }
}

package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Proveedor
import com.example.proyectofinal.databinding.ProveedorLayoutBinding

class ProveedorViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = ProveedorLayoutBinding.bind(v)

    fun render(proveedor: Proveedor, deleteProveedor: (Proveedor) -> Unit, updateProveedor: (Proveedor) -> Unit, infoProveedor: (Proveedor) -> Unit
    ) {
        binding.tvNombre.text = proveedor.nombre
        binding.tvContacto.text = proveedor.email + " | " + proveedor.telefono
        if(proveedor.estado == "Activo"){
            binding.layout.setBackgroundResource(R.color.fondo)
        }else{
            binding.layout.setBackgroundResource(R.color.fondo_inactivo)
        }

        binding.btnDelete.setOnClickListener {
            deleteProveedor(proveedor)
        }

        binding.btnUpdate.setOnClickListener {
            updateProveedor(proveedor)
        }

        binding.btnInfo.setOnClickListener {
            infoProveedor(proveedor)
        }
    }

}

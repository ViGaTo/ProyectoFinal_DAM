package com.example.proyectofinal.ui.adapters

import android.view.View

class ClienteViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    val binding = com.example.proyectofinal.databinding.ClienteLayoutBinding.bind(v)

    fun render(cliente: com.example.proyectofinal.data.models.Cliente, deleteCliente: (com.example.proyectofinal.data.models.Cliente) -> Unit, updateCliente: (com.example.proyectofinal.data.models.Cliente) -> Unit, infoCliente: (com.example.proyectofinal.data.models.Cliente) -> Unit
    ) {
        binding.tvNombre.text = cliente.nombre
        binding.tvContacto.text = cliente.email + " | " + cliente.telefono
        if(cliente.frecuencia == "Semanal") {
            binding.layout.setBackgroundResource(com.example.proyectofinal.R.color.fondo_semanal)
        }else if (cliente.frecuencia == "Mensual") {
            binding.layout.setBackgroundResource(com.example.proyectofinal.R.color.fondo_mensual)
        }else if (cliente.frecuencia == "Anual") {
            binding.layout.setBackgroundResource(com.example.proyectofinal.R.color.fondo_anual)
        }else{
            binding.layout.setBackgroundResource(com.example.proyectofinal.R.color.fondo_nuevo)
        }

        binding.btnDelete.setOnClickListener {
            deleteCliente(cliente)
        }

        binding.btnUpdate.setOnClickListener {
            updateCliente(cliente)
        }

        binding.btnInfo.setOnClickListener {
            infoCliente(cliente)
        }
    }

}

package com.example.proyectofinal.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Empleado
import com.example.proyectofinal.databinding.EmpleadoLayoutBinding

class EmpleadoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val binding = EmpleadoLayoutBinding.bind(v)

    fun render(empleado: Empleado, deleteEmpleado: (Empleado) -> Unit, updateEmpleado: (Empleado) -> Unit, infoEmpleado: (Empleado) -> Unit
    ) {
        binding.tvNombre.text = empleado.nombreApellidos
        binding.tvEmail.text = empleado.email

        binding.btnDelete.setOnClickListener {
            deleteEmpleado(empleado)
        }

        binding.btnUpdate.setOnClickListener {
            updateEmpleado(empleado)
        }

        binding.btnInfo.setOnClickListener {
            infoEmpleado(empleado)
        }
    }

}

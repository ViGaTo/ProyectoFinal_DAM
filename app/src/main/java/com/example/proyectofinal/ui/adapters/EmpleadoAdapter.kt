package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Empleado

class EmpleadoAdapter(var lista: List<Empleado>, private val deleteEmpleado: (Empleado) -> Unit, private val updateEmpleado: (Empleado) -> Unit, private val infoEmpleado: (Empleado) -> Unit):
    RecyclerView.Adapter<EmpleadoViewHolder>() {
    fun actualizarLista(newList: List<Empleado>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.empleado_layout, parent, false)
        return EmpleadoViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: EmpleadoViewHolder, position: Int) {
        holder.render(lista[position], deleteEmpleado, updateEmpleado, infoEmpleado)
    }

}
package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Cliente

class ClienteAdapter(var lista: List<Cliente>, private val deleteCliente: (Cliente) -> Unit, private val updateCliente: (Cliente) -> Unit, private val infoCliente: (Cliente) -> Unit):
    RecyclerView.Adapter<ClienteViewHolder>() {
    fun actualizarLista(newList: List<Cliente>) {
        lista = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cliente_layout, parent, false)
        return ClienteViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.render(lista[position], deleteCliente, updateCliente, infoCliente)
    }
}
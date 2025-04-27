package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.databinding.UsuarioLayoutBinding

class UsuarioAdapter(var lista: List<Usuario>, private val onActivamiento: (Usuario) -> Unit, private val onEditar: (Usuario) -> Unit): RecyclerView.Adapter<UsuarioViewHolder>() {
    fun actualizarLista(newList: List<Usuario>) {
        lista = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.usuario_layout, parent, false)
        return UsuarioViewHolder(v)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.render(lista[position], onActivamiento, onEditar)
    }

    override fun getItemCount() = lista.size


}

class UsuarioViewHolder(v: android.view.View) : RecyclerView.ViewHolder(v) {
    private val binding = UsuarioLayoutBinding.bind(v)
    fun render(usuario: Usuario, onActivamiento: (Usuario) -> kotlin.Unit, onEditar: (Usuario) -> kotlin.Unit) {
        binding.tvUsuario.text = usuario.email

        if (usuario.admin) {
            binding.btnAdmin.text = "Admin"
            binding.btnAdmin.setBackgroundColor(binding.btnAdmin.context.getColor(R.color.admin))
        } else {
            binding.btnAdmin.text = "User"
            binding.btnAdmin.setBackgroundColor(binding.btnAdmin.context.getColor(R.color.usuario))
        }

        if (usuario.activado) {
            binding.btnActivar.text = "Activo"
            binding.btnActivar.setBackgroundColor(binding.btnActivar.context.getColor(R.color.activado))
        } else {
            binding.btnActivar.text = "Inactivo"
            binding.btnActivar.setBackgroundColor(binding.btnActivar.context.getColor(R.color.desactivado))
        }

        binding.btnAdmin.setOnClickListener {
            onEditar(usuario)
        }
        binding.btnActivar.setOnClickListener {
            onActivamiento(usuario)
        }
    }

}
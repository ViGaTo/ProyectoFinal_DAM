package com.example.proyectofinal.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Preferences(c: Context) {
    val storage = c.getSharedPreferences("USER|ADMIN", MODE_PRIVATE)

    fun setAdmin(b: Boolean){
        storage.edit().putBoolean("ADMIN", b).apply()
    }

    fun isAdmin(): Boolean{
        return storage.getBoolean("ADMIN", false)
    }

    fun limpiarPreferencias(){
        storage.edit().clear().apply()
    }
}
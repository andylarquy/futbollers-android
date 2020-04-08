package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import com.google.gson.Gson




class Usuario {
    var id: Int? = null
    var nombre: String? = null
    var sexo: String? = null
    var posicion: String? = null
    var email: String? = null
    var lat: Double? = null
    var lon: Double? = null

    fun fromJson(s: String?): Usuario {
        return Gson().fromJson(s, Usuario::class.java)
    }

    fun createCopy(usuario: Usuario){
        id = usuario.id
        nombre = usuario.nombre
        sexo = usuario.sexo
        posicion = usuario.posicion
        email = usuario.email
        lat = usuario.lat
        lon = usuario.lon
    }
}
package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import androidx.databinding.BaseObservable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

class Usuario: BaseObservable() {
    var id: Int? = null

    var nombre: String? = null
    var password: String? = null
    var sexo: String? = null
    var posicion: String? = null
    var email:String? = null
    var lat: Double? = null
    var lon: Double? = null

    fun fromJson(s: String?): Usuario {
        return Gson().fromJson(s, Usuario::class.java)
    }

    fun toJson(o: Usuario?): JSONObject?{
        val jsonResult = JSONObject(GsonBuilder().serializeNulls().create().toJson(o))
        Log.i("LoginActivity", "usuario parseado a Json: "+jsonResult.toString())
        return jsonResult
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
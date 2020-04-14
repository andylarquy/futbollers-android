package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import androidx.databinding.library.baseAdapters.BR

class Usuario: BaseObservable() {
    var id: String? = null

    var nombre: String? = null
    var password: String? = null
    var email:String? = null
    var lat: Double? = null
    var lon: Double? = null

    @get:Bindable
    var posicion: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.posicion)
        }

    @get:Bindable
    var sexo: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.sexo)
        }

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
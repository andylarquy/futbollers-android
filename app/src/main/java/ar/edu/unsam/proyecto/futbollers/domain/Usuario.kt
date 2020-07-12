package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import androidx.databinding.library.baseAdapters.BR
import java.lang.Exception

class Usuario: BaseObservable() {
    var idUsuario: Long? = null

    var nombre: String? = null
    var password: String? = null
    var foto: String? = null
    var email:String? = null
    var lat: Double? = null
    var lon: Double? = null
    var token: String? = null



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
        val jsonResult = JSONObject(Gson().toJson(o))
        Log.i("LoginActivity", "usuario parseado a Json: "+jsonResult.toString())
        return jsonResult
    }

    fun createCopy(usuario: Usuario){
        idUsuario = usuario.idUsuario
        nombre = usuario.nombre
        sexo = usuario.sexo
        posicion = usuario.posicion
        email = usuario.email
        lat = usuario.lat
        lon = usuario.lon
    }

    fun tieneId(idBuscado: Long): Boolean{
        return idUsuario == idBuscado
    }

    fun validarSignUp(){

        if (nombre.isNullOrBlank()){
            throw Error("Debe ingresar un nombre")
        }

        if (email.isNullOrBlank()){
            throw Error("Debe ingresar un email")
        }

        //TODO: Con regex esto te queda espectacular
        if (!email!!.contains("@") || !email!!.contains(".") ){
            throw Error("Ingrese un email valido")
        }

        if (password.isNullOrBlank()){
            throw Error("Debe ingresar una contraseña")
        }

        if (password!!.length < 8){
            throw Error("La contraseña debe tener un minimo de 8 caracteres")
        }

        if (sexo.isNullOrBlank()){
            throw Error("Debe indicar su sexo")
        }

        if (posicion.isNullOrBlank()){
            throw Error("Debe ingresar una posicion deseada")
        }
    }

}
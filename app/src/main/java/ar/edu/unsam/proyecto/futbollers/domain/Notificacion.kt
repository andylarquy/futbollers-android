package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject

class Notificacion {

    val idNotificacion: Long? = null
    var titulo: String? = null
    var descripcion: String? = null
    val partido: Partido? = null
    val usuario: Usuario? = null
    var usuarioReceptor: Usuario? = null

    //TODO: Al traer las notificaciones ocultar este booleano en el back
    val aceptado: Boolean? = null

    fun toJson(o: Notificacion?): JSONObject?{
        val jsonResult = JSONObject(Gson().toJson(o))
        return jsonResult
    }


}
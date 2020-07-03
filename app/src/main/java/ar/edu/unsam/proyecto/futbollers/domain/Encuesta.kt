package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject

class Encuesta {

    var idEncuesta: Long? = null
    val usuarioEncuestado: Usuario? = null
    val usuarioReferenciado: Usuario? = null
    val partido: Partido? = null
    var respuesta1: Boolean? = null
    var respuesta2: Boolean? = null
    var respuesta3: Boolean? = null

    fun toJson(o: Encuesta): JSONObject?{
        val jsonResult = JSONObject(Gson().toJson(o))
        Log.i("EncuestasActivity", "Encuesta parseada a Json: $jsonResult")
        return jsonResult
    }

}
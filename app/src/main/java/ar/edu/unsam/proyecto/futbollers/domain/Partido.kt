package ar.edu.unsam.proyecto.futbollers.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*

class Partido {

    var idPartido: Long? = null

    var equipo1: Equipo? = null
    var equipo2: Equipo? = null

    var empresa: Empresa? = null
    var canchaReservada: Cancha? = null
    var fechaDeReserva: Date? = null

    fun toJson(o: Partido?): JSONObject?{
        val jsonResult = JSONObject(Gson().toJson(o))
        Log.i("LoginActivity", "usuario parseado a Json: $jsonResult")
        return jsonResult
    }

}
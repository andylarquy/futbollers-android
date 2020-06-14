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
    val fechaDeCreacion: Date? = null
    val cantidadDeConfirmaciones: Int? = null

    fun toJson(o: Partido?): JSONObject?{
        val jsonResult = JSONObject(Gson().toJson(o))
        Log.i("LoginActivity", "usuario parseado a Json: $jsonResult")
        return jsonResult
    }

    fun esOwner(usuario: Usuario): Boolean {
        return equipo1!!.esOwner(usuario)
    }

    //TODO: Revisar el -1
    fun jugadoresRestantes(): Int {
        return canchaReservada!!.cantidadJugadores!! - cantidadDeConfirmaciones!! - 1
    }

    fun faltanJugadoresPorConfirmar(): Boolean {
        return cantidadDeConfirmaciones!! < canchaReservada!!.cantidadJugadores!!
    }

}
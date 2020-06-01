package ar.edu.unsam.proyecto.futbollers.domain

import com.google.gson.Gson
import java.time.LocalDateTime

class Partido {

    var idPartido: Long? = null

    var equipo1: Equipo? = null
    var equipo2: Equipo? = null

    var empresa: Empresa? = null
    var canchaReservada: Cancha? = null
    var fechaDeReserva: LocalDateTime? = null

}
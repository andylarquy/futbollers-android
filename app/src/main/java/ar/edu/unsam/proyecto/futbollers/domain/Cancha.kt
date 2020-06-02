package ar.edu.unsam.proyecto.futbollers.domain

class Cancha {

    var idCancha: Long? = null
    var cantidadJugadores: Int? = null
    var foto: String? = null
    var superficie: String? = null

    fun cantidadJugadoresPorEquipo(): Int{
        return cantidadJugadores!!/2
    }


}
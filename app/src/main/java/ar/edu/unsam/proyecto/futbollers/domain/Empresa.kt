package ar.edu.unsam.proyecto.futbollers.domain

import android.location.Location

class Empresa {

    var id: String? = null
    var nombre: String? = null

    //Revisar si tienen q estar
    var lat: Double? = null
    var lon: Double? = null

    var nombreDuenio: String? = null
    var email: String? = null
    var lugar: Location? = null
    var direccion: String? = null
    var foto: String? = null

    var canchas: List<Cancha> = ArrayList<Cancha>()

}
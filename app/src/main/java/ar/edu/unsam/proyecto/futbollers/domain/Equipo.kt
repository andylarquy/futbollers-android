package ar.edu.unsam.proyecto.futbollers.domain

class Equipo {
    var id: String? = null
    var nombre: String? = null
    var foto: String? = null

    var owner: Usuario? = null
    var integrantes: List<Usuario>? = ArrayList<Usuario>()
}
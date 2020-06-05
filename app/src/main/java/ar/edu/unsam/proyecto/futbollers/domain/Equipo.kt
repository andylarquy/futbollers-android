package ar.edu.unsam.proyecto.futbollers.domain

class Equipo {
    var idEquipo: Long? = null
    var nombre: String? = null
    var foto: String? = null

    var owner: Usuario? = null
    var integrantes: List<Usuario>? = ArrayList<Usuario>()

    fun esOwnerById(usuario: Usuario): Boolean{
        return owner!!.idUsuario!! == usuario.idUsuario
    }

    fun cantidadDeIntegrantes(): Int{
        return integrantes!!.size
    }

    fun contieneIntegrante(integranteBuscado: Usuario): Boolean {
        return integrantes!!.any { integrante ->
            integrante.idUsuario != (-1).toLong() && integrante.idUsuario == integranteBuscado.idUsuario
        }
    }

}
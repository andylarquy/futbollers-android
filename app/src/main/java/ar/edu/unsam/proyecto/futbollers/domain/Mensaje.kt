package ar.edu.unsam.proyecto.futbollers.domain

open class Mensaje() {

    val nombre: String? = null
    val foto: String? = null
    var mensaje: String? = null
    var type_mensaje: String? = null
    open var hora: Any? = null
    var idUsuario: String? = null


    constructor(mensaje: String?, type_mensaje: String?, idUsuario: Int?) : this() {
        this.mensaje = mensaje
        this.idUsuario = idUsuario.toString()
        this.type_mensaje = type_mensaje
    }

}
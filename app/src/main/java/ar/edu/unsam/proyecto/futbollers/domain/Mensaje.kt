package ar.edu.unsam.proyecto.futbollers.domain

import java.text.SimpleDateFormat
import java.util.*

open class Mensaje() {
    var mensaje: String? = null
    var nombre: String? = null
    var fotoPerfil: String? = null
    var type_mensaje: String? = null
    open var hora: Any? = null


    constructor(mensaje: String?, nombre: String?, fotoPerfil: String?, type_mensaje: String?) : this() {
        this.mensaje = mensaje
        this.nombre = nombre
        this.fotoPerfil = fotoPerfil
        this.type_mensaje = type_mensaje
    }

}
package ar.edu.unsam.proyecto.futbollers.domain

class MensajeEnviar : Mensaje {

    override var hora: Any? = null

    constructor() {}

    constructor(hora: Map<*, *>?) {
        this.hora = hora
    }

    constructor(
        mensaje: String?,
        nombre: String?,
        fotoPerfil: String?,
        type_mensaje: String?,
        hora: Map<*, *>?
    ) {
        this.mensaje = mensaje
        this.nombre = nombre
        this.fotoPerfil = fotoPerfil
        this.type_mensaje = type_mensaje
        this.hora = hora
    }

}
package ar.edu.unsam.proyecto.futbollers.domain

class MensajeEnviar : Mensaje {

    override var hora: Any? = null

    constructor() {}

    constructor(hora: Map<*, *>?) {
        this.hora = hora
    }

    constructor(
        mensaje: String?,
        type_mensaje: String?,
        hora: Map<*, *>?,
        idUsuario: Long?
    ) {
        this.mensaje = mensaje
        this.type_mensaje = type_mensaje
        this.hora = hora
        this.idUsuario = idUsuario.toString()
    }

}
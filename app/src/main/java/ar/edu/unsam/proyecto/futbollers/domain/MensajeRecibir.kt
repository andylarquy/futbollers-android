package ar.edu.unsam.proyecto.futbollers.domain

class MensajeRecibir : Mensaje {
    override var hora: Any? = null

    constructor() {}
    constructor(hora: Long?) {
        this.hora = hora
    }

    constructor(
        mensaje: String?,
        type_mensaje: String?,
        hora: Long?,
        idUsuario: Int?
    ) : super(mensaje, type_mensaje,idUsuario) {
        this.hora = hora
    }

}

package ar.edu.unsam.proyecto.futbollers.domain

class MensajeRecibir : Mensaje {
    override var hora: Any? = null

    constructor() {}
    constructor(hora: Long?) {
        this.hora = hora
    }

    constructor(
        mensaje: String?,
        nombre: String?,
        fotoPerfil: String?,
        type_mensaje: String?,
        hora: Long?
    ) : super(mensaje, nombre, fotoPerfil, type_mensaje) {
        this.hora = hora
    }

}

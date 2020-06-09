package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.PointerIcon.getSystemIcon
import android.view.PointerIcon.load
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ShareCompat.getCallingActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.*
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.PartidoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.customListAdapter
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_elegir_equipo_gps.view.*
import kotlinx.android.synthetic.main.dialog_elegir_jugador_gps.view.*
import kotlinx.android.synthetic.main.fragment_elegir_equipo_local.*


class ElegirEquipoVisitanteFragment : ElegirEquipoGenerico(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_elegir_equipo_local, container, false)
    }

    val partidoService = PartidoService

    lateinit var elegirEquipoAdapter: ElegirEquipoAdapter
    lateinit var integranteAdapter: IntegranteAdapter
    lateinit var elegirAmigosAdapter: ElegirAmigoAdapter

    val equipoService = EquipoService
    val usuarioService = UsuarioService
    val usuarioLogueado = UsuarioLogueado.usuario
    var rv = integrantes_list

    //Setup Dialogs
    //TODO: Poner lateinit
    var dialogEquipo: MaterialDialog? = null
    var dialogAmigos: MaterialDialog? = null
    var dialogEquipoGPS: MaterialDialog? = null
    var dialogJugadorGPS: MaterialDialog? = null

    //Parametros Equipo GPS
    var rangoDeBusquedaEquipo: Int? = null
    var sexoBusquedaEquipo: String? = null

    //Parametros Jugador GPS
    var rangoDeBusquedaJugador: Int? = null
    var sexoBusquedaJugador: String? = null
    var posicionBusquedaJugador: String? = null

    var todosLosCandidatos: MutableList<Usuario> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elegirEquipoAdapter = ElegirEquipoAdapter(context!!, this)
        elegirAmigosAdapter = ElegirAmigoAdapter(context!!, this)

        rv = integrantes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        integranteAdapter = IntegranteAdapter(context!!, this)
        rv.adapter = integranteAdapter

        btn_agregar_equipo.setOnClickListener() {
            dialogEquipo!!.show()
        }

        btn_agregar_amigo.setOnClickListener() {
            dialogAmigos!!.show()
        }

        btn_agregar_equipo_desconocido.setOnClickListener() {
            Log.i("ArmarPartidoActivity", rangoDeBusquedaEquipo.toString())
            dialogEquipoGPS!!.show()
        }

        btn_agregar_jugador_desconocido.setOnClickListener(){
            dialogJugadorGPS!!.show()
        }

    }

    fun modalElegirEquipoCallback(equipos: MutableList<Equipo>) {
        elegirEquipoAdapter.clear()
        equipos.removeAll{it.idEquipo == equipoLocalSeleccionado!!.idEquipo}
        elegirEquipoAdapter.items = equipos
        elegirEquipoAdapter.notifyDataSetChanged()

        //Oculto el spinner
        loading_spinner.visibility = INVISIBLE
    }

    fun modalElegirAmigosCallback(amigos: MutableList<Usuario>) {
        elegirAmigosAdapter.clear()

        amigos.forEach{amigo ->
            if(!equipoLocalSeleccionado!!.integrantes!!.any{it.idUsuario == amigo.idUsuario}){
                todosLosCandidatos.add(amigo)
            }
        }


        amigos.forEach{amigo ->
            if(!equipoLocalSeleccionado!!.integrantes!!.any{it.idUsuario == amigo.idUsuario} && !integranteAdapter.items.any{it.idUsuario == amigo.idUsuario}){
                elegirAmigosAdapter.add(amigo)
            }
        }

        elegirAmigosAdapter.notifyDataSetChanged()
    }

    fun setupDialogs() {
        setupDialogEquipo()
        setupDialogAmigos()
        setupDialogEquipoGPS()
        setupDialogJugadorGPS()
    }


    fun setupDialogEquipo() {
        //Preparo el dialog de equipo
        dialogEquipo = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Selecciona un equipo")
                .message(text = "Con " + canchaSeleccionada!!.cantidadJugadoresPorEquipo() + " integrantes")
                .customListAdapter(elegirEquipoAdapter)
        }
    }

    fun setupDialogAmigos() {
        //Preparo el dialog de amigos
        dialogAmigos = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Selecciona un amigo")
                .message(text = "Listado de amigos")
                .customListAdapter(elegirAmigosAdapter)
        }
    }

    fun setupDialogEquipoGPS() {
        //Preparo el dialog de equipo por GPS
        dialogEquipoGPS = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Buscar Equipo por GPS")
                .message(text = "Selecciona parametros de busqueda")
                .positiveButton(text = "Aceptar"){
                    if(parametrosEquipoGPSSonValidos(it.view)) {
                        rangoDeBusquedaEquipo = it.view.combo_distancia.text.toString().toInt()
                        sexoBusquedaEquipo = it.view.combo_sexo_equipo.text.toString()

                        val nuevoUsuario = Usuario()
                        nuevoUsuario.idUsuario = -1
                        nuevoUsuario.sexo = sexoBusquedaEquipo
                        nuevoUsuario.email = rangoDeBusquedaEquipo.toString()

                        val nuevoEquipo = Equipo()
                        nuevoEquipo.idEquipo = -1
                        nuevoEquipo.foto = "https://i.imgur.com/c9zvT8Z.png"
                        nuevoEquipo.owner = usuarioLogueado

                        nuevoEquipo.rellenarConUsuario(nuevoUsuario, canchaSeleccionada!!.cantidadJugadoresPorEquipo())

                        equipoVisitanteSeleccionado = nuevoEquipo

                        postPartido()

                    }else{
                        Toasty.error(context, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){
                    rangoDeBusquedaEquipo = null
                    sexoBusquedaEquipo = null
                }
                .customView(R.layout.dialog_elegir_equipo_gps)
        }

        val customView = dialogEquipoGPS?.getCustomView()

        val spinnerDistancia = customView?.combo_distancia
        val spinnerSexo = customView?.combo_sexo_equipo

        val adapterDistancia = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.DISTANCIAS)
        spinnerDistancia?.setAdapter(adapterDistancia)

        val adapterSexo = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.SEXO)
        spinnerSexo?.setAdapter(adapterSexo)

    }

    fun setupDialogJugadorGPS(){
        //Preparo el dialog de equipo por GPS
        dialogJugadorGPS = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Buscar Jugador por GPS")
                .message(text = "Selecciona parametros de busqueda")
                .positiveButton(text = "Aceptar"){
                    if(parametrosJugadorGPSSonValidos(it.view)) {
                        rangoDeBusquedaJugador = it.view.combo_distancia_jugador.text.toString().toInt()
                        sexoBusquedaJugador = it.view.combo_sexo_jugador.text.toString()
                        posicionBusquedaJugador = it.view.combo_posicion_jugador.text.toString()

                        val nuevoIntegrante = Usuario()
                        nuevoIntegrante.idUsuario = -1
                        nuevoIntegrante.sexo = sexoBusquedaEquipo
                        nuevoIntegrante.foto = "https://i.imgur.com/c9zvT8Z.png"
                        nuevoIntegrante.posicion = posicionBusquedaJugador
                        nuevoIntegrante.email = rangoDeBusquedaJugador.toString()

                        integranteAdapter.items.add(nuevoIntegrante)
                        integranteAdapter.notifyItemInserted(integranteAdapter.items.size)
                        cantidadJugadores.text = integranteAdapter.items.size.toString()

                        refrescarListaDeAmigos()

                    }else{
                        Toasty.error(context, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){
                    rangoDeBusquedaJugador = null
                    sexoBusquedaJugador = null
                    posicionBusquedaJugador = null
                }
                .customView(R.layout.dialog_elegir_jugador_gps)
        }

        val customView = dialogJugadorGPS?.getCustomView()

        val spinnerDistancia = customView?.combo_distancia_jugador
        val spinnerSexo = customView?.combo_sexo_jugador
        val spinnerPosicion = customView?.combo_posicion_jugador

        val adapterDistancia = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.DISTANCIAS)
        spinnerDistancia?.setAdapter(adapterDistancia)

        val adapterSexo = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.SEXO)
        spinnerSexo?.setAdapter(adapterSexo)

        val adapterPosicion = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.POSICIONES)
        spinnerPosicion?.setAdapter(adapterPosicion)
    }

    fun parametrosEquipoGPSSonValidos(vistaGPS: View): Boolean{

        var valido = true

        if(vistaGPS.combo_distancia?.text.toString() == "Rango de busqueda"){
            valido = false
        }

        if(vistaGPS.combo_sexo_equipo?.text.toString() == "Sexo del equipo"){
            valido = false
        }

        return valido
    }

    fun parametrosJugadorGPSSonValidos(vistaGPS: View): Boolean{

        var valido = true

        if(vistaGPS.combo_distancia_jugador?.text.toString() == "Rango de busqueda"){
            valido = false
        }

        if(vistaGPS.combo_posicion_jugador?.text.toString() == "Posicion del jugador"){
            valido = false
        }

        if(vistaGPS.combo_sexo_jugador?.text.toString() == "Sexo del jugador"){
            valido = false
        }

        return valido
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {



        // TODO: Esto en principio sobra
        // Handler().postDelayed({ callback.goToNextStep() }, 1000L)
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        val equipoTemporal = Equipo()
        equipoTemporal.integrantes = integranteAdapter.items
        equipoTemporal.foto = "https://i.imgur.com/Tyf5hJn.png"
        equipoTemporal.owner = usuarioLogueado
        equipoTemporal.idEquipo = -2

        if(esValidoComoEquipoTemporal(equipoTemporal)) {
            equipoVisitanteSeleccionado = equipoTemporal
            postPartido()
        }


    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        equipoVisitanteSeleccionado = null
        callback.goToPrevStep()
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onSelected() {
        showStepperNavigation()

        setupDialogs()

        //Ya que vas para alla cargame los equipos
        equipoService.getEquiposAdministradosPorElUsuario(context!!, usuarioLogueado, ::modalElegirEquipoCallback)

        //Ah, y me vas a buscar los amigos tambien
        usuarioService.getAmigosDelUsuario( context!!, usuarioLogueado, ::modalElegirAmigosCallback)

        loading_spinner.visibility = VISIBLE

        totalJugadores.text = "/" + (canchaSeleccionada?.cantidadJugadores?.div(2)).toString()

        cantidadJugadores.text = integranteAdapter.items.size.toString()

        elegir_equipo_titulo.text = "Elegir Equipo Visitante"

        //Cambios esteticos para diferenciarlos
        btn_agregar_equipo.setTextColor(Color.parseColor("#03DAC5"))
        btn_agregar_amigo.setTextColor(Color.parseColor("#03DAC5"))
        btn_agregar_equipo_desconocido.setTextColor(Color.parseColor("#03DAC5"))
        btn_agregar_jugador_desconocido.setTextColor(Color.parseColor("#03DAC5"))

        //Reset parametros generales
        integranteAdapter.items.clear()
        cantidadJugadores.text = integranteAdapter.items.size.toString()
        integranteAdapter.notifyDataSetChanged()

        }

    override fun onError(error: VerificationError) {}


    //TODO: Delegar en un "validarEquipoClick" feliz (con un flag status)
    //TODO: Agregar tmb validacion de que usuarioLogueado es el owner
    override fun onElegirEquipoClick(position: Int) {
        val equipo = elegirEquipoAdapter.getItem(position)

        if(ningunJugadorDelEquipoSeleccionadoEstaEnElLocal(equipo)){

            if (equipo.cantidadDeIntegrantes() == canchaSeleccionada!!.cantidadJugadoresPorEquipo()) {

                if(equipoLocalSeleccionado!!.idEquipo != (-1).toLong() && equipoLocalSeleccionado!!.idEquipo != equipo!!.idEquipo!!.toLong()){
                        dialogEquipo?.dismiss()
                        equipoVisitanteSeleccionado = equipo

                        postPartido()
                    }else{
                        Toasty.error(context!!, "Este equipo ya fue elegido como equipo local", Toast.LENGTH_SHORT, true).show()
                    }
            } else {
                Toasty.error(context!!, "La cantidad de jugadores debe ser " + canchaSeleccionada!!.cantidadJugadoresPorEquipo(), Toast.LENGTH_SHORT, true).show()
            }

        }else{
            Toasty.error(context!!, "El equipo seleccionado tiene integrantes que forman parte del equipo local", Toast.LENGTH_SHORT, true).show()
        }

    }

    fun ningunJugadorDelEquipoSeleccionadoEstaEnElLocal(equipo: Equipo):Boolean{
        return equipo.integrantes!!.all { integrante ->
            equipoLocalSeleccionado!!.integrantes!!.all{it.idUsuario != integrante.idUsuario}
            }
    }

    override fun onElegirAmigoClick(position: Int) {
        val integrante = elegirAmigosAdapter.getItem(position)

        if (integrante !in integranteAdapter.items) {
            if (equipoLocalSeleccionado!!.contieneIntegrante(integrante!!)){
                Toasty.error(context!!, "El integrante forma parte del equipo local", Toast.LENGTH_SHORT, true).show()
            }else {
                integranteAdapter.items.add(integrante)
                integranteAdapter.notifyItemInserted(integranteAdapter.items.size)
                cantidadJugadores.text = integranteAdapter.items.size.toString()

                refrescarListaDeAmigos()

                dialogAmigos?.dismiss()
            }
        }else{
            Toasty.error(context!!, "El integrante ya fue añadido", Toast.LENGTH_LONG, true).show()
        }

    }

    override fun onDeleteIntegranteClick(position: Int) {
        val integrante = integranteAdapter.getItem(position)
        integranteAdapter.remove(integrante)
        integranteAdapter.notifyDataSetChanged()
        cantidadJugadores.text = integranteAdapter.items.size.toString()
        refrescarListaDeAmigos()
    }

    fun refrescarListaDeAmigos(){
        elegirAmigosAdapter.items.clear()

        todosLosCandidatos.forEach{
            if(!esIntegrante(it)){
                elegirAmigosAdapter.items.add(it)
            }
        }

        elegirAmigosAdapter.notifyDataSetChanged()
    }

    fun esIntegrante(usuario: Usuario): Boolean{
        return integranteAdapter.items.any{it.idUsuario == usuario.idUsuario}
    }

    fun postPartido(){
        val partido = Partido()
        partido.equipo1 = equipoLocalSeleccionado
        partido.equipo2 = equipoVisitanteSeleccionado
        partido.empresa = empresaSeleccionada
        partido.canchaReservada = canchaSeleccionada
        partido.fechaDeReserva = fechaSeleccionada

        partidoService.postNuevoPartido(context!!, partido, ::callbackPostPartido)
        Log.i("ArmarPartidoActivity", "SE ARMO UN PARTIDO VIEJA")

    }

    fun callbackPostPartido(){
        Toasty.success(context!!, "¡El partido ha sido creado correctamente!", Toast.LENGTH_SHORT, true).show()
        activity!!.finish()
    }


}







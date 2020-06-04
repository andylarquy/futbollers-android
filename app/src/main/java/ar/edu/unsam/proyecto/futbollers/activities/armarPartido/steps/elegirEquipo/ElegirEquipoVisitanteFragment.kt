package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.canchaSeleccionada
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.equipoLocalSeleccionado
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.showStepperNavigation
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elegirEquipoAdapter =
            ElegirEquipoAdapter(
                context!!,
                this
            )
        elegirAmigosAdapter =
            ElegirAmigoAdapter(
                context!!,
                this
            )

        rv = integrantes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        integranteAdapter = context?.let {
            IntegranteAdapter(
                it,
                this
            )
        }!!
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
        elegirEquipoAdapter.items = equipos
        elegirEquipoAdapter.notifyDataSetChanged()

        //Oculto el spinner
        loading_spinner.visibility = INVISIBLE
    }

    fun modalElegirAmigosCallback(amigos: MutableList<Usuario>) {
        elegirAmigosAdapter.clear()
        elegirAmigosAdapter.items = amigos
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

                        val nuevoEquipo = Equipo()
                        nuevoEquipo.idEquipo = -1
                        nuevoEquipo.foto = "https://i.imgur.com/c9zvT8Z.png"
                        nuevoEquipo.owner = nuevoUsuario

                    }else{
                        Toasty.error(context, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){
                    it.view.combo_distancia.setText("Rango de busqueda", false)
                    it.view.combo_sexo_equipo.setText("Sexo del equipo", false)
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

                        integranteAdapter.items.add(nuevoIntegrante)
                        integranteAdapter.notifyItemInserted(integranteAdapter.items.size)
                        cantidadJugadores.text = integranteAdapter.items.size.toString()

                    }else{
                        Toasty.error(context, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){
                    it.view.combo_distancia.setText("Rango de busqueda", false)
                    it.view.combo_sexo_equipo.setText("Sexo del equipo", false)
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

        if(vistaGPS.combo_distancia?.text.toString() == "Sexo del equipo"){
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

        Handler().postDelayed({ callback.goToNextStep() }, 1000L)
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        Toast.makeText(this.context, "FIN!!", Toast.LENGTH_SHORT).show()
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onSelected() {
        showStepperNavigation()

        setupDialogs()

        //Ya que vas para alla cargame los equipos
        equipoService.getEquiposDelUsuario(context!!, usuarioLogueado, ::modalElegirEquipoCallback)

        //Ah, y me vas a buscar los amigos tambien
        usuarioService.getAmigosDelUsuario( context!!, usuarioLogueado, ::modalElegirAmigosCallback )

        loading_spinner.visibility = VISIBLE

        totalJugadores.text = "/" + (canchaSeleccionada?.cantidadJugadores?.div(2)).toString()

        cantidadJugadores.text = integranteAdapter.items.size.toString()



    }

    override fun onError(error: VerificationError) {}


    override fun onElegirEquipoClick(position: Int) {
        val equipo = elegirEquipoAdapter.getItem(position)

        if (equipo.cantidadDeIntegrantes() == canchaSeleccionada!!.cantidadJugadoresPorEquipo()) {
            equipoLocalSeleccionado = equipo
            dialogEquipo?.dismiss()
        } else {
            Toasty.error(context!!, "La cantidad de jugadores debe ser " + canchaSeleccionada!!.cantidadJugadoresPorEquipo(), Toast.LENGTH_LONG, true).show()
        }

    }

    override fun onElegirAmigoClick(position: Int) {
        val integrante = elegirAmigosAdapter.getItem(position)

        if (integrante !in integranteAdapter.items) {
            integranteAdapter.items.add(integrante)
            integranteAdapter.notifyItemInserted(integranteAdapter.items.size)
            cantidadJugadores.text = integranteAdapter.items.size.toString()
            dialogAmigos?.dismiss()
        } else {
            Toasty.error(context!!, "El integrante ya fue añadido", Toast.LENGTH_LONG, true).show()
        }

    }

    override fun onDeleteIntegranteClick(position: Int) {
        val integrante = integranteAdapter.getItem(position)
        integranteAdapter.remove(integrante)
        integranteAdapter.notifyItemRemoved(position)
        cantidadJugadores.text = integranteAdapter.items.size.toString()
    }


}







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
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.*
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


class ElegirEquipoLocalFragment : ElegirEquipoGenerico() {

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

    var todosLosCandidatos: List<Usuario> = ArrayList()

    //Setup Dialogs
    lateinit var dialogEquipo: MaterialDialog
    lateinit var dialogAmigos: MaterialDialog
    lateinit var dialogEquipoGPS: MaterialDialog
    lateinit var dialogJugadorGPS: MaterialDialog

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
            dialogEquipo.show()
        }

        btn_agregar_amigo.setOnClickListener() {
            dialogAmigos.show()
        }

        btn_agregar_equipo_desconocido.setOnClickListener() {
            Log.i("ArmarPartidoActivity", rangoDeBusquedaEquipo.toString())
            dialogEquipoGPS.show()
        }

        btn_agregar_jugador_desconocido.setOnClickListener(){
            dialogJugadorGPS.show()
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
        todosLosCandidatos = ArrayList(amigos)
        elegirAmigosAdapter.clear()

        amigos.forEach{ amigo ->
            if(!integranteAdapter.items.any{it.idUsuario == amigo.idUsuario}){
                elegirAmigosAdapter.items.add(amigo)
            }
        }

        //elegirAmigosAdapter.items = amigos

        elegirAmigosAdapter.notifyDataSetChanged()
        integranteAdapter.notifyDataSetChanged()


    }

    fun setupDialogs() {

        setupDialogEquipo()
        setupDialogAmigos()
        setupDialogEquipoGPS()
        setupDialogJugadorGPS()
    }


    fun setupDialogEquipo() {
        //Preparo el dialog de equipo
        dialogEquipo = MaterialDialog(context!!)
            .title(text = "Selecciona un equipo")
            .message(text = "Con " + canchaSeleccionada!!.cantidadJugadoresPorEquipo() + " integrantes")
            .customListAdapter(elegirEquipoAdapter)

    }

    fun setupDialogAmigos() {
        //Preparo el dialog de amigos
        dialogAmigos = MaterialDialog(context!!)
            .title(text = "Selecciona un amigo")
            .message(text = "Listado de amigos")
            .customListAdapter(elegirAmigosAdapter)

    }

    fun setupDialogEquipoGPS() {
        //Preparo el dialog de equipo por GPS
        dialogEquipoGPS = MaterialDialog(context!!)
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

                        equipoLocalSeleccionado = nuevoEquipo

                        stepForward()

                    }else{
                        Toasty.error(context!!, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){
                    it.view.combo_distancia.setText("Rango de busqueda", false)
                    it.view.combo_sexo_equipo.setText("Sexo del equipo", false)
                    rangoDeBusquedaEquipo = null
                    sexoBusquedaEquipo = null
                }
                .customView(R.layout.dialog_elegir_equipo_gps)


        val customView = dialogEquipoGPS.getCustomView()

        val spinnerDistancia = customView.combo_distancia
        val spinnerSexo = customView.combo_sexo_equipo

        val adapterDistancia = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.DISTANCIAS)
        spinnerDistancia?.setAdapter(adapterDistancia)

        val adapterSexo = ArrayAdapter<String>(context!!, R.layout.dropdown_menu_popup_item, Constants.SEXO)
        spinnerSexo?.setAdapter(adapterSexo)

    }

    fun setupDialogJugadorGPS(){
        //Preparo el dialog de equipo por GPS
        dialogJugadorGPS = MaterialDialog(context!!)
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
                        Toasty.error(context!!, "Se ha dejado un campo vacío", Toast.LENGTH_SHORT, true).show()
                    }

                }
                .negativeButton(text = "Cancelar"){}
                .customView(R.layout.dialog_elegir_jugador_gps)


        val customView = dialogJugadorGPS.getCustomView()

        val spinnerDistancia = customView.combo_distancia_jugador
        val spinnerSexo = customView.combo_sexo_jugador
        val spinnerPosicion = customView.combo_posicion_jugador

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

        val equipoTemporal = Equipo()
        equipoTemporal.integrantes = integranteAdapter.items
        equipoTemporal.foto = "https://i.imgur.com/Tyf5hJn.png"
        equipoTemporal.owner = usuarioLogueado
        equipoTemporal.idEquipo = -2

        if(esValidoComoEquipoTemporal(equipoTemporal)) {
            equipoLocalSeleccionado = equipoTemporal
            Handler().postDelayed({ callback.goToNextStep() }, 1000L)
        }
    }



    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        Toast.makeText(this.context, "FIN!!", Toast.LENGTH_SHORT).show()
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        integranteAdapter.items.clear()
        integranteAdapter.notifyDataSetChanged()

        equipoLocalSeleccionado = null
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
        usuarioService.getAmigosDelUsuario( context!!, usuarioLogueado, ::modalElegirAmigosCallback )

        loading_spinner.visibility = VISIBLE

        totalJugadores.text = "/" + (canchaSeleccionada?.cantidadJugadores?.div(2)).toString()

        cantidadJugadores.text = integranteAdapter.items.size.toString()

        elegir_equipo_titulo.text = "Elegir Equipo Local"

        //TODO: Metodo agregar integrante
        //If el usuarioLogueado no es un integrante
        if(!integranteAdapter.items.contains(usuarioLogueado)) {
            integranteAdapter.items.add(usuarioLogueado)
            integranteAdapter.notifyItemInserted(integranteAdapter.items.size)
            cantidadJugadores.text = integranteAdapter.items.size.toString()
        }

        Toast.makeText(context!!, "Eq: "+equipoLocalSeleccionado?.idEquipo+" "+ equipoLocalSeleccionado?.integrantes?.map{it.idUsuario}+ " own: "+equipoLocalSeleccionado?.owner?.nombre, Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: VerificationError) {}


    override fun onElegirEquipoClick(position: Int) {

        val equipo = elegirEquipoAdapter.getItem(position)

        if(equipo.owner!!.idUsuario == usuarioLogueado.idUsuario) {
            if (equipo.cantidadDeIntegrantes() == canchaSeleccionada!!.cantidadJugadoresPorEquipo()) {

                equipoLocalSeleccionado = equipo

                dialogEquipo.dismiss()

                stepForward()
            } else {
                Toasty.error(
                    context!!,
                    "La cantidad de jugadores debe ser " + canchaSeleccionada!!.cantidadJugadoresPorEquipo(),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }else{
            Toasty.error(context!!, "Como hiciste eso? Solo podes agregar equipos de los que sos owner!", Toast.LENGTH_SHORT, true).show()
        }

    }

    override fun onElegirAmigoClick(position: Int) {
        val integrante = elegirAmigosAdapter.getItem(position)

        if (integrante !in integranteAdapter.items) {
            integranteAdapter.items.add(integrante)
            integranteAdapter.notifyDataSetChanged()
            cantidadJugadores.text = integranteAdapter.items.size.toString()
            refrescarListaDeAmigos()
            dialogAmigos.dismiss()
        } else {
            Toasty.error(context!!, "El integrante ya fue añadido", Toast.LENGTH_LONG, true).show()
        }

    }

    override fun onDeleteIntegranteClick(position: Int) {

        val integrante = integranteAdapter.getItem(position)

        if(integrante.idUsuario !== usuarioLogueado.idUsuario) {

            integranteAdapter.remove(integrante)
            integranteAdapter.notifyDataSetChanged()
            cantidadJugadores.text = integranteAdapter.items.size.toString()
            refrescarListaDeAmigos()
        }else{
            Toasty.error(context!!, "Debes formar parte del equipo local para continuar", Toast.LENGTH_SHORT, true).show()
        }
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


}


package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_elegir_equipo_gps.view.*
import kotlinx.android.synthetic.main.fragment_elegir_equipo_local.*
import kotlinx.android.synthetic.main.row_elegir_amigo.view.*
import kotlinx.android.synthetic.main.row_elegir_equipo.view.*
import kotlinx.android.synthetic.main.row_integrante.view.*


class ElegirEquipoLocalFragment : Fragment(), BlockingStep, ElegirEquipoMultipleClickListener, IntegranteClickListener,
    ElegirEquipoGPSClickListener {

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
    var dialogEquipo: MaterialDialog? = null
    var dialogAmigos: MaterialDialog? = null
    var dialogEquipoGPS: MaterialDialog? = null

    //Parametros Equipo GPS
    var rangoDeBusquedaEquipo: Int? = null
    var sexoBusquedaEquipo: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elegirEquipoAdapter = ElegirEquipoAdapter(context!!, this)
        elegirAmigosAdapter = ElegirAmigoAdapter(context!!, this)

        rv = integrantes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        integranteAdapter = context?.let { IntegranteAdapter(it, this) }!!
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
                Toasty.error(context!!, "El integrante ya fue añadido", Toast.LENGTH_LONG, true)
                    .show()
            }

        }

        override fun onDeleteIntegranteClick(position: Int) {
            val integrante = integranteAdapter.getItem(position)
            integranteAdapter.remove(integrante)
            integranteAdapter.notifyItemRemoved(position)
            cantidadJugadores.text = integranteAdapter.items.size.toString()
        }

    override fun onElegirEquipoGPSClick(position: Int) {
        //TODO: Cosas
    }

}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

    class IntegranteViewHolder(itemView: View, listener: IntegranteClickListener?) :
        BaseViewHolder<Usuario, IntegranteClickListener>(itemView, listener) {

        private val integranteNombre: TextView = itemView.integrante_nombre
        private val integranteFoto: ImageView = itemView.integrante_foto
        private val integrantePosicion: TextView = itemView.posicion_integrante


        init {
            listener?.run {
                itemView.trash_icon.setOnClickListener { onDeleteIntegranteClick(adapterPosition) }
            }
        }

        override fun onBind(item: Usuario) {
            integranteNombre.text = item.nombre
            integrantePosicion.text = item.posicion
            Picasso.get().load(item.foto).into(integranteFoto)
        }
    }

    class IntegranteAdapter(context: Context, listener: ElegirEquipoLocalFragment) :
        GenericRecyclerViewAdapter<Usuario, IntegranteClickListener, IntegranteViewHolder>(
            context,
            listener
        ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegranteViewHolder {
            return IntegranteViewHolder(inflate(R.layout.row_integrante, parent), listener)
        }
    }

interface IntegranteClickListener: BaseRecyclerListener {
    fun onDeleteIntegranteClick(position: Int)
}


////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////// ELEGIR EQUIPO /////////////////////////////////////////////////////////////////

    class ElegirEquipoAdapter(context: Context, listener: ElegirEquipoLocalFragment) :
        GenericRecyclerViewAdapter<Equipo, ElegirEquipoMultipleClickListener, ElegirEquipoViewHolder>(
            context, listener) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElegirEquipoViewHolder {
            return ElegirEquipoViewHolder(inflate(R.layout.row_elegir_equipo, parent), listener)
        }
    }



    class ElegirEquipoViewHolder(itemView: View, listener: ElegirEquipoMultipleClickListener?) :
        BaseViewHolder<Equipo, ElegirEquipoMultipleClickListener>(itemView, listener) {

        private val nombreEquipo: TextView = itemView.nombre_equipo
        private val cantidadJugadores: TextView = itemView.cantidad_jugadores
        private val equipoFoto: ImageView = itemView.equipo_foto

        private val warnIcon: ImageView = itemView.warning_icon

        init {
            listener?.run {
                itemView.setOnClickListener { onElegirEquipoClick(adapterPosition) }
            }
        }

        override fun onBind(item: Equipo) {

            nombreEquipo.text = item.nombre
            cantidadJugadores.text = item.integrantes!!.size.toString()
            Picasso.get().load(item.foto).into(equipoFoto)

            if (item.integrantes!!.size != canchaSeleccionada!!.cantidadJugadoresPorEquipo()) {
                cantidadJugadores.setTextColor(Color.parseColor("#DB0F13"))
                warnIcon.setImageResource(R.drawable.ic_baseline_warning_red_24)
            } else {
                cantidadJugadores.setTextColor(Color.parseColor("#808080"))
                warnIcon.setImageResource(0)
            }
        }
    }




////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////// ELEGIR AMIGOS /////////////////////////////////////////////////////////////////

    class ElegirAmigoAdapter(context: Context, listener: ElegirEquipoLocalFragment) :
        GenericRecyclerViewAdapter<Usuario, ElegirEquipoMultipleClickListener, ElegirAmigoViewHolder>(
            context, listener) {



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElegirAmigoViewHolder {
            return ElegirAmigoViewHolder(inflate(R.layout.row_elegir_amigo, parent), listener)
        }
    }

    class ElegirAmigoViewHolder(itemView: View, listener: ElegirEquipoMultipleClickListener?) :
        BaseViewHolder<Usuario, ElegirEquipoMultipleClickListener>(itemView, listener) {

        private val amigoNombre: TextView = itemView.amigo_nombre
        private val posicionAmigo: TextView = itemView.posicion_amigo
        private val amigoFoto: ImageView = itemView.amigo_foto

        init {
            listener?.run {
                itemView.setOnClickListener { onElegirAmigoClick(adapterPosition) }
            }
        }

        override fun onBind(item: Usuario) {
            amigoNombre.text = item.nombre
            posicionAmigo.text = item.posicion
            Picasso.get().load(item.foto).into(amigoFoto)
        }
    }

interface ElegirEquipoMultipleClickListener : BaseRecyclerListener {
    fun onElegirEquipoClick(position: Int)
    fun onElegirAmigoClick(position: Int)
}

//////////////////////////////////////////////////////////////////////////////////
//////////////////////////// BUSCAR EQUIPO GPS //////////////////////////////////


interface ElegirEquipoGPSClickListener : BaseRecyclerListener {
    fun onElegirEquipoGPSClick(position: Int)
}





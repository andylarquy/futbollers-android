package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.canchaSeleccionada
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.hideStepperNavigation
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.showStepperNavigation
import ar.edu.unsam.proyecto.futbollers.activities.home.fragments.EquipoFragment.EquipoAdapter
import ar.edu.unsam.proyecto.futbollers.activities.inicio.SignUpActivity
import ar.edu.unsam.proyecto.futbollers.domain.Cancha

import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_elegir_equipo_local.*
import kotlinx.android.synthetic.main.row_elegir_equipo.view.*
import kotlinx.android.synthetic.main.row_elegir_equipo.view.equipo_foto
import kotlinx.android.synthetic.main.row_integrante.view.*

class ElegirEquipoLocalFragment : Fragment(), BlockingStep, OnRecyclerItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_elegir_equipo_local, container, false)
    }

    lateinit var elegirEquipoAdapter: ElegirEquipoAdapter
    lateinit var integranteAdapter: IntegranteAdapter
    val equipoService = EquipoService
    val usuarioLogueado = UsuarioLogueado.usuario
    var rv = integrantes_list

    //Setup Dialog
    var dialogEquipo: MaterialDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elegirEquipoAdapter = ElegirEquipoAdapter(context!!, this)

        rv = integrantes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        integranteAdapter = context?.let { IntegranteAdapter(it, this) }!!
        rv.adapter = integranteAdapter

        btn_agregar_equipo.setOnClickListener(){
            dialogEquipo!!.show()
        }

    }

    fun modalElegirEquipoCallback(equipos: MutableList<Equipo>){
        elegirEquipoAdapter.clear()
        elegirEquipoAdapter.items = equipos
        elegirEquipoAdapter.notifyDataSetChanged()

        //Oculto el spinner
        loading_spinner.visibility = INVISIBLE
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

        //Ya que vas para alla cargame los equipos
        equipoService.getEquiposDelUsuario(context!!, usuarioLogueado, ::modalElegirEquipoCallback)

        //Preparo el dialog de equipo
        dialogEquipo = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Selecciona un equipo")
                .message(text = "Con "+canchaSeleccionada!!.cantidadJugadoresPorEquipo() +" integrantes")
                .customListAdapter(elegirEquipoAdapter)
        }

        loading_spinner.visibility = VISIBLE
    }

    override fun onError(error: VerificationError) {}

    override fun onItemClick(position: Int) {
        val equipo = elegirEquipoAdapter.getItem(position)

        if(equipo.cantidadDeIntegrantes() == canchaSeleccionada!!.cantidadJugadoresPorEquipo()){
            dialogEquipo?.dismiss()
        }else{
            Toasty.error(context!!, "La cantidad de jugadores debe ser "+ canchaSeleccionada!!.cantidadJugadoresPorEquipo(), Toast.LENGTH_LONG, true).show()
        }

    }

}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class IntegranteViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<Usuario, OnRecyclerItemClickListener>(itemView, listener) {

    private val integranteNombre: TextView = itemView.integrante_nombre
    private val integranteFoto: ImageView = itemView.integrante_foto
    private val integrantePosicion: TextView = itemView.posicion_integrante

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Usuario) {
        integranteNombre.text = item.nombre
        integrantePosicion.text = item.posicion
        Picasso.get().load(item.foto).into(integranteFoto)
    }
}

class IntegranteAdapter(context: Context, listener: ElegirEquipoLocalFragment) : GenericRecyclerViewAdapter<Usuario, OnRecyclerItemClickListener, IntegranteViewHolder>(context, listener) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegranteViewHolder {
        return IntegranteViewHolder(inflate(R.layout.row_integrante, parent), listener)
    }
}



class ElegirEquipoViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
    BaseViewHolder<Equipo, OnRecyclerItemClickListener>(itemView, listener) {

    private val nombreEquipo: TextView = itemView.nombre_equipo
    private val cantidadJugadores: TextView = itemView.cantidad_jugadores
    private val equipoFoto: ImageView = itemView.equipo_foto

    private val warnIcon: ImageView = itemView.warning_icon

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Equipo) {

        nombreEquipo.text = item.nombre
        cantidadJugadores.text = item.integrantes!!.size.toString()
        Picasso.get().load(item.foto).into(equipoFoto)

        if(item.integrantes!!.size != canchaSeleccionada!!.cantidadJugadoresPorEquipo()) {
            cantidadJugadores.setTextColor(Color.parseColor("#DB0F13"))
            warnIcon.setImageResource(R.drawable.ic_baseline_warning_red_24)
        }else{
            cantidadJugadores.setTextColor(Color.parseColor("#808080"))
            warnIcon.setImageResource(0)
        }
    }
}

class ElegirEquipoAdapter(context: Context, listener: ElegirEquipoLocalFragment) :
    GenericRecyclerViewAdapter<Equipo, OnRecyclerItemClickListener, ElegirEquipoViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElegirEquipoViewHolder {
        return ElegirEquipoViewHolder(inflate(R.layout.row_elegir_equipo, parent), listener)
    }
}
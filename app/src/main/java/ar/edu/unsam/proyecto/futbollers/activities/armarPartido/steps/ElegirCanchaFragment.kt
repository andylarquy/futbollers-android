package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.canchaSeleccionada
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.empresaSeleccionada
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.showStepperNavigation
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.services.CanchaService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout.*
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.dialog_seleccionar_cancha.*
import kotlinx.android.synthetic.main.fragment_elegir_cancha.*
import kotlinx.android.synthetic.main.row_cancha.view.*


class ElegirCanchaFragment : Fragment(), BlockingStep, OnRecyclerItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_elegir_cancha, container, false)
    }

    lateinit var canchaAdapter: CanchaAdapter
    val canchaService = CanchaService

    //Setup Dialog
    var dialogCanchas: MaterialDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //val usuarioLogueado = UsuarioLogueado.usuario

        canchaAdapter = CanchaAdapter(context!!, this)

        cancha_seleccionada.visibility = View.INVISIBLE


        dialogCanchas = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Selecciona la cancha")
                .message(text = "Listado de canchas")
                .customListAdapter(canchaAdapter)
        }

        btn_seleccionar_cancha.setOnClickListener() {
            dialogCanchas!!.show()

            if(canchaAdapter.items.isEmpty()){
                loading_spinner?.visibility = View.VISIBLE
            }

        }

    }

    fun callBackCanchas(canchas: MutableList<Cancha>) {
        canchaAdapter.items?.clear()
        canchaAdapter.items = canchas
        canchaAdapter.notifyDataSetChanged()

        //Desactivo pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE

    }

    override fun onItemClick(position: Int) {
        canchaSeleccionada = canchaAdapter.getItem(position)
        dialogCanchas?.dismiss()

        showCanchaSeleccionada()
    }

    override fun onSelected() {
        showStepperNavigation()

        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        canchaService.getCanchasDeLaEmpresa(context!!, empresaSeleccionada!!.id!!, ::callBackCanchas)

        if(canchaSeleccionada === null){
            hideCanchaSeleccionada()
        }

        canchaAdapter.items = ArrayList()
}

    override fun onNextClicked(callback: OnNextClickedCallback) {
        Handler().postDelayed({ callback.goToNextStep() }, 1000L)
    }

    override fun onCompleteClicked(callback: OnCompleteClickedCallback?) {}

    override fun onBackClicked(callback: OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onError(error: VerificationError) {}

    fun showCanchaSeleccionada(){

        if(canchaSeleccionada !== null) {

            Picasso.get().load(canchaSeleccionada!!.foto).into(cancha_seleccionada.cancha_foto)
            cancha_seleccionada.superficie.text = canchaSeleccionada!!.superficie
            cancha_seleccionada.cantidad_maxima.text = canchaSeleccionada!!.cantidadJugadores.toString()
            cancha_seleccionada.visibility = View.VISIBLE
        }else{
            hideCanchaSeleccionada()
        }
    }

    fun hideCanchaSeleccionada(){
        cancha_seleccionada.visibility = View.INVISIBLE
    }
}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class CanchaViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
    BaseViewHolder<Cancha, OnRecyclerItemClickListener>(itemView, listener) {

    private val cantidadMaxima: TextView = itemView.cantidad_maxima
    private val superficie: TextView = itemView.superficie
    private val canchaFoto: ImageView = itemView.cancha_foto

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Cancha) {
        cantidadMaxima.text = item.cantidadJugadores.toString()
        superficie.text = item.superficie
        Picasso.get().load(item.foto).into(canchaFoto)
    }
}

class CanchaAdapter(context: Context, listener: ElegirCanchaFragment) :
    GenericRecyclerViewAdapter<Cancha, OnRecyclerItemClickListener, CanchaViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanchaViewHolder {
        return CanchaViewHolder(inflate(R.layout.row_cancha, parent), listener)
    }
}
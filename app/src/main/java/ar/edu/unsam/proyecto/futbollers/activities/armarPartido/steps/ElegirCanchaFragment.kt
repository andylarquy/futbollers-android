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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.empresaSeleccionada
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.hideStepperNavigation
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.showStepperNavigation
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.services.CanchaService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout.*
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.dialog_seleccionar_cancha.*
import kotlinx.android.synthetic.main.activity_armar_partido.*
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
        }

    }

    fun callBackCanchas(canchas: MutableList<Cancha>) {
        canchaAdapter.items?.clear()
        canchaAdapter.items = canchas
        canchaAdapter.notifyDataSetChanged()

        Toast.makeText(context, canchas.map{cancha -> cancha.foto}.toString(), Toast.LENGTH_SHORT).show()

        //Desactivo pantalla de carga
        loading_spinner!!.visibility = View.INVISIBLE
        Log.i("ArmarPartidoActivity", canchaAdapter.items.size.toString())

    }

    override fun onItemClick(position: Int) {
        val canchaSeleccionada: Cancha = canchaAdapter.getItem(position)
        dialogCanchas?.dismiss()

        Toast.makeText(context, canchaSeleccionada.id, Toast.LENGTH_SHORT).show()

    }

    override fun onSelected() {
        showStepperNavigation()

        //Render pantalla de carga
        loading_spinner!!.visibility = View.VISIBLE

        canchaService.getCanchasDeLaEmpresa(context!!, empresaSeleccionada!!.id!!, ::callBackCanchas)
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
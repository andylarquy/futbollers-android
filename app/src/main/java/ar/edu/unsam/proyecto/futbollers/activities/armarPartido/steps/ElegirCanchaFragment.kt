package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.*
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.services.CanchaService
import ar.edu.unsam.proyecto.futbollers.services.Constants.simpleDateFormatter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.list.customListAdapter
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout.*
import com.stepstone.stepper.VerificationError
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_seleccionar_cancha.*
import kotlinx.android.synthetic.main.fragment_elegir_cancha.*
import kotlinx.android.synthetic.main.row_cancha.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    var dialogFecha: MaterialDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //val usuarioLogueado = UsuarioLogueado.usuario
        canchaAdapter = CanchaAdapter(context!!, this)

        cancha_seleccionada.visibility = View.INVISIBLE
        texto_fecha.visibility = View.INVISIBLE

        dialogCanchas = context?.let { context ->
            MaterialDialog(context)
                .title(text = "Selecciona la cancha")
                .message(text = "Listado de canchas")
                .customListAdapter(canchaAdapter)
        }

        dialogFecha = context?.let { context ->

            val minDate = Calendar.getInstance()
            minDate.add(Calendar.DATE, 3)

            MaterialDialog(context)
                .dateTimePicker(minDateTime = minDate, show24HoursView = true) { _, dateTime ->
                    // Use dateTime (Calendar)
                    fechaSeleccionada = dateTime.time
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

                    texto_fecha.text = dateFormat.format(fechaSeleccionada!!)
                    texto_fecha.visibility = View.VISIBLE

                    //TODO: Ir al back a validar la fecha
                }
        }

        btn_seleccionar_cancha.setOnClickListener() {
            dialogCanchas!!.show()

            if (canchaAdapter.items.isEmpty()) {
                loading_spinner?.visibility = View.VISIBLE
            }

        }

        btn_seleccionar_fecha.setOnClickListener() {
            dialogFecha!!.show()
        }

        input_field_codigo_promocion.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                codigoPromocionalSeleccionado = nuevoTexto.toString()
            }
        })
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

        canchaService.getCanchasDeLaEmpresa(
            context!!,
            empresaSeleccionada!!.id!!,
            ::callBackCanchas
        )

        if (canchaSeleccionada === null) {
            hideCanchaSeleccionada()
        }

        //Reset fields
        canchaAdapter.items = ArrayList()
        input_field_codigo_promocion.setText("")
        texto_fecha.text = ""
    }

    var statusG = true
    var fechaRepetida = false
    fun validarCampos(): Boolean {

        //Validar Cancha
        if (canchaSeleccionada === null) {
            Toasty.error(context!!, "Debe seleccionar una cancha.", Toast.LENGTH_LONG, true).show()
            statusG = false
        }

        //Validar Promocion
        if (codigoPromocionalSeleccionado != "") {
            //TODO: Ir al back a validar el codigo
            if (false) {
                Toasty.error(context!!, "El codigo promocional no es valido.", Toast.LENGTH_SHORT, true).show()
            }
        }

        if (fechaSeleccionada === null) {
            statusG = false
            Toasty.error(context!!, "Debe ingresar una fecha.", Toast.LENGTH_SHORT+5, true).show();
        }

        if (fechaRepetida) {
            statusG = false
            Toasty.error(context!!, "Esta fecha de reserva ya está ocupada.", Toast.LENGTH_SHORT, true).show()
        }

        return statusG
    }


    fun callbackValidarReserva(canchaDisponible: Boolean) {

        //!status significa que esa fecha esta ocupada
        fechaRepetida = !canchaDisponible
        Log.i("ArmarPartidoActivity", "fechaRepetida = "+fechaRepetida)
    }

    override fun onNextClicked(callback: OnNextClickedCallback) {

        statusG = true

        if (fechaSeleccionada !== null) {

            context?.let { canchaService.validarFechaDeReserva(it, fechaSeleccionada!!.time,::callbackValidarReserva) }

            goToNextStep(callback)
            //ESTE ELSE ESTA POR ALGO, NO ME ACUERDO QUE, PERO VOS DEJALO
        } else {
            goToNextStep(callback)
        }
    }

    fun goToNextStep(callback: OnNextClickedCallback){
        Handler().postDelayed({
            val status = validarCampos()
            if (status) {
                callback.goToNextStep()
            }
        }, 2000L)
    }

    override fun onCompleteClicked(callback: OnCompleteClickedCallback?) {}

    override fun onBackClicked(callback: OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onError(error: VerificationError) {}

    fun showCanchaSeleccionada() {

        if (canchaSeleccionada !== null) {

            Picasso.get().load(canchaSeleccionada!!.foto).into(cancha_seleccionada.cancha_foto)
            cancha_seleccionada.superficie.text = canchaSeleccionada!!.superficie
            cancha_seleccionada.cantidad_maxima.text =
                canchaSeleccionada!!.cantidadJugadores.toString()
            cancha_seleccionada.visibility = View.VISIBLE
        } else {
            hideCanchaSeleccionada()
        }
    }

    fun hideCanchaSeleccionada() {
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
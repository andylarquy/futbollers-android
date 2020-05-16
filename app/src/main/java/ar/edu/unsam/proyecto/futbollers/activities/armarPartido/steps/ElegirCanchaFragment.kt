package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirCancha

import android.content.Context
import ar.edu.unsam.proyecto.futbollers.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import ivb.com.materialstepper.stepperFragment
import kotlinx.android.synthetic.main.fragment_elegir_cancha.*
import kotlinx.android.synthetic.main.row_cancha.view.*


class ElegirCanchaFragment: stepperFragment(), OnRecyclerItemClickListener {
    override fun onNextButtonHandler(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_elegir_cancha, container, false)
    }

    lateinit var canchaAdapter: CanchaAdapter
    var rv = canchas_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val usuarioLogueado = UsuarioLogueado.usuario

        rv = canchas_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        canchaAdapter = context?.let { CanchaAdapter(it, this) }!!
        rv.adapter = canchaAdapter
        canchaAdapter.items = canchasHardcodeadas()

    }

    fun canchasHardcodeadas(): MutableList<Cancha> {
        var debugCancha = Cancha()
        debugCancha.foto = "https://i.imgur.com/8tKp3V1.jpg"
        debugCancha.id = "C1"
        debugCancha.cantidadJugadores = 5
        debugCancha.superficie = "sintetico"

        return mutableListOf(debugCancha)
    }

    override fun onItemClick(position: Int) {
        val canchaSeleccionada: Cancha = canchaAdapter.getItem(position)
        Log.i("ElegirCanchaFragment", "TODO: Darle comportamiento al click (no va a ser tan facil)")
        Toast.makeText(context, "TODO: Seleccionar cancha (con id: "+canchaSeleccionada.id+")", Toast.LENGTH_SHORT).show()
    }
}





//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class CanchaViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<Cancha, OnRecyclerItemClickListener>(itemView, listener) {


    private val cv: CardView = itemView.cv
    private val cantidadMaxima: TextView = itemView.cantidad_maxima
    private val superficie: TextView = itemView.superficie
    private val canchaFoto: ImageView = itemView.cancha_foto

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
            cv.setOnClickListener { onItemClick(adapterPosition) }
        }

    }

    override fun onBind(item: Cancha) {
        cantidadMaxima.text = item.cantidadJugadores.toString()
        superficie.text = item.superficie
        Picasso.get().load(item.foto).into(canchaFoto)
    }
}

class CanchaAdapter(context: Context, listener: ElegirCanchaFragment) : GenericRecyclerViewAdapter<Cancha, OnRecyclerItemClickListener, CanchaViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanchaViewHolder {
        return CanchaViewHolder(inflate(R.layout.row_cancha, parent), listener)
    }
}


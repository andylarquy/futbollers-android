package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.canchaSeleccionada
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import com.stepstone.stepper.BlockingStep
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_elegir_amigo.view.*
import kotlinx.android.synthetic.main.row_elegir_equipo.view.*
import kotlinx.android.synthetic.main.row_integrante.view.*

//https://i.imgflip.com/2s9nrb.jpg
abstract class ElegirEquipoGenerico : Fragment(), BlockingStep,
    ElegirEquipoMultipleClickListener,
    IntegranteClickListener{

    fun esValidoComoEquipoTemporal(equipo: Equipo): Boolean{
        var esValido = true

        if(equipo.cantidadDeIntegrantes() != canchaSeleccionada?.cantidadJugadoresPorEquipo()){
            esValido = false
            Toasty.error(context!!, "La cantidad de jugadores debe ser " + canchaSeleccionada!!.cantidadJugadoresPorEquipo(), Toast.LENGTH_SHORT, true).show()
        }

        return esValido

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

class IntegranteAdapter(context: Context, listener: ElegirEquipoGenerico) :
    GenericRecyclerViewAdapter<Usuario, IntegranteClickListener, IntegranteViewHolder>(
        context,
        listener
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegranteViewHolder {
        return IntegranteViewHolder(
            inflate(R.layout.row_integrante, parent),
            listener
        )
    }
}

interface IntegranteClickListener: BaseRecyclerListener {
    fun onDeleteIntegranteClick(position: Int)
}


////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////// ELEGIR EQUIPO /////////////////////////////////////////////////////////////////

class ElegirEquipoAdapter(context: Context, listener: ElegirEquipoGenerico) :
    GenericRecyclerViewAdapter<Equipo, ElegirEquipoMultipleClickListener, ElegirEquipoViewHolder>(
        context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElegirEquipoViewHolder {
        return ElegirEquipoViewHolder(
            inflate(R.layout.row_elegir_equipo, parent),
            listener
        )
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

class ElegirAmigoAdapter(context: Context, listener: ElegirEquipoGenerico) :
    GenericRecyclerViewAdapter<Usuario, ElegirEquipoMultipleClickListener, ElegirAmigoViewHolder>(
        context, listener) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElegirAmigoViewHolder {
        return ElegirAmigoViewHolder(
            inflate(R.layout.row_elegir_amigo, parent),
            listener
        )
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


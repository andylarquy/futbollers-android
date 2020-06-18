package ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.ArmarPartidoActivity
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.services.PartidoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.dateTransformer
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.simpleDateFormatter
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.toCalendar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_partido.*
import kotlinx.android.synthetic.main.row_fragment_partido.view.*
import java.util.*


class PartidoFragment(val floatButton: FloatingActionButton) : Fragment(),
    OnRecyclerItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_partido, container, false)
    }

    lateinit var partidoAdapter: PartidoAdapter
    var rv = partidos_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val partidoService = PartidoService
        val usuarioLogueado = UsuarioLogueado.usuario

        rv = partidos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        partidoAdapter = context?.let { PartidoAdapter(it, this) }!!
        rv.adapter = partidoAdapter


        refrescarPartidos()

        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        //Burocracia para ocultar el float button (recomiendo ocultarlo)

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scroll Down
                    if (floatButton.isShown) {
                        floatButton.hide()
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!floatButton.isShown) {
                        floatButton.show()
                    }
                }
            }
        })

        floatButton.setOnClickListener {
            val intent = Intent(activity, ArmarPartidoActivity::class.java).putExtra(
                "activity",
                activity!!::class.java.simpleName
            ).apply {}
            startActivity(intent)
        }

    }

    fun callBackPartidos(partidos: MutableList<Partido>) {
        partidoAdapter.items?.clear()
        partidoAdapter.items = partidos
        partidoAdapter.notifyDataSetChanged()
        Log.i("HomeActivity", partidoAdapter.items!!.map { it.empresa!!.foto }.toString())

        //Desactivo pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE
    }

    override fun onItemClick(position: Int) {
        val partido = partidoAdapter.getItem(position)
        PartidoService.confirmarPartido(context!!, partido, ::callbackConfirmarPartido)
    }

    fun callbackConfirmarPartido() {
        refrescarPartidos()
    }

    fun refrescarPartidos() {
        PartidoService.getPartidosDelUsuario(context!!, UsuarioLogueado.usuario, ::callBackPartidos)
    }

}


//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class PartidoViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
    BaseViewHolder<Partido, OnRecyclerItemClickListener>(itemView, listener) {

    private val empresaNombre: TextView? = itemView.empresa_nombre
    private val empresaDireccion: TextView? = itemView.empresa_direccion
    private val equipo1Nombre: TextView? = itemView.equipo1_nombre
    private val equipo2Nombre: TextView? = itemView.equipo2_nombre
    private val partidoFoto: ImageView? = itemView.partido_foto
    private val fechaDeConfirmacion: TextView? = itemView.tiempo_para_confirmar
    private val fieldFechaDeConfirmacion: TextView = itemView.field_tiempo_para_confirmar
    private val jugadores_para_confirmar: TextView = itemView.jugadores_para_confirmar
    private val botonConfirmarReserva: Button = itemView.btn_tiempo_para_confirmar
    private val check: ImageView = itemView.check
    private val fechaPartido: TextView = itemView.partido_fecha_jugar

    init {
        listener?.run {
            itemView.btn_tiempo_para_confirmar.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Partido) {
        empresaDireccion?.text = item.empresa!!.direccion
        empresaNombre?.text = item.empresa!!.nombre
        equipo1Nombre?.text = item.equipo1!!.nombre
        equipo2Nombre?.text = item.equipo2!!.nombre
        fechaPartido.text = dateTransformer(simpleDateFormatter.format(item.fechaDeReserva!!.time))
        Picasso.get().load(item.empresa!!.foto).into(partidoFoto)

        val fechaLimite = toCalendar(item.fechaDeCreacion!!)
        fechaLimite.add(Calendar.DATE, 2)


        //TODO: Pensar
        if (item.equipo1!!.owner?.idUsuario == UsuarioLogueado.usuario.idUsuario) {

            if (item.confirmado!!) {
                botonConfirmarReserva.visibility = View.GONE
                fechaDeConfirmacion?.text = ""
                fieldFechaDeConfirmacion.text = ""
                jugadores_para_confirmar.text = ""
                check.visibility = View.VISIBLE
            } else if (item.faltanJugadoresPorConfirmar()) {
                jugadores_para_confirmar.text = "${item.jugadoresRestantes()} jugadores para confirmar"
                fieldFechaDeConfirmacion.text = "Fecha limite de reserva:"
                botonConfirmarReserva.isEnabled = false
                fechaDeConfirmacion?.text =
                    dateTransformer(simpleDateFormatter.format(fechaLimite.time))
            } else {
                jugadores_para_confirmar.text = ""
                fieldFechaDeConfirmacion.text = "Fecha limite de reserva:"
                botonConfirmarReserva.isEnabled = true
                fechaDeConfirmacion?.text =
                    dateTransformer(simpleDateFormatter.format(fechaLimite.time))
            }

        } else if (item.confirmado!!) {
            fieldFechaDeConfirmacion.text = "Reserva confirmada!"
            botonConfirmarReserva.visibility = View.GONE
            fechaDeConfirmacion?.text = ""
        } else if (item.faltanJugadoresPorConfirmar()) {
            jugadores_para_confirmar.text = "${item.jugadoresRestantes()} jugadores para confirmar"
            fieldFechaDeConfirmacion.text = "Fecha limite de reserva:"
            botonConfirmarReserva.visibility = View.GONE
            fechaDeConfirmacion?.text =
                dateTransformer(simpleDateFormatter.format(fechaLimite.time))
        } else {
            jugadores_para_confirmar.text = ""
            fieldFechaDeConfirmacion.text = "\n\n\n\n\n\n\n\n\n\nDebes esperar a que el creador confirme la reserva"
            botonConfirmarReserva.visibility = View.GONE
            fechaDeConfirmacion?.text = ""
        }

    }
}

class PartidoAdapter(context: Context, listener: PartidoFragment) :
    GenericRecyclerViewAdapter<Partido, OnRecyclerItemClickListener, PartidoViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoViewHolder {
        return PartidoViewHolder(inflate(R.layout.row_fragment_partido, parent), listener)
    }
}

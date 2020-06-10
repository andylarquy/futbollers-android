package ar.edu.unsam.proyecto.futbollers.activities.home.fragments.PartidoFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.ArmarPartidoActivty
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.services.PartidoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_partido.*
import kotlinx.android.synthetic.main.row_fragment_partido.view.*


class PartidoFragment: Fragment(), OnRecyclerItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_partido, container, false)
    }

    lateinit var partidoAdapter:PartidoAdapter
    var rv = partidos_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val partidoService= PartidoService
        val usuarioLogueado = UsuarioLogueado.usuario

        rv = partidos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        partidoAdapter = context?.let { PartidoAdapter(it, this) }!!
        rv.adapter = partidoAdapter


        partidoService.getPartidosDelUsuario(context!!, usuarioLogueado, ::callBackPartidos)

        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        //Burocracia para ocultar el float button (recomiendo ocultarlo)
        val fab = floating_action_button
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scroll Down
                    if (fab.isShown) {
                        fab.hide()
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown) {
                        fab.show()
                    }
                }
            }
        })

        fab.setOnClickListener {
            val intent = Intent(activity, ArmarPartidoActivty::class.java).putExtra("activity", activity!!::class.java.simpleName).apply{}
            startActivity(intent)
        }

    }

    fun callBackPartidos(partidos: MutableList<Partido>){
        partidoAdapter.items?.clear()
        partidoAdapter.items = partidos
        partidoAdapter.notifyDataSetChanged()
        Log.i("HomeActivity", partidoAdapter.items!!.map { it.empresa!!.foto }.toString())

        //Desactivo pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE
    }

    override fun onItemClick(position: Int) {
        Log.i("PartidoFragment", "TODO: Darle comportamiento al click (no va a ser tan facil)")
        //Do Nothing yet - Espero q nunca haya que hacer algo aca jajajaja
    }

}


//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class PartidoViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<Partido, OnRecyclerItemClickListener>(itemView, listener) {

    private val empresaNombre: TextView? = itemView.empresa_nombre
    private val empresaDireccion: TextView? = itemView.empresa_direccion
    private val equipo1Nombre: TextView? = itemView.equipo1_nombre
    private val equipo2Nombre: TextView? = itemView.equipo2_nombre
    private val partidoFoto: ImageView? = itemView.partido_foto

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Partido) {
        empresaDireccion?.text = item.empresa!!.direccion
        empresaNombre?.text = item.empresa!!.nombre
        equipo1Nombre?.text = item.equipo1!!.nombre
        equipo2Nombre?.text = item.equipo2!!.nombre
        Picasso.get().load(item.empresa!!.foto).into(partidoFoto)
    }
}

class PartidoAdapter(context: Context, listener: PartidoFragment) : GenericRecyclerViewAdapter<Partido, OnRecyclerItemClickListener, PartidoViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoViewHolder {
        return PartidoViewHolder(inflate(R.layout.row_fragment_partido, parent), listener)
    }
}

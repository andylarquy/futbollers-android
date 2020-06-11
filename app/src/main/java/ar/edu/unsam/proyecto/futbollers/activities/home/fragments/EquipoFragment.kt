package ar.edu.unsam.proyecto.futbollers.activities.home.fragments.EquipoFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.ArmarPartidoActivity
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_equipo.*
import kotlinx.android.synthetic.main.row_fragment_equipo.view.*


class EquipoFragment(val fab: FloatingActionButton) : Fragment(), OnRecyclerItemClickListener, EquipoMultipleClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equipo, container, false)
    }

    lateinit var equipoAdapter: EquipoAdapter
    var rv = equipos_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val equipoService = EquipoService
        val usuarioLogueado = UsuarioLogueado.usuario

        rv = equipos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        equipoAdapter = context?.let { EquipoAdapter(it, this) }!!
        rv.adapter = equipoAdapter

        equipoService.getEquiposDelUsuario(context!!, usuarioLogueado, ::callBackEquipos)

        //Renderizo pantalla de carga
        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        //Burocracia para ocultar el float button (recomiendo ocultarlo)
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
            Toast.makeText(context,"TODO: Crear equipo",Toast.LENGTH_SHORT).show()
        }

    }


    fun callBackEquipos(equipos: MutableList<Equipo>) {
        equipoAdapter.items!!.clear()
        equipoAdapter.items!!.addAll(equipos)
        equipoAdapter.notifyDataSetChanged()
        //Render pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE
    }

    override fun onItemClick(position: Int) {
        //TODO: Creo que aca es donde se le da comportamiento SI PAPA ES ACA!!
        //Al eliminar, editar etc
        val equipoSeleccionado: Equipo = equipoAdapter.getItem(position)
    }

    override fun onEditClick(position: Int) {
        val equipoSeleccionado: Equipo = equipoAdapter.getItem(position)
        Toast.makeText(context, "TODO: Editar equipo (con id: "+equipoSeleccionado.idEquipo+")", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(position: Int) {
        val equipoSeleccionado: Equipo = equipoAdapter.getItem(position)
        Toast.makeText(context, "TODO: Eliminar equipo (con id: "+equipoSeleccionado.idEquipo+")", Toast.LENGTH_SHORT).show()
    }

    override fun onAbandonClick(position: Int) {
        val equipoSeleccionado: Equipo = equipoAdapter.getItem(position)
        Toast.makeText(context, "TODO: Abandonar equipo (con id: "+equipoSeleccionado.idEquipo+")", Toast.LENGTH_SHORT).show()
    }

}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class EquipoViewHolder(itemView: View, listener: EquipoMultipleClickListener?) : BaseViewHolder<Equipo, EquipoMultipleClickListener>(itemView, listener) {

    private val usuarioLogueado = UsuarioLogueado.usuario

    private val equipoNombre: TextView? = itemView.equipo_nombre
    private val ownerIcon: ImageView? = itemView.owner_icon
    private val abandonIcon: ImageView? = itemView.abandon_icon
    private val trashIcon: ImageView? = itemView.abandon_icon
    private val editIcon: ImageView? = itemView.edit_icon
    private val equipoFoto: ImageView? = itemView.equipo_foto

    init {
        listener?.run {
            itemView.edit_icon.setOnClickListener { onEditClick(adapterPosition) }
            itemView.trash_icon.setOnClickListener { onDeleteClick(adapterPosition) }
            itemView.abandon_icon.setOnClickListener { onAbandonClick(adapterPosition) }
        }
    }

    override fun onBind(item: Equipo) {

        if(item.esOwnerById(usuarioLogueado)){
            ownerIcon?.setImageResource(R.drawable.ic_portrait_black_24dp)

            //TODO: Darle comportamiento a los botones
            trashIcon?.setImageResource(R.drawable.ic_delete_black_24dp)
            editIcon?.setImageResource(R.drawable.ic_create_black_24dp)
        }else{
            ownerIcon?.setImageResource(0)
            trashIcon?.setImageResource(0)
            editIcon?.setImageResource(0)
        }
        abandonIcon?.setImageResource(R.drawable.ic_exit_to_app_black_24dp)
        equipoNombre?.text = item.nombre
        Picasso.get().load(item.foto).into(equipoFoto)
    }
}

class EquipoAdapter(context: Context, listener: EquipoFragment) : GenericRecyclerViewAdapter<Equipo, EquipoMultipleClickListener, EquipoViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        return EquipoViewHolder(inflate(R.layout.row_fragment_equipo, parent), listener)
    }
}

interface EquipoMultipleClickListener: BaseRecyclerListener {
    fun onEditClick(position: Int)
    fun onDeleteClick(position: Int)
    fun onAbandonClick(position: Int)
}


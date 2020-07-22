package ar.edu.unsam.proyecto.futbollers.activities.home.fragments

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
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.inicio.LoginActivity
import ar.edu.unsam.proyecto.futbollers.activities.mensajes.MensajeActivity
import ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo.usuarioLogueado
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.chatIdBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.row_contacto.view.*

class ChatFragment(val fab: FloatingActionButton) : Fragment(), OnRecyclerItemClickListener {

    lateinit var contactoAdapter: ContactoAdapter

    val usuarioService = UsuarioService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usuarioLogueado = UsuarioLogueado.usuario

        var rv = usuarios_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        contactoAdapter = context?.let { ContactoAdapter(it, this) }!!
        rv.adapter = contactoAdapter

        usuarioService.getAmigosDelUsuario(context!!, usuarioLogueado, ::callbackGetAmigos)

        // Si, yo se que esto es malisimo, pero como creimos que iba a haber floating
        // button hay que hacer refactor para cambiarlo
        fab.visibility = View.GONE

    }

    fun callbackGetAmigos(amigos: MutableList<Usuario>){
        contactoAdapter.items!!.clear()
        contactoAdapter.items!!.addAll(amigos)
        contactoAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {

        val contactoSeleccionado: Usuario = contactoAdapter.getItem(position)
        val idChat = chatIdBuilder(usuarioLogueado.idUsuario!!, contactoSeleccionado.idUsuario!!)

        val intent = Intent(context, MensajeActivity::class.java).apply{}
        intent.putExtra("nombreContacto", contactoSeleccionado.nombre)
        intent.putExtra("fotoContacto", contactoSeleccionado.foto)
        intent.putExtra("idContacto", contactoSeleccionado.idUsuario!!)
        intent.putExtra("idChat", idChat)
        startActivity(intent)
    }


}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class ContactoViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<Usuario, OnRecyclerItemClickListener>(itemView, listener) {
    private val contactoNombre: TextView? = itemView.nombre_contacto
    private val contactoFoto: ImageView? = itemView.contacto_foto

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Usuario) {
        contactoNombre?.text = item.nombre
        Picasso.get().load(item.foto).into(contactoFoto)
    }

}

class ContactoAdapter(context: Context, listener: ChatFragment) : GenericRecyclerViewAdapter<Usuario, OnRecyclerItemClickListener, ContactoViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        return ContactoViewHolder(inflate(R.layout.row_contacto, parent), listener)
    }
}


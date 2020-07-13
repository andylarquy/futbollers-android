package ar.edu.unsam.proyecto.futbollers.activities.mensajes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo.usuarioLogueado
import ar.edu.unsam.proyecto.futbollers.activities.periferico.EncuestasContext
import ar.edu.unsam.proyecto.futbollers.domain.*
import ar.edu.unsam.proyecto.futbollers.services.NotificacionService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.google.firebase.database.*
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.activity_mensaje.*
import kotlinx.android.synthetic.main.row_mensaje.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

lateinit var globalContext: Context

class MensajeActivity : AppCompatActivity(), OnRecyclerItemClickListener {

    lateinit var mensajesAdapter: MensajesAdapter

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null

    var notificacionService = NotificacionService

    var idContacto: Long? = null
    var idChat: String? = null

    lateinit var fotoContacto: String
    lateinit var nombreContacto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje)

        if (savedInstanceState == null) {
            val extras = intent.extras

            if (extras !== null) {
                idContacto = extras.getLong("idContacto")
                nombreContacto = extras.getString("nombreContacto")!!
                idChat = extras.getString("idChat")!!
                fotoContacto = extras.getString("fotoContacto")!!
                databaseReference = database.getReference(idChat!!)
            }
        }

        globalContext = this

        btnEnviar.setTextColor(Color.WHITE)
        Picasso.get().load(fotoContacto).into(foto_contacto)

        var rv = mensajes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        mensajesAdapter = MensajesAdapter(this, this)
        rv.adapter = mensajesAdapter

        nombre.setText(nombreContacto)

        arrow_back.setOnClickListener{
            this.finish()
        }

        btnEnviar.setOnClickListener {
            if(txtMensaje.text.toString().isNotBlank()) {
                databaseReference!!.push().setValue(
                    MensajeEnviar(
                        txtMensaje.text.toString(),
                        "1",
                        ServerValue.TIMESTAMP,
                        usuarioLogueado.idUsuario

                    )
                )
                enviarNotificacionDeMensaje(txtMensaje.text.toString())
                txtMensaje.setText("")
            }
        }

        mensajesAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                rv.scrollToPosition(mensajesAdapter.itemCount - 1);
            }
        })

        databaseReference!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val m: MensajeRecibir? = dataSnapshot.getValue(MensajeRecibir::class.java)
                if (m != null) {
                    mensajesAdapter.add(m)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}

        })

    }

    fun enviarNotificacionDeMensaje(mensaje: String){
        val notificacion = Notificacion()
        notificacion.titulo = usuarioLogueado.nombre+" te ha enviado un mensaje!"
        notificacion.descripcion = mensaje

        val receptor = Usuario()
        receptor.idUsuario = idContacto

        notificacion.usuarioReceptor = receptor
        notificacionService.enviarNotificacionMensaje(this, notificacion)
    }

    override fun onItemClick(position: Int) {}
}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class MensajesViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
    BaseViewHolder<Mensaje, OnRecyclerItemClickListener>(itemView, listener) {

    val usuarioService = UsuarioService

    private val horaMensajeEmisor: TextView? = itemView.horaMensajeEmisor
    private val mensajeEmisor: TextView? = itemView.mensajeEmisor
    private val nombreMensajeEmisor: TextView? = itemView.nombreMensajeEmisor
    private val horaMensajeReceptor: TextView? = itemView.horaMensajeReceptor
    private val mensajeReceptor: TextView? = itemView.mensajeReceptor
    private val nombreMensajeReceptor: TextView? = itemView.nombreMensajeReceptor
    private val contactoFoto: ImageView? = itemView.fotoMensaje
    private val constraintLayout  = itemView.constrain_layout



    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Mensaje) {



        val idContacto = item.idUsuario!!.toLong()

        if(usuarioLogueado.tieneId(idContacto)){
            //Mensaje de salida (TODO: Delega ese if en una funcion papi, no se entiende nada)

            horaMensajeEmisor?.visibility = View.VISIBLE
            mensajeEmisor?.visibility = View.VISIBLE
            nombreMensajeEmisor?.visibility = View.VISIBLE

            val horaAsDate = Date(item.hora as Long)
            horaMensajeEmisor?.text = SimpleDateFormat("HH:mm ", Locale.getDefault()).format(horaAsDate)
            mensajeEmisor?.text = item.mensaje

            mensajeEmisor?.setBackgroundResource(R.drawable.outcoming_bubble)

            usuarioService.getUsuarioContactoById(
                globalContext,
                idContacto,
                nombreMensajeEmisor,
                contactoFoto,
                ::callbackPerfilUsuario
            )

        }else{
            //Mensaje de entrada

            horaMensajeReceptor?.visibility = View.VISIBLE
            mensajeReceptor?.visibility = View.VISIBLE
            nombreMensajeReceptor?.visibility = View.VISIBLE

            val horaAsDate = Date(item.hora as Long)
            horaMensajeReceptor?.text = SimpleDateFormat("HH:mm ", Locale.getDefault()).format(horaAsDate)
            mensajeReceptor?.text = item.mensaje

            mensajeReceptor?.setBackgroundResource(R.drawable.incoming_bubble)

            usuarioService.getUsuarioContactoById(
                globalContext,
                idContacto,
                nombreMensajeReceptor,
                contactoFoto,
                ::callbackPerfilUsuario
            )


        }





        //  jugadoNombre.text = item.usuarioReferenciado!!.nombre
        //   Picasso.get().load(item.usuarioReferenciado.foto).into(jugadorFoto)
        //  Picasso.get().load(item.foto).into(fotoUsuario)
        // Picasso.get().load("https://imgur.com/DFcX6vX").into(jugadorFoto);


    }

}

fun callbackPerfilUsuario(contacto: Usuario, nombreMensaje: TextView?, contactoFoto: ImageView?) {
    Log.i("MensajeActivity", contacto.foto)
    nombreMensaje?.text = contacto.nombre
    Picasso.get().load(contacto.foto).into(contactoFoto)
}


class MensajesAdapter(context: Context, listener: MensajeActivity) :
    GenericRecyclerViewAdapter<Mensaje, OnRecyclerItemClickListener, MensajesViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajesViewHolder {
        return MensajesViewHolder(inflate(R.layout.row_mensaje, parent), listener)
    }


}






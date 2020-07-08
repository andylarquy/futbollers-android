package ar.edu.unsam.proyecto.futbollers.activities.mensajes

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo.usuarioLogueado
import ar.edu.unsam.proyecto.futbollers.activities.periferico.EncuestasContext
import ar.edu.unsam.proyecto.futbollers.domain.*
import com.google.firebase.database.*
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.activity_mensaje.*
import kotlinx.android.synthetic.main.row_agregar_amigo.view.*
import kotlinx.android.synthetic.main.row_encuesta.view.*
import kotlinx.android.synthetic.main.row_mensaje.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MensajeActivity : AppCompatActivity(), OnRecyclerItemClickListener {

    lateinit var mensajesAdapter: MensajesAdapter

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null

    var idContacto: Long? = null
    var idChat: String? = null
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
                databaseReference = database.getReference(idChat!!)
            }
        }

        var rv = mensajes_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        mensajesAdapter = MensajesAdapter(this, this)
        rv.adapter = mensajesAdapter

        nombre.setText(nombreContacto)

        btnEnviar.setOnClickListener {
            databaseReference!!.push().setValue(
                MensajeEnviar(
                    txtMensaje.text.toString(),
                   "1",
                    ServerValue.TIMESTAMP,
                    usuarioLogueado.idUsuario

                )
            )
            txtMensaje.setText("")
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

            override fun onChildChanged(dataSnapshot: DataSnapshot,s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot,s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}

        })

}

    override fun onItemClick(position: Int) {}
}

//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class MensajesViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
    BaseViewHolder<Mensaje, OnRecyclerItemClickListener>(itemView, listener) {
    private val horaMensaje: TextView? = itemView.horaMensaje
    private val mensaje: TextView? = itemView.mensajeMensaje
    private val jugadorFoto : ImageView? = itemView.foto_perfil
    private val fotoUsuario : ImageView? = itemView.foto_perfil


    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onBind(item: Mensaje) {

        val horaAsDate = Date(item.hora as Long)
        horaMensaje?.text = SimpleDateFormat("HH:mm ", Locale.getDefault()).format(horaAsDate)
        mensaje?.text = item.mensaje

       //TODO: Bindear foto de perfil NO ANDA UN JORACA ESTO LRPM!
        usuarioLogueado.foto = item.foto
       Picasso.get().load(item.foto).into(jugadorFoto)

      //  jugadoNombre.text = item.usuarioReferenciado!!.nombre
     //   Picasso.get().load(item.usuarioReferenciado.foto).into(jugadorFoto)
      //  Picasso.get().load(item.foto).into(fotoUsuario)
       // Picasso.get().load("https://imgur.com/DFcX6vX").into(jugadorFoto);

    }

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






package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Encuesta
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.EncuestaService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.customListAdapter
import com.bumptech.glide.Glide
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_encuesta.*
import kotlinx.android.synthetic.main.row_encuesta.view.*


class EncuestasActivity : AppCompatActivity(), ContestarEncuestaClickListener {

    lateinit var dialogContestarEncuesta: MaterialDialog
    lateinit var rv: RecyclerView
    lateinit var encuestasAdapter: EncuestasAdapter

    val encuestaService = EncuestaService
    val usuarioLogueado = UsuarioLogueado.usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encuesta)

        setupDialogContestarEncuesta()

        rv = encuestas_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        encuestasAdapter = EncuestasAdapter(this, this)
        rv.adapter = encuestasAdapter

        encuestaService.getEncuestasDelUsuario(this, usuarioLogueado, ::callbackEncuesta)

    }

    fun callbackEncuesta(encuestas: MutableList<Encuesta>){
        encuestasAdapter.clear()
        encuestasAdapter.items = encuestas
        encuestasAdapter.notifyDataSetChanged()
    }

    fun setupDialogContestarEncuesta() {
        //Preparo el dialog de amigos
        dialogContestarEncuesta = MaterialDialog(this)
            .title(text = "Contestar Encuesta")
            .customView(R.layout.dialog_formulario_encuesta)
            .noAutoDismiss()
            .positiveButton(text = "Aceptar") {
                it.dismiss()
            }
            .negativeButton(text = "Cancelar") {
                it.dismiss()
            }
            .onDismiss{
                //TODO: Algo?
            }

    }

    override fun onContestarEncuestaClick(position: Int) {
        dialogContestarEncuesta.show()
    }

}

class EncuestasAdapter(context: Context, listener: ContestarEncuestaClickListener) :
    GenericRecyclerViewAdapter<Encuesta, ContestarEncuestaClickListener, EncuestaViewHolder>(
        context, listener
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        return EncuestaViewHolder(
            inflate(R.layout.row_encuesta, parent),
            listener
        )
    }
}

class EncuestaViewHolder(itemView: View, listener: ContestarEncuestaClickListener) :
    BaseViewHolder<Encuesta, ContestarEncuestaClickListener>(itemView, listener) {

    private val jugadoNombre: TextView = itemView.jugador_nombre
    private val jugadorFoto: ImageView = itemView.jugador_foto
    private val contestarEncuestaButton: Button = itemView.btn_contestar_encuesta

    init {
        listener.run {
            contestarEncuestaButton.setOnClickListener { onContestarEncuestaClick(adapterPosition)
            }
        }
    }

    override fun onBind(item: Encuesta) {
       //TODO Bindear sarasa
        jugadoNombre.text = item.usuarioReferenciado!!.nombre
        Picasso.get().load(item.usuarioReferenciado.foto).into(jugadorFoto)
    }

}

interface ContestarEncuestaClickListener : BaseRecyclerListener {
    fun onContestarEncuestaClick(position: Int)
}


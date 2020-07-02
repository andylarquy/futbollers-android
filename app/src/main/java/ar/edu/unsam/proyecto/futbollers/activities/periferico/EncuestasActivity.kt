package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Encuesta
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.EncuestaService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_encuesta.*
import kotlinx.android.synthetic.main.dialog_formulario_encuesta.view.*
import kotlinx.android.synthetic.main.row_encuesta.view.*


class EncuestasActivity : AppCompatActivity(), ContestarEncuestaClickListener {

    lateinit var dialogContestarEncuesta: MaterialDialog
    lateinit var rv: RecyclerView
    lateinit var encuestasAdapter: EncuestasAdapter

    val encuestaService = EncuestaService
    val usuarioLogueado = UsuarioLogueado.usuario
    val encuestaSeleccionada: Encuesta? = EncuestasContext.encuestaSeleccionada

    var respuesta1: Boolean = false
    var respuesta2: Boolean = false
    var respuesta3: Boolean = true

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

    fun callbackUpdateEncuesta(){
        Toasty.success(this, "Estoy impactada!!",Toast.LENGTH_SHORT).show()
    }

    override fun onContestarEncuestaClick(position: Int) {
        dialogContestarEncuesta.show()
    }

    fun setupDialogContestarEncuesta() {
        //Preparo el dialog de amigos
        dialogContestarEncuesta = MaterialDialog(this)
            .title(text = "Contestar Encuesta")
            .customView(R.layout.dialog_formulario_encuesta)
            .noAutoDismiss()
            .positiveButton(text = "Aceptar") {

                encuestaSeleccionada?.respuesta1 = respuesta1
                encuestaSeleccionada?.respuesta2 = respuesta2
                encuestaSeleccionada?.respuesta3 = respuesta3

                encuestaService.updateEncuesta(this, encuestaSeleccionada!!, ::callbackUpdateEncuesta)
                //TODO: Toast de que todo piola

                it.dismiss()
            }
            .negativeButton(text = "Cancelar") {
                respuesta1 = false
                respuesta2 = false
                respuesta3 = true

                Toasty.error(this, "You only had to follow the damn train", Toast.LENGTH_SHORT).show()

                it.dismiss()
            }
            .onDismiss{
                //TODO: Algo?
            }

        val customView = dialogContestarEncuesta.getCustomView()


        val radioGroup = customView.first
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                2131231233 -> {
                    respuesta1 = true
                }

                2131231230 -> {
                    respuesta1 = false
                }
            }

            Log.i("EncuestasActivity", respuesta1.toString())
        }


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
        jugadoNombre.text = item.usuarioReferenciado!!.nombre
        Picasso.get().load(item.usuarioReferenciado.foto).into(jugadorFoto)
        EncuestasContext.encuestaSeleccionada = item
    }

}

interface ContestarEncuestaClickListener : BaseRecyclerListener {
    fun onContestarEncuestaClick(position: Int)
}

object EncuestasContext{
    var encuestaSeleccionada: Encuesta? = null
}


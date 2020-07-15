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
import ar.edu.unsam.proyecto.futbollers.activities.drawer.SetupDrawer
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
import kotlinx.android.synthetic.main.activity_encuesta.base_drawer_layout
import kotlinx.android.synthetic.main.activity_encuesta.base_toolbar
import kotlinx.android.synthetic.main.activity_encuesta.nav_drawer
import kotlinx.android.synthetic.main.activity_invitaciones.*
import kotlinx.android.synthetic.main.dialog_formulario_encuesta.view.*
import kotlinx.android.synthetic.main.row_encuesta.view.*
import kotlin.properties.Delegates


class EncuestasActivity : AppCompatActivity(), ContestarEncuestaClickListener {

    lateinit var dialogContestarEncuesta: MaterialDialog
    lateinit var rv: RecyclerView
    lateinit var encuestasAdapter: EncuestasAdapter

    val encuestaService = EncuestaService
    val usuarioLogueado = UsuarioLogueado.usuario
    val encuestaSeleccionada: Encuesta = EncuestasContext.encuestaSeleccionada
    var idEncuestaSeleccionada: Long? = null

    var respuesta1: Boolean? = null
    var respuesta2: Boolean? = null
    var respuesta3: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encuesta)

        setupDialogContestarEncuesta()

        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(this, this, toolbar, drawerLayout, nav_drawer)

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
        //Update de la lista de encuestas
        encuestaService.getEncuestasDelUsuario(this, usuarioLogueado, ::callbackEncuesta)
        Toasty.success(this, "Gracias! contestaste la encuesta satisfactoriamente!",Toast.LENGTH_SHORT).show()
    }

    override fun onContestarEncuestaClick(position: Int, idEncuesta: Long) {
        idEncuestaSeleccionada = idEncuesta
        dialogContestarEncuesta.show()
    }

    //TODO: El nombre es feo, resetea los campos del viewModel
    fun resetearEncuesta(){
        idEncuestaSeleccionada = null
        respuesta1 = null
        respuesta2 = null
        respuesta3 = null
    }

    fun setupDialogContestarEncuesta() {
        //Preparo el dialog de amigos
        dialogContestarEncuesta = MaterialDialog(this)
            .title(text = "Contestar Encuesta")
            .customView(R.layout.dialog_formulario_encuesta)
            .noAutoDismiss()
            .positiveButton(text = "Aceptar") {
                if(respuestasSonValidas()) {
                    encuestaSeleccionada.idEncuesta = idEncuestaSeleccionada
                    encuestaSeleccionada.respuesta1 = respuesta1!!
                    encuestaSeleccionada.respuesta2 = respuesta2!!
                    encuestaSeleccionada.respuesta3 = respuesta3!!

                    encuestaService.updateEncuesta(this, encuestaSeleccionada, ::callbackUpdateEncuesta)

                    resetearEncuesta()

                    it.dismiss()
                }else{
                    Toasty.error(this, "Debe contestar todas las preguntas de la encuesta", Toast.LENGTH_SHORT).show()

                }
            }
            .negativeButton(text = "Cancelar") {
                resetearEncuesta()
                it.dismiss()
            }
            .onDismiss{
                val customView = dialogContestarEncuesta.getCustomView()
                customView.pregunta1RadioGroup.clearCheck()
                customView.pregunta2RadioGroup.clearCheck()
                customView.pregunta3RadioGroup.clearCheck()
            }

        val customView = dialogContestarEncuesta.getCustomView()

        //NO HAY REPETICION DE CODIGO EN BA SING SE
        customView.pregunta1RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_btn_si_1 -> {
                    respuesta1 = true
                }

                R.id.radio_btn_no_1 -> {
                    respuesta1 = false
                }
            }

            Log.i("EncuestasActivity", "Respuesta 1: $respuesta1")
        }

        customView.pregunta2RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_btn_si_2 -> {
                    respuesta2 = true
                }

                R.id.radio_btn_no_2 -> {
                    respuesta2 = false
                }
            }

            Log.i("EncuestasActivity", "Respuesta 2: $respuesta2")
        }

        customView.pregunta3RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_btn_si_3 -> {
                    respuesta3 = true
                }

                R.id.radio_btn_no_3 -> {
                    respuesta3 = false
                }
            }

            Log.i("EncuestasActivity", "Respuesta 3: $respuesta3")
        }

    }


    fun respuestasSonValidas(): Boolean{
        return respuesta1 !== null &&
               respuesta2 !== null &&
               respuesta3 !== null
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
    var idEncuesta by Delegates.notNull<Long>() //kotlin wtf

    init {
        listener.run {
            contestarEncuestaButton.setOnClickListener { onContestarEncuestaClick(adapterPosition, idEncuesta)
            }
        }
    }

    override fun onBind(item: Encuesta) {
        jugadoNombre.text = item.usuarioReferenciado!!.nombre
        Picasso.get().load(item.usuarioReferenciado.foto).into(jugadorFoto)
        EncuestasContext.encuestaSeleccionada = item
        idEncuesta = item.idEncuesta!!
    }

}

interface ContestarEncuestaClickListener : BaseRecyclerListener {
    fun onContestarEncuestaClick(position: Int, idEncuesta: Long)
}

object EncuestasContext{
    var encuestaSeleccionada: Encuesta = Encuesta()
}


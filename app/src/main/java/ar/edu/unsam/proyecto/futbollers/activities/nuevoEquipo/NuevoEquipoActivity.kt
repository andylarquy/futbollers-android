package ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.ElegirCanchaFragment
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_nuevo_equipo.*
import kotlinx.android.synthetic.main.row_elegir_jugador_para_equipo.view.*

val usuarioService = UsuarioService
val usuarioLogueado = UsuarioLogueado.usuario

class NuevoEquipoActivity : AppCompatActivity(), IntegranteListener {

    var nombreSeleccionado: String = ""

    lateinit var integrantesAdapter: IntegrantesDeEquipoAdapter
    lateinit var rv: RecyclerView
    lateinit var dialogAgregarAmigo: MaterialDialog
    val integrantes: MutableList<Usuario> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_equipo)

        rv = integrantes_de_equipo_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        integrantesAdapter = IntegrantesDeEquipoAdapter(this, this)

        rv.adapter = integrantesAdapter
        usuarioService.getAmigosDelUsuario(this, usuarioLogueado, ::callbackAmigosDelUsuario)

        setupDialogAgregarAmigo()

        input_nombre_equipo.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nombreSeleccionado = nuevoTexto.toString()
            }
        })

        icono_foto_equipo.setOnClickListener{
            uploadImage()
        }

        foto_equipo.setOnClickListener{
            uploadImage()
        }

        btn_agregar_integrante.setOnClickListener{

        }

    }

    fun setupDialogAgregarAmigo() {
        //Preparo el dialog de amigos
        dialogAgregarAmigo = MaterialDialog(this)
            .title(text = "Agrega un integrante")
            //TODO: Implementar adapter de integrantes
            //.customListAdapter(agregarIntegranteAdapter)
            .noAutoDismiss()
            .negativeButton(text = "Cerrar") {
                it.dismiss()
            }

    }

    fun callbackAmigosDelUsuario(amigos: MutableList<Usuario>){
        //FIXME: Crear una variable temporal, los integrantes no venian del back
        integrantesAdapter.clear()
        integrantesAdapter.addAll(amigos)
        integrantesAdapter.notifyDataSetChanged()
    }

    fun uploadImage(){
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .toolbarArrowColor(Color.BLACK)
            .single()
            .limit(1)
            .showCamera(true)
            .imageDirectory("Camera")
            .enableLog(false)
            .start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
             val image:Image = ImagePicker.getFirstImageOrNull(data)
            if(data === null){
                Toasty.error(this,"Mira, no se que hiciste, pero no me vas a hackear. La imagen no puede ser null.", Toast.LENGTH_SHORT).show()
            }else{
                    Glide.with(this)
                    .load(image.path)
                    .override(200,200)
                    .into(foto_equipo)
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    override fun onDeleteClick(position: Int) {
        val integrante = integrantesAdapter.getItem(position)
        integrantes.remove(integrante)
        integrantesAdapter.notifyDataSetChanged()
    }


}

class IntegrantesDeEquipoAdapter(context: Context, listener: NuevoEquipoActivity) :
    GenericRecyclerViewAdapter<Usuario, IntegranteListener, IntegrantesDeEquipoViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegrantesDeEquipoViewHolder {
        return IntegrantesDeEquipoViewHolder(inflate(R.layout.row_elegir_jugador_para_equipo, parent), listener)
    }
}

class IntegrantesDeEquipoViewHolder(itemView: View, listener: IntegranteListener?) :
    BaseViewHolder<Usuario, IntegranteListener>(itemView, listener) {

    private val amigoFoto: ImageView = itemView.amigo_foto
    private val amigoNombre: TextView = itemView.amigo_nombre
    private val posicionAmigo: TextView = itemView.posicion_amigo

    init {
        listener?.run {
            itemView.trash_icon.setOnClickListener { onDeleteClick(adapterPosition) }
        }
    }

    override fun onBind(item: Usuario) {
        amigoNombre.text = item.nombre
        posicionAmigo.text = item.posicion
        Picasso.get().load(item.foto).into(amigoFoto)
    }

}

interface IntegranteListener : BaseRecyclerListener {

    fun onDeleteClick(position: Int)

}


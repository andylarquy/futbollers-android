package ar.edu.unsam.proyecto.futbollers.activities.nuevoEquipo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.AuxiliarService
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
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

class NuevoEquipoActivity : AppCompatActivity(), OnRecyclerItemClickListener, IntegranteListener {

    var nombreSeleccionado: String = ""

    lateinit var integrantesAdapter: IntegrantesDeEquipoAdapter
    lateinit var amigosAdapter: AmigosAdapter
    lateinit var rv: RecyclerView
    lateinit var dialogAgregarAmigo: MaterialDialog

    var imagenSeleccionada: Bitmap? = null
    var urlImagenSeleccionada: String? = null
    var idEquipoAEditar: Long? = null
    var equipoAEditar: Equipo? = null

    val equipoService = EquipoService
    val auxiliarService = AuxiliarService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_equipo)

        rv = integrantes_de_equipo_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        integrantesAdapter = IntegrantesDeEquipoAdapter(this, this)
        amigosAdapter = AmigosAdapter(this, this)

        rv.adapter = integrantesAdapter
        integrantesAdapter.add(usuarioLogueado)
        integrantesAdapter.notifyDataSetChanged()

        //Descomentar dspues
        usuarioService.getAmigosDelUsuario(this, usuarioLogueado, ::callbackAmigosDelUsuario)

        setupDialogAgregarAmigo()

        val b = intent.extras

        Log.i("HomeActivity", "asdasd")

        if(b != null) {
            idEquipoAEditar = b.getLong("idEquipo")
            Log.i("HomeActivity", "DALE")
            Log.i("HomeActivity", idEquipoAEditar.toString())
            equipoService.getEquipoById(this, idEquipoAEditar!!, ::callbackGetEquipoAEditar)
        }

        input_nombre_equipo.addTextChangedListener(object : TextWatcher {
            //Estos metodos tienen que estar implementados, burocracia
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(nuevoTexto: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nombreSeleccionado = nuevoTexto.toString()
            }
        })

        icono_foto_equipo.setOnClickListener {
            uploadImage()
        }

        foto_equipo.setOnClickListener {
            uploadImage()
        }

        btn_agregar_integrante.setOnClickListener {
            dialogAgregarAmigo.show()
        }

        btn_confirmar.setOnClickListener {
            loading_spinner.visibility = View.VISIBLE
            confirmarEquipo()
        }

        btn_cancelar.setOnClickListener{
            finish()
        }

    }

    fun confirmarEquipo() {

        var status = true

        if(integrantesAdapter.items.size < 1){
            Toasty.warning(this, "Advertencia: Has creado un equipo sin integrantes", Toast.LENGTH_SHORT).show();
        }

        if (nombreSeleccionado == "") {
            Toasty.error(this, "El nombre del equipo no puede estar vacío", Toast.LENGTH_SHORT).show()
            status = false
        }

        if (nombreSeleccionado.toLowerCase().contains("equipo temporal")) {
            Toasty.error(this, "El nombre del equipo no puede contener \"Equipo Temporal \"", Toast.LENGTH_SHORT).show()
            status = false
        }

        if (imagenSeleccionada === null){
            Toasty.error(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show()
            status = false
        }

        if(status){
            uploadImageToServer(imagenSeleccionada!!)
        }else{
            loading_spinner.visibility = View.INVISIBLE
        }

    }

    fun uploadImage() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .toolbarArrowColor(Color.BLACK)
            .single()
            .limit(1)
            .showCamera(false)
            .imageDirectory("Camera")
            .enableLog(false)
            .start()
    }

    fun uploadImageToServer(imagen: Bitmap){
        auxiliarService.uploadImage(this, imagen, ::callbackUploadImage)
    }

    fun callbackUploadImage(url: String){
        urlImagenSeleccionada = url
       //La imagen fue subida al image server

        val nuevoEquipo = Equipo()
        nuevoEquipo.integrantes = integrantesAdapter.items
        nuevoEquipo.nombre = nombreSeleccionado
        nuevoEquipo.foto = urlImagenSeleccionada
        nuevoEquipo.owner = usuarioLogueado

        if(esUnaEdicion()){
            nuevoEquipo.idEquipo = idEquipoAEditar
            equipoService.updateEquipo(this, nuevoEquipo, ::callbackUpdateEquipo, ::callbackErrorUpdateEquipo)
        }else{
            equipoService.postEquipo(this, nuevoEquipo, ::callbackPostEquipo, ::callbackErrorPostEquipo)
        }
    }

    fun callbackGetEquipoAEditar(equipo: Equipo){

        equipoAEditar = equipo

        urlImagenSeleccionada = equipo.foto
        nombreSeleccionado = equipo.nombre!!

        //Bindeo contra la interfaz
        input_nombre_equipo.setText(nombreSeleccionado)
        Picasso.get().load(urlImagenSeleccionada).fit().centerInside().into(foto_equipo)

        Handler().postDelayed({
            imagenSeleccionada = foto_equipo.drawable.toBitmap()
        },400)


        integrantesAdapter.items = equipo.integrantes
        integrantesAdapter.notifyDataSetChanged()

        usuarioService.getAmigosDelUsuario(this, usuarioLogueado, ::callbackAmigosDelUsuario)
    }


    //NO HAY REPETICION DE CODIGO EN BA SING SE
    fun callbackPostEquipo(){
        Toasty.success(this, "¡El equipo ha sido creado correctamente!",Toast.LENGTH_SHORT).show()
        loading_spinner.visibility = View.INVISIBLE
        finish()
    }

    fun callbackUpdateEquipo(){
        Toasty.success(this, "¡El equipo ha sido editado correctamente!",Toast.LENGTH_SHORT).show()
        loading_spinner.visibility = View.INVISIBLE
        finish()
    }

    fun callbackErrorPostEquipo(errorMessage: String){
        Toasty.error(this, errorMessage,Toast.LENGTH_SHORT).show()
        loading_spinner.visibility = View.INVISIBLE
    }

    fun callbackErrorUpdateEquipo(errorMessage: String){
        Toasty.error(this, errorMessage,Toast.LENGTH_SHORT).show()
        loading_spinner.visibility = View.INVISIBLE
    }

    fun callbackAmigosDelUsuario(amigos: MutableList<Usuario>) {
        amigosAdapter.clear()
        amigosAdapter.addAll(amigos)
        amigosAdapter.notifyDataSetChanged()

        if(esUnaEdicion()){
            //Remueve de amigosAdapter los integrantes que coincidan ID con equipoAEditar
            amigosAdapter.items.removeAll{ integrante ->
                equipoAEditar!!.integrantes!!.any{it.tieneId(integrante.idUsuario!!)}
            }
            amigosAdapter.notifyDataSetChanged()
        }

    }

    fun esUnaEdicion(): Boolean{
        return idEquipoAEditar !== null && equipoAEditar !== null
    }

    fun setupDialogAgregarAmigo() {
        //Preparo el dialog de amigos
        dialogAgregarAmigo = MaterialDialog(this)
            .title(text = "Agrega un integrante")
            .customListAdapter(amigosAdapter)
            .noAutoDismiss()
            .negativeButton(text = "Cerrar") {
                it.dismiss()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image = ImagePicker.getFirstImageOrNull(data)
            if (data === null) {
                Toasty.error(this, "Mira, no se que hiciste, pero no me vas a hackear. La imagen no puede ser null.", Toast.LENGTH_SHORT).show()
            } else {

                val bmImg: Bitmap = BitmapFactory.decodeFile(Uri.parse(image.path).toString())

                val drawableImage: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bmImg, 200, 200, true))

                foto_equipo.setImageDrawable(drawableImage)
                imagenSeleccionada = bmImg

                //Glide es async, asi que lo banco un toque
                Handler().postDelayed({
                    Log.i("NuevoEquipoActivity",foto_equipo.toString())
                    if(foto_equipo.drawable === null){
                        Toasty.error(this,"Ha habido un error al procesar la imagen [DEBUG] Path: "+image.path+"]", Toast.LENGTH_SHORT).show()
                    }else{
                        imagenSeleccionada = foto_equipo.drawable.toBitmap()
                    }
                }, 200)

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDeleteClick(position: Int) {
        val integrante = integrantesAdapter.getItem(position)
        integrantesAdapter.items.remove(integrante)
        integrantesAdapter.notifyDataSetChanged()

        amigosAdapter.items.add(integrante)
        amigosAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        val amigo = amigosAdapter.getItem(position)
        amigosAdapter.items.remove(amigo)
        amigosAdapter.notifyDataSetChanged()

        integrantesAdapter.items.add(amigo)
        integrantesAdapter.notifyDataSetChanged()
    }


}

/////////////////////////////////////////////////////////////////////////////////////////////////////

class IntegrantesDeEquipoAdapter(context: Context, listener: NuevoEquipoActivity) :
    GenericRecyclerViewAdapter<Usuario, IntegranteListener, IntegrantesDeEquipoViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntegrantesDeEquipoViewHolder {
        return IntegrantesDeEquipoViewHolder(
            inflate(R.layout.row_elegir_jugador_para_equipo, parent), listener)
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

/////////////////////////////////////////////////////////////////////////////////////////////////////

class AmigosAdapter(context: Context, listener: NuevoEquipoActivity) :
    GenericRecyclerViewAdapter<Usuario, OnRecyclerItemClickListener, AmigosAdapter.AmigosViewHolder>(
        context,
        listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigosViewHolder {
        return AmigosViewHolder(inflate(R.layout.row_elegir_amigo, parent), listener)
    }

    class AmigosViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) :
        BaseViewHolder<Usuario, OnRecyclerItemClickListener>(itemView, listener) {

        private val amigoFoto: ImageView = itemView.amigo_foto
        private val amigoNombre: TextView = itemView.amigo_nombre
        private val posicionAmigo: TextView = itemView.posicion_amigo

        init {
            listener?.run {
                itemView.setOnClickListener { onItemClick(adapterPosition) }
            }
        }

        override fun onBind(item: Usuario) {
            amigoNombre.text = item.nombre

            posicionAmigo.text = item.posicion
            Picasso.get().load(item.foto).into(amigoFoto)
        }

    }

}


package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.drawer.SetupDrawer
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.customListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_amigos.*
import kotlinx.android.synthetic.main.row_agregar_amigo.view.*
import kotlinx.android.synthetic.main.row_amigo.view.*


class AmigosActivity : AppCompatActivity(), AgregarAmigoClickListener {

    lateinit var dialogAgregarAmigo: MaterialDialog
    lateinit var agregarAmigoAdapter: AgregarAmigoAdapter
    lateinit var listaAmigosAdapter: ListaAmigosAdapter
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    val usuarioService = UsuarioService
    lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)

        rv = amigos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        agregarAmigoAdapter = AgregarAmigoAdapter(this, this)
        listaAmigosAdapter = ListaAmigosAdapter(this, this)
        rv.adapter = listaAmigosAdapter
        setupDialogAgregarAmigo()

        refrescarListaAmigos()
        refrescarListaCandidatos()


        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(this, this, toolbar, drawerLayout, nav_drawer)

        val floatButton: FloatingActionButton =
            floating_action_button.findViewById(R.id.floating_action_button)

        floatButton.setOnClickListener {
            dialogAgregarAmigo.show()
        }

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scroll Down
                    if (floatButton.isShown) {
                        floatButton.hide()
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!floatButton.isShown) {
                        floatButton.show()
                    }
                }
            }
        })


    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java).apply{}
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
        this.finish()
    }

    fun setupDialogAgregarAmigo() {
        //Preparo el dialog de amigos
        dialogAgregarAmigo = MaterialDialog(this)
            .title(text = "Agrega un amigo")
            .customListAdapter(agregarAmigoAdapter)
            .noAutoDismiss()
            .negativeButton(text = "Cerrar") {
                it.dismiss()
            }
            .onDismiss{
                refrescarListaAmigos()
                refrescarListaCandidatos()
            }

    }


    fun modalCandidatosCallback(candidatos: MutableList<Usuario>) {
        agregarAmigoAdapter.clear()
        agregarAmigoAdapter.items = candidatos
        agregarAmigoAdapter.notifyDataSetChanged()
    }


    override fun onAgregarAmigoClick(position: Int, button: Button, check: ImageView) {
        val amigo = agregarAmigoAdapter.getItem(position)
        button.isClickable = false
        button.visibility = View.GONE

        usuarioService.postAmistad(this, amigo, ::callbackAgregarAmigo, check)
    }

    fun callbackAgregarAmigo(check: ImageView) {
        check.visibility = View.VISIBLE
        Toasty.success(
            this,
            "¡Se ha agregado con éxito el usuario a tu lista de amigos!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun refrescarListaAmigos(){
        usuarioService.getAmigosDelUsuario(this, usuarioLogueado, ::callbackAmigosDelUsuario)
    }

    fun refrescarListaCandidatos(){
        setupDialogAgregarAmigo()
        usuarioService.getCandidatosDelUsuario(this, usuarioLogueado, ::modalCandidatosCallback)
    }

    fun callbackAmigosDelUsuario(amigos: MutableList<Usuario>){
        listaAmigosAdapter.clear()
        listaAmigosAdapter.items = amigos
        listaAmigosAdapter.notifyDataSetChanged()
    }

    class AgregarAmigoAdapter(context: Context, listener: AgregarAmigoClickListener) :
        GenericRecyclerViewAdapter<Usuario, AgregarAmigoClickListener, AgregarAmigoViewHolder>(
            context, listener
        ) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgregarAmigoViewHolder {
            return AgregarAmigoViewHolder(
                inflate(R.layout.row_agregar_amigo, parent),
                listener
            )
        }
    }

    class AgregarAmigoViewHolder(itemView: View, listener: AgregarAmigoClickListener) :
        BaseViewHolder<Usuario, AgregarAmigoClickListener>(itemView, listener) {

        private val amigoNombre: TextView = itemView.amigo_nombre
        private val posicionAmigo: TextView = itemView.posicion_amigo
        private val amigoFoto: ImageView = itemView.amigo_foto
        private val check: ImageView = itemView.check

        init {
            listener.run {
                val btnAgregarAmigo = itemView.dialog_btn_agregar_amigo
                btnAgregarAmigo.setOnClickListener { onAgregarAmigoClick(adapterPosition, btnAgregarAmigo, check)
                }
            }
        }

        override fun onBind(item: Usuario) {
            amigoNombre.text = item.nombre
            posicionAmigo.text = item.posicion
            Picasso.get().load(item.foto).into(amigoFoto)
        }

    }

}

interface AgregarAmigoClickListener : BaseRecyclerListener {
    fun onAgregarAmigoClick(position: Int, button: Button, check: ImageView)
}

/////////////////////////////////////////////////////////////////////////////////////////////////

class ListaAmigosAdapter(context: Context, listener: BaseRecyclerListener) :
    GenericRecyclerViewAdapter<Usuario, BaseRecyclerListener, ListaAmigosViewHolder>(
        context, listener
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaAmigosViewHolder {
        return ListaAmigosViewHolder(inflate(R.layout.row_amigo, parent), listener)
    }
}

class ListaAmigosViewHolder(itemView: View, listener: BaseRecyclerListener) :
    BaseViewHolder<Usuario, BaseRecyclerListener>(itemView, listener) {

    private val amigoNombre: TextView = itemView.lista_nombre_amigo
    private val posicionAmigo: TextView = itemView.lista_posicion_amigo
    private val amigoFoto: ImageView = itemView.lista_amigo_foto

    override fun onBind(item: Usuario) {
        amigoNombre.text = item.nombre
        posicionAmigo.text = item.posicion
        Picasso.get().load(item.foto).into(amigoFoto)
    }

}











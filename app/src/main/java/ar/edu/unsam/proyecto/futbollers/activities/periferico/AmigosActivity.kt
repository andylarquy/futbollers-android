package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo.ElegirAmigoViewHolder
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo.ElegirEquipoGenerico
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo.ElegirEquipoMultipleClickListener
import ar.edu.unsam.proyecto.futbollers.activities.drawer.SetupDrawer
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.UsuarioService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_amigos.*
import kotlinx.android.synthetic.main.activity_amigos.base_drawer_layout
import kotlinx.android.synthetic.main.activity_amigos.base_toolbar
import kotlinx.android.synthetic.main.activity_amigos.floating_action_button
import kotlinx.android.synthetic.main.activity_amigos.nav_drawer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.row_elegir_amigo.view.*


class AmigosActivity : AppCompatActivity() {

    lateinit var dialogAgregarAmigo: MaterialDialog
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    val usuarioService = UsuarioService
    lateinit var agregarAmigoAdapter: AgregarAmigoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDialogAgregarAmigo()

        usuarioService.getCandidatosDelUsuario(applicationContext, usuarioLogueado, ::modalCandidatosCallback)

        /* ESTA COPIADO Y PEGADO EN OnRestart POR OBLIGACION */
        //layoutInflater.inflate(R.layout.activity_home, base_drawer_layout, true)
        setContentView(R.layout.activity_amigos)

        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(applicationContext, this, toolbar, drawerLayout, nav_drawer)

        val floatButton: FloatingActionButton =
            floating_action_button.findViewById(R.id.floating_action_button)

        floatButton.setOnClickListener {
            dialogAgregarAmigo.show()
        }


    }

    fun setupDialogAgregarAmigo() {
        //Preparo el dialog de amigos
        dialogAgregarAmigo = MaterialDialog(applicationContext)
            .title(text = "Agrega un amigo")
            .customListAdapter(agregarAmigoAdapter)

    }


    fun modalCandidatosCallback(candidatos: MutableList<Usuario>) {
        agregarAmigoAdapter.clear()
        agregarAmigoAdapter.items = candidatos
        agregarAmigoAdapter.notifyDataSetChanged()
    }


    class AgregarAmigoAdapter(context: Context, listener: OnRecyclerItemClickListener) :
        GenericRecyclerViewAdapter<Usuario, OnRecyclerItemClickListener, AgregarAmigoViewHolder>(
            context, listener
        ) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgregarAmigoViewHolder {
            return AgregarAmigoViewHolder(
                inflate(R.layout.row_elegir_amigo, parent),
                listener
            )
        }
    }

    class AgregarAmigoViewHolder(itemView: View, listener: OnRecyclerItemClickListener) :
        BaseViewHolder<Usuario, OnRecyclerItemClickListener>(itemView, listener) {

        private val amigoNombre: TextView = itemView.amigo_nombre
        private val posicionAmigo: TextView = itemView.posicion_amigo
        private val amigoFoto: ImageView = itemView.amigo_foto

        init {
            listener.run {
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
        /*
    Si implementas recycler view ahi tenes la animacion del boton


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
     */



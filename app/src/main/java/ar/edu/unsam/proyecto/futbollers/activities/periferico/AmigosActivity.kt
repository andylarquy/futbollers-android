package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unsam.proyecto.futbollers.R
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
import kotlinx.android.synthetic.main.row_agregar_amigo.view.*


class AmigosActivity : AppCompatActivity(), AgregarAmigoClickListener {

    lateinit var dialogAgregarAmigo: MaterialDialog
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    val usuarioService = UsuarioService
    lateinit var agregarAmigoAdapter: AgregarAmigoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)

        agregarAmigoAdapter = AgregarAmigoAdapter(this, this)
        setupDialogAgregarAmigo()


        usuarioService.getCandidatosDelUsuario(this, usuarioLogueado, ::modalCandidatosCallback)


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


    }

    fun setupDialogAgregarAmigo() {
        //Preparo el dialog de amigos
        dialogAgregarAmigo = MaterialDialog(this)
            .title(text = "Agrega un amigo")
            .customListAdapter(agregarAmigoAdapter)
            .noAutoDismiss()

    }


    fun modalCandidatosCallback(candidatos: MutableList<Usuario>) {
        agregarAmigoAdapter.clear()
        agregarAmigoAdapter.items = candidatos
        agregarAmigoAdapter.notifyDataSetChanged()
    }




    override fun onAgregarAmigoClick(position: Int, button:Button, check: ImageView) {
        val amigo = agregarAmigoAdapter.getItem(position)
        Toast.makeText(this, "TODO: Implementar POST notificacion-amistad a usuario con id: "+amigo.idUsuario.toString(), Toast.LENGTH_SHORT).show()
        button.isClickable = false
        button.visibility = View.GONE
        check.visibility = View.VISIBLE
        //TODO: descomentar usuarioService.postAmistad(this, usuarioLogueado, callbackAgregarAmigo)
        //TODO: Ver despues tmb la inyeccion de dependencias del check icon
    }

    fun callbackAgregarAmigo(){
        Toast.makeText(this, "Se agrego un usuario a la lista de amigos", Toast.LENGTH_SHORT).show()
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
                btnAgregarAmigo.setOnClickListener { onAgregarAmigoClick(adapterPosition, btnAgregarAmigo, check) }
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



package ar.edu.unsam.proyecto.futbollers.activities.periferico

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.drawer.SetupDrawer
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.domain.Notificacion
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.NotificacionService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.simpleDateFormatter
import com.leodroidcoder.genericadapter.BaseRecyclerListener
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_invitaciones.*
import kotlinx.android.synthetic.main.activity_invitaciones.base_drawer_layout
import kotlinx.android.synthetic.main.activity_invitaciones.base_toolbar
import kotlinx.android.synthetic.main.activity_invitaciones.nav_drawer
import kotlinx.android.synthetic.main.row_invitacion.view.*

class InvitacionesActivity : AppCompatActivity(), InvitacionesClickListener {

    val notificacionesService: NotificacionService = NotificacionService
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    lateinit var invitacionesAdapter: InvitacionesAdapter
    lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitaciones)

        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(this, this, toolbar, drawerLayout, nav_drawer)

        rv = invitaciones_list

        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        invitacionesAdapter = InvitacionesAdapter(this, this)
        rv.adapter = invitacionesAdapter

        refrescarInvitaciones()


    }

    fun refrescarInvitaciones() {
        notificacionesService.getInvitacionesDelUsuario(
            this,
            usuarioLogueado,
            ::callbackInvitaciones
        )
    }

    fun callbackInvitaciones(invitaciones: MutableList<Notificacion>) {
        invitacionesAdapter.clear()
        invitacionesAdapter.items = invitaciones
        invitacionesAdapter.notifyDataSetChanged()
    }

    override fun onAceptarInvitacionClick(position: Int) {
        val invitacion = invitacionesAdapter.getItem(position)
        notificacionesService.aceptarInvitacion(this, invitacion, ::callbackAceptarInvitacion)
    }

    fun callbackAceptarInvitacion() {
        refrescarInvitaciones()
        if (noHayMasNotificaciones()) {
            val intent = Intent(this, HomeActivity::class.java).apply {}
            this.startActivity(intent)
            this.finish()
        }

        Toasty.success(this, "¡Has aceptado la invitación correctamente!", Toast.LENGTH_SHORT).show()
    }

    fun noHayMasNotificaciones(): Boolean {
        return invitacionesAdapter.itemCount < 1
    }

    override fun onRechazarInvitacionClick(position: Int) {
        //TODO: Rechazar invitacion
    }

}

class InvitacionesAdapter(context: Context, listener: InvitacionesClickListener) :
    GenericRecyclerViewAdapter<Notificacion, InvitacionesClickListener, InvitacionesViewHolder>(
        context, listener
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitacionesViewHolder {
        return InvitacionesViewHolder(
            inflate(R.layout.row_invitacion, parent),
            listener
        )
    }
}

class InvitacionesViewHolder(itemView: View, listener: InvitacionesClickListener) :
    BaseViewHolder<Notificacion, InvitacionesClickListener>(itemView, listener) {

    private val direccionPartido: TextView = itemView.direccion_partido
    private val fechaPartido: TextView = itemView.fecha_partido
    private val partidoFoto: ImageView = itemView.partido_foto

    init {
        listener.run {
            itemView.dialog_btn_aceptar_invitacion.setOnClickListener {
                onAceptarInvitacionClick(
                    adapterPosition
                )
            }
            itemView.dialog_btn_rechazar_invitacion.setOnClickListener {
                onRechazarInvitacionClick(
                    adapterPosition
                )
            }
        }
    }

    override fun onBind(item: Notificacion) {
        direccionPartido.text = item.partido!!.empresa!!.direccion
        fechaPartido.text = simpleDateFormatter.format(item.partido.fechaDeReserva!!.time)
        Picasso.get().load(item.partido.empresa!!.foto).into(partidoFoto)
    }

}

interface InvitacionesClickListener : BaseRecyclerListener {
    fun onAceptarInvitacionClick(position: Int)
    fun onRechazarInvitacionClick(position: Int)
}
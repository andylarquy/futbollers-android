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
import kotlinx.android.synthetic.main.activity_candidatos.*
import kotlinx.android.synthetic.main.row_candidato.view.*

class CandidatosActivity: AppCompatActivity(), AceptarCandidatoClickListener{

    lateinit var candidatoAdapter:CandidatoAdapter
    val notificacionesService: NotificacionService = NotificacionService
    val usuarioLogueado: Usuario = UsuarioLogueado.usuario
    lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidatos)

        val setupDrawer = SetupDrawer()

        val toolbar = base_toolbar
        val drawerLayout = base_drawer_layout

        setSupportActionBar(toolbar)
        setupDrawer.startSetup(this, this, toolbar, drawerLayout, nav_drawer)

        rv = candidatos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        candidatoAdapter = CandidatoAdapter(this, this)
        rv.adapter = candidatoAdapter

        //Render pantalla de carga
        //loading_spinner?.visibility = View.GONE

        refrescarCandidatos()

    }

    fun callbackCandidatos(notificacionesCandidatos: MutableList<Notificacion>){
        candidatoAdapter.clear()
        candidatoAdapter.items = notificacionesCandidatos
        candidatoAdapter.notifyDataSetChanged()
        //Deshabilito pantalla de carga
       // loading_spinner?.visibility = View.GONE
    }

    override fun onAceptarCandidatoClick(position: Int) {
        val candidato = candidatoAdapter.getItem(position)
        notificacionesService.aceptarCandidato(this, candidato, ::callbackAceptarCandidato)

    }

    fun callbackAceptarCandidato(){
        refrescarCandidatos()
        if (noHayMasNotificaciones()) {
            val intent = Intent(this, HomeActivity::class.java).apply {}
            this.startActivity(intent)
            this.finish()
        }

        Toasty.success(this, "¡Has aceptado al candidato correctamente!", Toast.LENGTH_SHORT).show()
    }

    fun noHayMasNotificaciones(): Boolean {
        return candidatoAdapter.itemCount < 1
    }

    fun refrescarCandidatos(){
        notificacionesService.getNotificacionesDeCandidatosDelUsuario(this, usuarioLogueado, ::callbackCandidatos)
    }


    override fun onRechazarCandidatoClick(position: Int) {
        val candidato = candidatoAdapter.getItem(position)
        Toast.makeText(this, "TODO: Implementar POST rechazar-candidato (?? ", Toast.LENGTH_SHORT).show()
    }

}


class CandidatoViewHolder(itemView: View, listener: AceptarCandidatoClickListener?) : BaseViewHolder<Notificacion, AceptarCandidatoClickListener>(itemView, listener) {

    private val nombreCandidato: TextView? = itemView.nombre_candidato
    private val posicionCandidato: TextView? = itemView.posicion_candidato
    private val direccionPartido: TextView? = itemView.direccion_partido
    private val fechaPartido: TextView? = itemView.fecha_partido
    private val candidatoFoto: ImageView? = itemView.candidato_foto

    init {
        listener?.run {
            itemView.btn_aceptar_candidato.setOnClickListener { onAceptarCandidatoClick(adapterPosition) }
            itemView.btn_rechazar_candidato.setOnClickListener { onRechazarCandidatoClick(adapterPosition) }
        }
    }


    override fun onBind(item: Notificacion) {
        nombreCandidato?.text = item.usuarioReceptor!!.nombre
        posicionCandidato?.text = item.usuarioReceptor.posicion
        direccionPartido?.text = item.partido!!.empresa!!.direccion

        fechaPartido?.text = simpleDateFormatter.format(item.partido.fechaDeReserva!!.time)
        Picasso.get().load(item.usuarioReceptor.foto).into(candidatoFoto)
    }
}

class CandidatoAdapter(context: Context, listener: CandidatosActivity) : GenericRecyclerViewAdapter<Notificacion, AceptarCandidatoClickListener, CandidatoViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatoViewHolder {
        return CandidatoViewHolder(inflate(R.layout.row_candidato, parent), listener)
    }
}

interface AceptarCandidatoClickListener : BaseRecyclerListener {
    fun onAceptarCandidatoClick(position: Int)
    fun onRechazarCandidatoClick(position: Int)
}

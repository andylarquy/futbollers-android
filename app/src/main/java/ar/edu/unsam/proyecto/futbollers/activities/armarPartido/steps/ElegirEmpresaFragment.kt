package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.content.Context
import ar.edu.unsam.proyecto.futbollers.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.domain.Cancha
import ar.edu.unsam.proyecto.futbollers.domain.Empresa
import ar.edu.unsam.proyecto.futbollers.services.EmpresaService
import com.leodroidcoder.genericadapter.BaseViewHolder
import com.leodroidcoder.genericadapter.GenericRecyclerViewAdapter
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.squareup.picasso.Picasso
import ivb.com.materialstepper.stepperFragment
import kotlinx.android.synthetic.main.fragment_elegir_empresa.*
import kotlinx.android.synthetic.main.row_empresa.view.*


class ElegirEmpresaFragment: stepperFragment(), OnRecyclerItemClickListener {
    override fun onNextButtonHandler(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_elegir_empresa, container, false)
    }

    lateinit var empresaAdapter: EmpresaAdapter
    var rv = empresas_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val empresaService = EmpresaService


        //val usuarioLogueado = UsuarioLogueado.usuario

        rv = empresas_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm
        empresaAdapter = context?.let { EmpresaAdapter(it, this) }!!
        rv.adapter = empresaAdapter

        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        empresaService.getEmpresas(context!!, ::callBackEmpresa)

    }

    fun callBackEmpresa(empresa: MutableList<Empresa>){
        empresaAdapter.items?.clear()
        empresaAdapter.items = empresa
        empresaAdapter.notifyDataSetChanged()

        //Desactivo pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE
        Log.i("ArmarPartidoActivity", empresaAdapter.items.size.toString())

    }

    override fun onItemClick(position: Int) {
        val empresaSeleccionada: Empresa = empresaAdapter.getItem(position)
        Log.i("ArmarPartidoActivty", "TODO: Darle comportamiento al click (no va a ser tan facil)")
        Toast.makeText(context, "TODO: Seleccionar empresa (con id: "+empresaSeleccionada.id+")", Toast.LENGTH_SHORT).show()
    }
}





//RECOMIENDO CERRAR ESTOS ARCHIVOS, SON AUXILIARES
// (CORTESIA DE com.leodroidcoder:generic-adapter:1.0.1

class EmpresaViewHolder(itemView: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<Empresa, OnRecyclerItemClickListener>(itemView, listener) {


    private val cv: CardView = itemView.cv
    private val nombre: TextView = itemView.nombre
    private val direccion: TextView = itemView.direccion
    private val empresaFoto: ImageView = itemView.empresa_foto

    init {
        listener?.run {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
            cv.setOnClickListener { onItemClick(adapterPosition) }
        }

    }

    override fun onBind(item: Empresa) {
        nombre.text = item.nombre
        direccion.text = item.direccion
        Picasso.get().load(item.foto).into(empresaFoto)
    }
}

class EmpresaAdapter(context: Context, listener: ElegirEmpresaFragment) : GenericRecyclerViewAdapter<Empresa, OnRecyclerItemClickListener, EmpresaViewHolder>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        return EmpresaViewHolder(inflate(R.layout.row_empresa, parent), listener)
    }
}
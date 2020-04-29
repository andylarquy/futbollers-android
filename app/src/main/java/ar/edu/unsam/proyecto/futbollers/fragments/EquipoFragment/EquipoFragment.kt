package ar.edu.unsam.proyecto.futbollers.fragments.EquipoFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.services.EquipoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import kotlinx.android.synthetic.main.fragment_equipo.*

class EquipoFragment: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equipo, container, false)
    }

    var equipoAdapter = EquipoAdapter()
    var rv = equipos_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val equipoService= EquipoService
        val usuarioLogueado = UsuarioLogueado.usuario

        rv = equipos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        equipoAdapter = EquipoAdapter()
        rv.adapter = equipoAdapter

        equipoService.getEquiposDelUsuario(context!!, usuarioLogueado, ::callBackEquipos)
    }

    fun callBackEquipos(equipos: MutableList<Equipo>){
        equipoAdapter.equipos!!.clear()
        equipoAdapter.equipos!!.addAll(equipos)
        equipoAdapter.notifyDataSetChanged()
    }

}
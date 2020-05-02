package ar.edu.unsam.proyecto.futbollers.fragments.PartidoFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.services.PartidoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import kotlinx.android.synthetic.main.fragment_partido.*


class PartidoFragment: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_partido, container, false)
    }

    var partidoAdapter = PartidoAdapter()
    var rv = partidos_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val partidoService= PartidoService
        val usuarioLogueado = UsuarioLogueado.usuario

        rv = partidos_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        partidoAdapter = PartidoAdapter()
        rv.adapter = partidoAdapter

        partidoService.getPartidosDelUsuario(context!!, usuarioLogueado, ::callBackPartidos)

    }

    fun callBackPartidos(partidos: MutableList<Partido>){
        partidoAdapter.partidos!!.clear()
        partidoAdapter.partidos!!.addAll(partidos)
        partidoAdapter.notifyDataSetChanged()
        Log.i("HomeActivity", partidoAdapter.partidos!!.map { it.empresa!!.foto }.toString())
    }

}
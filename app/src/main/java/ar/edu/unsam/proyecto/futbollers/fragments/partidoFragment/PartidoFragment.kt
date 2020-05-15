package ar.edu.unsam.proyecto.futbollers.fragments.PartidoFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.ArmarPartido.ArmarPartidoActivty
import ar.edu.unsam.proyecto.futbollers.domain.Partido
import ar.edu.unsam.proyecto.futbollers.services.PartidoService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import kotlinx.android.synthetic.main.fragment_partido.*
import kotlinx.android.synthetic.main.fragment_partido.floating_action_button


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

        //Render pantalla de carga
        loading_spinner?.visibility = View.VISIBLE

        //Burocracia para ocultar el float button (recomiendo ocultarlo)
        val fab = floating_action_button
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scroll Down
                    if (fab.isShown) {
                        fab.hide()
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown) {
                        fab.show()
                    }
                }
            }
        })

        fab.setOnClickListener {
            val intent = Intent(activity, ArmarPartidoActivty::class.java).apply{}
            startActivity(intent)
        }

    }



    fun callBackPartidos(partidos: MutableList<Partido>){
        partidoAdapter.partidos!!.clear()
        partidoAdapter.partidos!!.addAll(partidos)
        partidoAdapter.notifyDataSetChanged()
        Log.i("HomeActivity", partidoAdapter.partidos!!.map { it.empresa!!.foto }.toString())

        //Desactivo pantalla de carga
        loading_spinner?.visibility = View.INVISIBLE
    }

}
package ar.edu.unsam.proyecto.futbollers.activities.ArmarPartido.steps.elegirCancha

import ar.edu.unsam.proyecto.futbollers.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ivb.com.materialstepper.stepperFragment
import kotlinx.android.synthetic.main.fragment_elegir_cancha.*


class ElegirCanchaFragment: stepperFragment() {
    override fun onNextButtonHandler(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_elegir_cancha, container, false)
    }

    var canchaAdapter = CanchaAdapter()
    var rv = canchas_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val usuarioLogueado = UsuarioLogueado.usuario

        rv = canchas_list
        rv.setHasFixedSize(true)

        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm

        canchaAdapter = CanchaAdapter()
        rv.adapter = canchaAdapter

    }
}
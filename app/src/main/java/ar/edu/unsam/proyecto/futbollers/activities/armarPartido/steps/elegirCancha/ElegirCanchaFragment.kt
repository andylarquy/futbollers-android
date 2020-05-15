package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirCancha

import ar.edu.unsam.proyecto.futbollers.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ivb.com.materialstepper.stepperFragment


class ElegirCanchaFragment: stepperFragment() {
    override fun onNextButtonHandler(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_elegir_cancha, container, false)
    }


}
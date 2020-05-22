package ar.edu.unsam.proyecto.futbollers.activities.armarPartido

import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.ElegirEmpresaFragment
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirCancha.ElegirCanchaFragment
import ivb.com.materialstepper.progressMobileStepper

class ArmarPartidoActivty : progressMobileStepper() {
    override fun onStepperCompleted() {
        true
    }

    override fun init(): MutableList<Class<*>> {
        return mutableListOf(ElegirEmpresaFragment::class.java, ElegirCanchaFragment::class.java)
    }

}
package ar.edu.unsam.proyecto.futbollers.activities.armarPartido

import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirCancha.ElegirCanchaFragment
import ivb.com.materialstepper.progressMobileStepper

class ArmarPartidoActivty : progressMobileStepper() {
    override fun onStepperCompleted() {
        true
    }

    override fun init(): MutableList<Class<*>> {
        val stepperFragmentList: MutableList<Class<*>> = ArrayList()
        stepperFragmentList.add(ElegirCanchaFragment::class.java)
        return stepperFragmentList
    }

}
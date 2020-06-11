package ar.edu.unsam.proyecto.futbollers.activities.armarPartido

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.ElegirEmpresaFragment
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.ElegirCanchaFragment
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo.ElegirEquipoLocalFragment
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps.elegirEquipo.ElegirEquipoVisitanteFragment
import ar.edu.unsam.proyecto.futbollers.activities.drawer.ToolbarActivity
import ar.edu.unsam.proyecto.futbollers.domain.*
import com.stepstone.stepper.Step
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import kotlinx.android.synthetic.main.activity_armar_partido.*
import kotlinx.android.synthetic.main.util_drawer.*
import java.util.*

var mStepperLayout: StepperLayout? = null
var empresaSeleccionada: Empresa? = null
var canchaSeleccionada: Cancha? = null
var fechaSeleccionada: Date? = null
var promocionSeleccionada: Promocion? = null
var equipoLocalSeleccionado: Equipo? = null
var equipoVisitanteSeleccionado: Equipo? = null

var amigosPosibles: MutableList<Usuario>? = ArrayList()
var equiposPosibles: MutableList<Equipo>? = ArrayList()

class ArmarPartidoActivity : ToolbarActivity(), StepperLayout.StepperListener {

    private var mStepperAdapter: StepperAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_armar_partido)
        layoutInflater.inflate(R.layout.activity_armar_partido, base_drawer_layout, true)

        mStepperLayout = stepperLayout

        mStepperAdapter = StepperAdapter(supportFragmentManager, this)
        mStepperLayout!!.adapter = mStepperAdapter
        mStepperLayout!!.setListener(this)

    }

    override fun onStepSelected(newStepPosition: Int) {}

    override fun onError(verificationError: VerificationError?) {
        Toast.makeText(this, "Error inesperado!" + verificationError!!.errorMessage, Toast.LENGTH_SHORT).show()

    }

    override fun onReturn() {
        finish()
    }

    override fun onCompleted(completeButton: View?) {
        Toast.makeText(this, "Listo, te recibiste!", Toast.LENGTH_SHORT).show()
    }

}


const val CANTIDAD_DE_STEPS: Int = 4

class StepperAdapter(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm,
    context
) {
    private val CURRENT_STEP_POSITION_KEY = "messageResourceId"//messageResourceId


    override fun createStep(position: Int): Step? {
        when (position) {
            0 -> {
                val step1 =
                    ElegirEmpresaFragment()
                val b1 = Bundle()
                b1.putInt(CURRENT_STEP_POSITION_KEY, position)
                step1.arguments = b1
                return step1
            }
            1 -> {
                val step2 = ElegirCanchaFragment()
                val b2 = Bundle()
                b2.putInt(CURRENT_STEP_POSITION_KEY, position)
                step2.arguments = b2
                return step2
            }

            2 -> {
                val step3 =
                    ElegirEquipoLocalFragment()
                val b3 = Bundle()
                b3.putInt(CURRENT_STEP_POSITION_KEY, position)
                step3.arguments = b3
                return step3
            }

            3 -> {
                val step4 = ElegirEquipoVisitanteFragment()
                val b4 = Bundle()
                b4.putInt(CURRENT_STEP_POSITION_KEY, position)
                step4.arguments = b4
                return step4
            }
        }
        return null
    }

    override fun getCount(): Int {
        return CANTIDAD_DE_STEPS
    }

    override fun getViewModel(@IntRange(from = 0) position: Int): StepViewModel {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        when (position) {
            0 -> return StepViewModel.Builder(context)
                .setTitle("Elegir Sede")
                .create()

            1 -> return StepViewModel.Builder(context)
                .setTitle("Elegir Cancha")
                .create()

            2 -> return StepViewModel.Builder(context)
                .setTitle("Elegir Equipo Local")
                .create()

            3 -> return StepViewModel.Builder(context)
                .setTitle("Elegir Equipo Visitante")
                .create()
        }
        return StepViewModel.Builder(context).create()
    }

}

fun showStepperNavigation(){
    mStepperLayout!!.setShowBottomNavigation(true)
}

fun hideStepperNavigation(){
    mStepperLayout!!.setShowBottomNavigation(false)
    mStepperLayout!!.adapter.createStep(1)
}

fun stepForward(){
    val currentStep = mStepperLayout!!.currentStepPosition
    mStepperLayout!!.currentStepPosition = currentStep + 1
}
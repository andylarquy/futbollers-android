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
import com.stepstone.stepper.Step
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import kotlinx.android.synthetic.main.activity_armar_partido.*

class ArmarPartidoActivty : AppCompatActivity(), StepperLayout.StepperListener {


    var mStepperLayout: StepperLayout? = null
    private var mStepperAdapter: StepperAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_armar_partido)

        mStepperLayout = stepperLayout

        mStepperAdapter = StepperAdapter(supportFragmentManager, this)
        mStepperLayout!!.adapter = mStepperAdapter
        mStepperLayout!!.setListener(this)
    }

    override fun onStepSelected(newStepPosition: Int) {

    }

    override fun onError(verificationError: VerificationError?) {
        Toast.makeText(
            this,
            "Que rompiste Seba!" + verificationError!!.errorMessage,
            Toast.LENGTH_SHORT
        ).show()

    }

    override fun onReturn() {
        finish()
    }

    override fun onCompleted(completeButton: View?) {
        Toast.makeText(this, "Listo, te recibiste!", Toast.LENGTH_SHORT).show()
    }

}


val CANTIDAD_DE_STEPS: Int = 2

class StepperAdapter(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm,
    context
) {
    private val CURRENT_STEP_POSITION_KEY = "messageResourceId"


    override fun createStep(position: Int): Step? {
        when (position) {
            0 -> {
                val step1 = ElegirEmpresaFragment()
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
                .setTitle("Elegir Cancha") //can be a CharSequence instead
                .create()
        }
        return StepViewModel.Builder(context).create()
    }

}
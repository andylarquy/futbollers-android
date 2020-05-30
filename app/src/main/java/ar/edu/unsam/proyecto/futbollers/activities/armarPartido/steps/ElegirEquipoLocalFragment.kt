package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.hideStepperNavigation
import ar.edu.unsam.proyecto.futbollers.activities.armarPartido.showStepperNavigation
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError

class ElegirEquipoLocalFragment : Fragment(), BlockingStep {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_elegir_equipo_local, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        Handler().postDelayed({ callback.goToNextStep() }, 1000L)
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        Toast.makeText(this.context, "FIN!!", Toast.LENGTH_SHORT).show()
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onSelected() {
        showStepperNavigation()
    }

    override fun onError(error: VerificationError) {}



}
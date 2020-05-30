package ar.edu.unsam.proyecto.futbollers.activities.armarPartido.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ar.edu.unsam.proyecto.futbollers.R
import com.leodroidcoder.genericadapter.OnRecyclerItemClickListener
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError

class ElegirEquipoLocalFragment: Fragment(), BlockingStep, OnRecyclerItemClickListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_elegir_equipo_local, container, false)
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback?) {
        TODO("Not yet implemented")
    }

    override fun onSelected() {
        TODO("Not yet implemented")
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        TODO("Not yet implemented")
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?) {
        TODO("Not yet implemented")
    }

    override fun verifyStep(): VerificationError? {
        TODO("Not yet implemented")
    }

    override fun onError(error: VerificationError) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}
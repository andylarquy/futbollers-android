package ar.edu.unsam.proyecto.futbollers.activities.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.databinding.ActivitySignUpBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.POSICIONES
import ar.edu.unsam.proyecto.futbollers.services.SignUpService
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    val usuarioNuevo: Usuario = Usuario()
    val signUpService = SignUpService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        binding.setUsuarioNuevo(usuarioNuevo)


        btn_signUp.setOnClickListener() {

            try {
                usuarioNuevo.validarSignUp()
                usuarioNuevo.formatearSexo()
                signUpService.postUsuarioNuevo(this, usuarioNuevo, ::callbackSignUpUsuario)
            }catch(e: Error){
                Toasty.error(this, e.message!!, Toast.LENGTH_SHORT).show()
            }



        }

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, POSICIONES)
        combo_posiciones.setAdapter(adapter)

    }
        fun callbackSignUpUsuario() {
            //GoTo Login con el usuario creado
            val intent = Intent(this, LoginActivity::class.java).apply{}
            startActivity(intent)


            Toasty.success(this, "Se ha registrado su usuario correctamente", Toast.LENGTH_LONG).show()
        }

}
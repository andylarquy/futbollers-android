package ar.edu.unsam.proyecto.futbollers.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.databinding.ActivitySignUpBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.Constants.POSICIONES
import ar.edu.unsam.proyecto.futbollers.services.SignUpService
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    val usuarioNuevo: Usuario = Usuario()
    val signUpService: SignUpService = SignUpService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        //val binding: ActivitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)

        binding.setUsuarioNuevo(usuarioNuevo)


        btn_signUp.setOnClickListener() {
            signUpService.postUsuarioNuevo(
                this@SignUpActivity,
                usuarioNuevo,
                ::callbackSignUpUsuario
            )

        }

        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item, POSICIONES)
        combo_posiciones.setAdapter(adapter)

    }
        fun callbackSignUpUsuario() {
            //GoTo Login con el usuario creado
            val intent = Intent(this, LoginActivity::class.java).apply{}
            startActivity(intent)

            //TODO: VALIDAR CAMPOS
            Toast.makeText(
                this@SignUpActivity,
                "Bienvenido " + usuarioNuevo.nombre,
                Toast.LENGTH_LONG
            ).show()
        }

}
package ar.edu.unsam.proyecto.futbollers.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.databinding.ActivityLoginBinding
import ar.edu.unsam.proyecto.futbollers.databinding.ActivitySignUpBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    val usuarioNuevo: Usuario = Usuario()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding: ActivitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        binding.setUsuarioNuevo(usuarioNuevo)

        btn_signUp.setOnClickListener(){
            Toast.makeText(this@SignUpActivity, usuarioNuevo.nombre, Toast.LENGTH_LONG).show()

        }

    }




}
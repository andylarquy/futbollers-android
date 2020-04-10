package ar.edu.unsam.proyecto.futbollers

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.databinding.ActivityLoginBinding
import ar.edu.unsam.proyecto.futbollers.services.LoginService

class LoginActivity : AppCompatActivity() {
    var usuarioLogueado:Usuario = Usuario()
    private var loginService: LoginService = LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.setUsuarioLogueado(usuarioLogueado)

        btn_login.setOnClickListener {
            Log.i("LoginActivity", "Voy a la API con mi usuario de entrada: $usuarioLogueado \n")
            loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado)
        }

    }

    fun callbackUsuarioLogueado(usuario: Usuario){
        usuarioLogueado = usuario

        //GoTo HomeActivty
        Toast.makeText(this@LoginActivity, "Bienvenido ${usuarioLogueado.nombre}!!\nTODO: GoTo HomeActivity", Toast.LENGTH_LONG).show()
    }
}

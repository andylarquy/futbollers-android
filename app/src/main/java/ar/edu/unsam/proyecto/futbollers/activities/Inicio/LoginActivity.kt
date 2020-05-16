package ar.edu.unsam.proyecto.futbollers.activities.Inicio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.Home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.databinding.ActivityLoginBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.LoginService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import kotlinx.android.synthetic.main.activity_login.*



class LoginActivity : AppCompatActivity() {
    var usuarioLogueado:Usuario = UsuarioLogueado.usuario
    private var loginService: LoginService = LoginService

    val activityLogin = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityLogin)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this,
            activityLogin
        )
        binding.setUsuarioLogueado(usuarioLogueado)

        text_signUp.setOnClickListener(){
            //GoTo SignupActivty
            val intent = Intent(this, SignUpActivity::class.java).apply{}
            startActivity(intent)

            Toast.makeText(this@LoginActivity, "TODO: Hacer SignUpActivity", Toast.LENGTH_LONG).show()

        }

        btn_login.setOnClickListener {
            Log.i("LoginActivity", "Voy a la API con mi usuario de entrada: $usuarioLogueado \n")

            // RENDER PANTALLA DE CARGA
            loading_spinner.alpha = 1F
            loading_spinner_parent.alpha = 1F
            loading_spinner_parent.visibility = View.VISIBLE

            loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado, ::callbackErrorUsuarioLogueado)

        }

            // RENDER PANTALLA DE CARGA
            loading_spinner.alpha = 1F
            loading_spinner_parent.alpha = 1F
            loading_spinner_parent.visibility = View.VISIBLE

            loginWithStoredCredentials()

    }

    //TODO: Encriptar esto por favor!!!
    fun loginWithStoredCredentials(){

        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)

        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)
        usuarioLogueado.email = email
        usuarioLogueado.password = password
        loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado, ::callbackErrorUsuarioLogueado)
    }

    fun guardarCredenciales(email: String?, password: String?){
        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val sharedPrefEdit: SharedPreferences.Editor = sharedPref.edit()
        sharedPrefEdit.putString("email", email)
        sharedPrefEdit.putString("password", password)
        sharedPrefEdit.apply()
    }

    fun callbackUsuarioLogueado(usuario: Usuario){
        UsuarioLogueado.usuario = usuario

        guardarCredenciales(usuarioLogueado.email, usuarioLogueado.password)
        //GoTo HomeActivty
        val intent = Intent(this,
            HomeActivity::class.java).apply{}
        startActivity(intent)
        finish()
        Log.i("LoginActivity", UsuarioLogueado.usuario.id)
    }

    fun callbackErrorUsuarioLogueado(error: Exception){
        // DESACTIVA RENDER PANTALLA DE CARGA
        loading_spinner_parent.visibility = View.INVISIBLE
    }
}


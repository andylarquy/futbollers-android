package ar.edu.unsam.proyecto.futbollers.activities.inicio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.activities.drawer.ToolbarActivity
import ar.edu.unsam.proyecto.futbollers.activities.home.HomeActivity
import ar.edu.unsam.proyecto.futbollers.databinding.ActivityLoginBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.LoginService
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.util_toolbar.*


class LoginActivity : ToolbarActivity() {
    var usuarioLogueado:Usuario = UsuarioLogueado.usuario
    private var loginService: LoginService = LoginService

    val activityLogin = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(activityLogin)


        //layoutInflater.inflate(R.layout.activity_login, content_frame_layout)
        val binding: ActivityLoginBinding = DataBindingUtil.inflate(layoutInflater,R.layout.activity_login, toolbar_frame_layout, true)
        //val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this,
           // activityLogin
        //)
        binding.setUsuarioLogueado(usuarioLogueado)

        val b = intent.extras
        if(b != null) {
            val cerrarSesion = b.getBoolean("cerrarSesion")

            if(cerrarSesion !== null){
                if(cerrarSesion){
                    clearCredentialsStorage()
                }
            }
        }
        text_signUp.setOnClickListener(){
            //GoTo SignupActivty
            val intent = Intent(this, SignUpActivity::class.java).apply{}
            startActivity(intent)

        }

        btn_login.setOnClickListener {
            Log.i("LoginActivity", "Voy a la API con mi usuario de entrada: $usuarioLogueado \n")

            // RENDER PANTALLA DE CARGA
            loading_spinner.alpha = 1F
            loading_spinner.visibility = View.VISIBLE



            loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado, ::callbackErrorUsuarioLogueado)

        }

            // RENDER PANTALLA DE CARGA
            loading_spinner.alpha = 1F
            loading_spinner.alpha = 1F
            loading_spinner.visibility = View.VISIBLE

            loginWithStoredCredentials()

    }

    //TODO: Encriptar esto por favor!!!
    fun loginWithStoredCredentials(){

        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)

        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)
        usuarioLogueado.email = email
        usuarioLogueado.password = password
        loginService.getUsuarioLogueado(this, usuarioLogueado, ::callbackUsuarioLogueado, ::callbackErrorUsuarioLogueadoAutomatico)
    }

    fun guardarCredenciales(email: String?, password: String?, idUsuario: String?){
        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val sharedPrefEdit: SharedPreferences.Editor = sharedPref.edit()
        sharedPrefEdit.putString("idUsuario", idUsuario)
        sharedPrefEdit.putString("email", email)
        sharedPrefEdit.putString("password", password)
        sharedPrefEdit.apply()
    }

    fun callbackUsuarioLogueado(usuario: Usuario){

        UsuarioLogueado.usuario = usuario

        guardarCredenciales(UsuarioLogueado.usuario.email, UsuarioLogueado.usuario.password, UsuarioLogueado.usuario.idUsuario.toString())
        //GoTo HomeActivty
        val intent = Intent(this,
            HomeActivity::class.java).apply{}
        startActivity(intent)
        finish()
    }

    fun callbackErrorUsuarioLogueado(errorMessage: String){
        // DESACTIVA RENDER PANTALLA DE CARGA
        loading_spinner.visibility = View.INVISIBLE
        if(errorMessage.isNotBlank()) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun callbackErrorUsuarioLogueadoAutomatico(error: String){
        // DESACTIVA RENDER PANTALLA DE CARGA
        loading_spinner.visibility = View.INVISIBLE
    }

    fun clearCredentialsStorage(){
        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val sharedPrefEdit: SharedPreferences.Editor = sharedPref.edit()
        sharedPrefEdit.putString("idUsuario", "")
        sharedPrefEdit.putString("email", "")
        sharedPrefEdit.putString("password", "")
        sharedPrefEdit.apply()
    }

}


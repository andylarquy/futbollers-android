package ar.edu.unsam.proyecto.futbollers.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ar.edu.unsam.proyecto.futbollers.R
import ar.edu.unsam.proyecto.futbollers.databinding.ActivityLoginBinding
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.LoginService
import kotlinx.android.synthetic.main.activity_login.*



class LoginActivity : AppCompatActivity() {
    private var usuarioLogueado:Usuario = Usuario()
    private var loginService: LoginService = LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_login
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
            loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado)
        }

        try {
            loginWithStoredCredentials()
        }catch (e: Exception){
            Log.i("LoginActivity", "[DEBUG]: Error AutoLogin: "+e.message)
            Toast.makeText(this@LoginActivity, "Hubo un error al intentar iniciar sesion automaticamente, por favor vuelva a ingresar", Toast.LENGTH_LONG).show()
        }


    }

    //TODO: Encriptar esto por favor!!!
    fun loginWithStoredCredentials(){

        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)

        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)
        usuarioLogueado.email = email
        usuarioLogueado.password = password
        loginService.getUsuarioLogueado(this@LoginActivity, usuarioLogueado, ::callbackUsuarioLogueado)
    }

    fun guardarCredenciales(email: String?, password: String?){
        val sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val sharedPrefEdit: SharedPreferences.Editor = sharedPref.edit()
        sharedPrefEdit.putString("email", email)
        sharedPrefEdit.putString("password", password)
        sharedPrefEdit.apply()
    }

    fun callbackUsuarioLogueado(usuario: Usuario){
        usuarioLogueado = usuario

        guardarCredenciales(usuarioLogueado.email, usuarioLogueado.password)
        //GoTo HomeActivty
        val intent = Intent(this,HomeActivity::class.java).apply{}
        startActivity(intent)
        finish()
        Toast.makeText(this@LoginActivity, "Bienvenido ${usuarioLogueado.nombre}!!\nTODO: GoTo HomeActivity", Toast.LENGTH_LONG).show()
    }
}


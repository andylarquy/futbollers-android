package ar.edu.unsam.proyecto.futbollers

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.services.LoginService

import com.android.volley.Response

import java.io.IOException


class MainActivity : AppCompatActivity() {

    var usuarioLogueado:Usuario = Usuario()
    private var loginService: LoginService = LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_login.setOnClickListener {
            loginService.getUsuarioLogueado(this@MainActivity, usuarioLogueado) { callbackUsuarioLogueado() }
        }

    }

    fun callbackUsuarioLogueado(){
        Toast.makeText(this@MainActivity,
            "id: "+usuarioLogueado.id.toString()
                +" nombre: "+usuarioLogueado.nombre
                +" sexo: "+usuarioLogueado.sexo
                    +" posicion: "+usuarioLogueado.posicion
                    +" email: "+usuarioLogueado.email
                    +" lat: "+usuarioLogueado.lat
                    +" lon: "+usuarioLogueado.lon

            , Toast.LENGTH_SHORT).show()
    }


}


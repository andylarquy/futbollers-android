package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object SignUpService {
    fun postUsuarioNuevo(context: Context, usuarioNuevo: Usuario, callback: () -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario"

        val request = JsonObjectRequest(
            Request.Method.POST, url, Usuario().toJson(usuarioNuevo),
            Response.Listener<JSONObject> { response ->

                Log.i("SignUpActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("SignUpActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("SignUpActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        if (statusCode == 422) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }
    }

}
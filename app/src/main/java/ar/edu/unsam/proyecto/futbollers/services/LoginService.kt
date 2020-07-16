package ar.edu.unsam.proyecto.futbollers.services

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.BASE_URL
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import com.android.volley.*

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject
import java.lang.Exception

object LoginService {

    fun getUsuarioLogueado(
        context: Context,
        usuario: Usuario,
        callback: (Usuario) -> Unit,
        callbackError: (String) -> Unit
    ) {

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                val deviceToken = instanceIdResult.token
                usuario.token = deviceToken
                Log.i("HomeActivity", "[DEBUG] Device token: ${usuario.token}")
                peticionUsuarioLogueado(context, usuario, callback, callbackError)
            }
    }

    fun peticionUsuarioLogueado(
        context: Context,
        usuario: Usuario,
        callback: (Usuario) -> Unit,
        callbackError: (String) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url = "$BASE_URL/login"

        Log.i("LoginActivity", "[DEBUG] Device token: ${usuario.token}")
        Log.i("LoginActivity", "[DEBUG] JSON SALIDA: ${Usuario().toJson(usuario)}")
        val request = JsonObjectRequest(
            Request.Method.POST, url, Usuario().toJson(usuario),
            Response.Listener<JSONObject> { response ->
                val nuevoUsuario = Usuario().fromJson(response.toString())
                Log.i("LoginActivity", "Respuesta de la API Rest: $response\n")
                callback(nuevoUsuario)
            },
            Response.ErrorListener {
                handleError(context, it, callbackError)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    fun handleError(context: Context, error: VolleyError, callbackError: (String) -> Unit) {
        Log.i("LoginActivity", "[DEBUG]: API Rest Error: +" + error.toString())

        var errorMessage = ""

        if (error is ClientError) {
            errorMessage = "El dispositivo no se pudo conectar con el servidor, vuelva a intentar mas tarde"
        } else if (error is NoConnectionError) {
            errorMessage = "Revise su conexion a internet"
        } else if (error is TimeoutError) {
            errorMessage = "No se pudo conectar con el servidor, vuelva a intentar mas tarde"
        } else if (error is AuthFailureError) {
            errorMessage = "Credenciales invalidas"
        }

        callbackError(errorMessage)

    }

}
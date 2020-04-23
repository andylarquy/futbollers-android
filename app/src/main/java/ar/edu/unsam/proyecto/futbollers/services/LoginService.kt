package ar.edu.unsam.proyecto.futbollers.services

import ar.edu.unsam.proyecto.futbollers.domain.Usuario

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.services.Constants.BASE_URL
import com.android.volley.*

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

object LoginService {

 fun getUsuarioLogueado(context: Context, usuario: Usuario, callback: (Usuario) -> Unit, callbackError: (Exception) -> Unit) {

    val queue = Volley.newRequestQueue(context)
    val url = "$BASE_URL/usuario/login"

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
    request.retryPolicy = DefaultRetryPolicy(250, 3, 1F)

    queue.add(request)
 }

 fun handleError(context: Context, error: VolleyError, callbackError: (Exception) -> Unit) {
    Log.i("LoginActivity", "[DEBUG]: API Rest Error: +" + error.toString())
    if (error is AuthFailureError) {
       Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
    } else if (error is ClientError) {
       Toast.makeText(context, "El dispositivo no se pudo conectar con el servidor, vuelva a intentar mas tarde", Toast.LENGTH_SHORT).show()
    } else if(error is NoConnectionError){
       Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
    }else if(error is TimeoutError){
       Toast.makeText(context, "No se pudo conectar con el servidor, vuelva a intentar mas tarde", Toast.LENGTH_SHORT).show()
    }

    callbackError(error)

 }

}
package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object SignUpService {
    fun postUsuarioNuevo(context: Context, usuarioNuevo: Usuario, callback: () -> Unit) {

        val queue = Volley.newRequestQueue(context)
        val url = "${Constants.BASE_URL}/usuario"

        val request = JsonObjectRequest(
            Request.Method.POST, url, Usuario().toJson(usuarioNuevo),
            Response.Listener<JSONObject>{ response ->
                Log.i("SignUpActivity", "todo bien")

                Log.i("SignUpActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("SignUpActivity", "todo mal")
               handleError(context, it)
            })
        request.retryPolicy = DefaultRetryPolicy(250, 3, 1F)

        queue.add(request)
        Log.i("SignUpActivity", "fin")
    }

    fun handleError(context: Context, error: VolleyError) {
        Log.i("SignUpActivity", "[DEBUG]: API Rest Error: +" + error.toString())
        if (error is AuthFailureError) {
            Toast.makeText(context, "Las credenciales son invalidas", Toast.LENGTH_SHORT).show()
        } else if (error is ClientError) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        } else if(error is NoConnectionError){
            Toast.makeText(context, "Revise su conexion a internet", Toast.LENGTH_SHORT).show()
        }

    }


}
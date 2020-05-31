package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado.usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object EquipoService {
    fun getEquiposDelUsuario(
        context: Context,
        usuarioLogueado: Usuario,
        callback: (MutableList<Equipo>) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos/"
        Log.i("HomeActivity", url + usuario.id)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.id, null,

            Response.Listener<JSONArray> { response ->

                val equipos = Gson().fromJson(response.toString(), Array<Equipo>::class.java)

                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("HomeActivity", response.toString())
                callback(equipos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("HomeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = DefaultRetryPolicy(250, 3, 1F)

        queue.add(request)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        if (statusCode == 422) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }
    }

}

package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.UsuarioLogueado.usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.longPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object EquipoService {
    fun getEquiposDelUsuario(context: Context, usuarioLogueado: Usuario, callback: (MutableList<Equipo>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

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
        request.retryPolicy = longPolicy

        queue.add(request)

    }

    fun getEquiposAdministradosPorElUsuario(context: Context, usuarioLogueado: Usuario, callback: (MutableList<Equipo>) -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/equipos-owner/"
        Log.i("HomeActivity", url + usuario.idUsuario)

        val request = JsonArrayRequest(
            Request.Method.GET, url + usuario.idUsuario, null,

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
        request.retryPolicy = longPolicy

        queue.add(request)
    }

    fun postEquipo(context: Context, equipo: Equipo, callback: () -> Unit, callbackError: (String) -> Unit){
        val queue = Volley.newRequestQueue(context)

        Log.i("NuevoEquipoActivity", Equipo().toJson(equipo).toString())
        Log.i("NuevoEquipoActivity", Gson().toJson(equipo).toString())

        val url = "${Constants.BASE_URL}/equipos"

        val request = JsonObjectRequest(
            Request.Method.POST, url, Equipo().toJson(equipo),

            Response.Listener<JSONObject> { response ->

                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("NuevoEquipoActivity", response.toString())
                callback()
            },
            Response.ErrorListener {
                Log.i("NuevoEquipoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleErrorPostEquipo(context, it, callbackError)
            })
        request.retryPolicy = longPolicy

        queue.add(request)
    }
    
    fun handleErrorPostEquipo(context: Context, error: VolleyError,callbackError: (String) -> Unit){

        error.networkResponse.data

        val responseBody = String(error.networkResponse.data,  Charsets.UTF_8)
        val data = JSONObject(responseBody)
        val errorMessage = data.optString("message")
        callbackError(errorMessage)

    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        if (statusCode == 422) {
            Toast.makeText(context, "Ese mail ya pertenece a un usuario", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }
    }

}

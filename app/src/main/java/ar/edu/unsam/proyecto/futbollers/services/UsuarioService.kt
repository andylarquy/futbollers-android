package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray

object UsuarioService {

    fun getAmigosDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Usuario>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario/${usuario.idUsuario.toString()}/amigos"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONArray> { response ->

                val amigos = Gson().fromJson(response.toString(), Array<Usuario>::class.java)

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("ArmarPartidoActivity", response.toString())
                callback(amigos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, UsuarioService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)

    }

    fun getCandidatosDelUsuario(context: Context, usuarioLogueado: Usuario, callback: (MutableList<Usuario>) -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/candidatos/${usuarioLogueado.idUsuario}"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONArray> { response ->

                val candidatos = Gson().fromJson(response.toString(), Array<Usuario>::class.java)

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("ArmarPartidoActivity", response.toString())
                callback(candidatos.toMutableList())
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, UsuarioService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)
    }


    fun lambdaManejoErrores(context: Context, statusCode: Int) {}


}
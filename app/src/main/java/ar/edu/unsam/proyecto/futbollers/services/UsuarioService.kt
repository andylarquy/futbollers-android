package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    fun postAmistad(context: Context, amigo: Usuario, callback: (ImageView) -> Unit, check: ImageView){
        val usuarioLogueado = UsuarioLogueado.usuario
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario/${usuarioLogueado.idUsuario}/amigo/${amigo.idUsuario}"

        val request = JsonObjectRequest(
            Request.Method.POST, url, null,

            Response.Listener<JSONObject> { response ->

                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("ArmarPartidoActivity", response.toString())
                callback(check)
            },
            Response.ErrorListener {
                Log.i("ArmarPartidoActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, UsuarioService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)

    }

    fun getUsuarioContactoById(context: Context, idUsuario: Long, nombreMensaje:TextView?, contactoFoto:ImageView?, callback: (Usuario, TextView?, ImageView?) -> Unit){

        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario/"+idUsuario

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,

            Response.Listener<JSONObject> { response ->

                Log.i("MensajeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("MensajeActivity", response.toString())

                val usuarioBuscado = Gson().fromJson(response.toString(), Usuario::class.java)

                callback(usuarioBuscado, nombreMensaje, contactoFoto)
            },
            Response.ErrorListener {
                Log.i("MensajeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.defaultPolicy

        queue.add(request)
    }

    fun deleteAmistad(context: Context, amigo: Usuario, usuario: Usuario, callback: () -> Unit){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario/${usuario.idUsuario}/amigo/${amigo.idUsuario}"

        val request = JsonObjectRequest(
            Request.Method.DELETE, url, null,

            Response.Listener<JSONObject> { response ->

                Log.i("MensajeActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("MensajeActivity", response.toString())

                callback()
            },
            Response.ErrorListener {
                Log.i("MensajeActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, UsuarioService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.mediumPolicy

        queue.add(request)
    }

    fun updatePerfil(context: Context, usuario: Usuario, callback: (Usuario) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/usuario"

        Log.i("PerfilActivity", url)
        Log.i("PerfilActivity", Usuario().toJson(usuario).toString())

        val request = JsonObjectRequest(
            Request.Method.PUT, url, Usuario().toJson(usuario),

            Response.Listener<JSONObject> { response ->
                Log.i("PerfilActivity", "[DEBUG]:Communication with API Rest Suceeded")
                Log.i("PerfilActivity", response.toString())

                callback(usuario)
            },
            Response.ErrorListener {
                Log.i("PerfilActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, UsuarioService::lambdaManejoErrores)
            })
        request.retryPolicy = Constants.longPolicy

        queue.add(request)
    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {}


}
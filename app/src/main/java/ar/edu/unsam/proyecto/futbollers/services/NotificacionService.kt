package ar.edu.unsam.proyecto.futbollers.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import ar.edu.unsam.proyecto.futbollers.activities.periferico.InvitacionesActivity
import ar.edu.unsam.proyecto.futbollers.domain.Equipo
import ar.edu.unsam.proyecto.futbollers.domain.Notificacion
import ar.edu.unsam.proyecto.futbollers.domain.Usuario
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.Constants.defaultPolicy
import ar.edu.unsam.proyecto.futbollers.services.auxiliar.handleError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

object NotificacionService {

    fun getNotificacionesDeCandidatosDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Notificacion>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/notificaciones-candidatos/"

        val request = JsonArrayRequest(
            Request.Method.GET, url + UsuarioLogueado.usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder()
                    .setDateFormat(Constants.DATE_FORMAT)
                    .create()

                val notificaciones = gson.fromJson(response.toString(), Array<Notificacion>::class.java)

                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("CandidatosActivity","Candidatos response: "+ response.toString())
                callback(notificaciones.toMutableList())
            },
            Response.ErrorListener {
                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    fun getInvitacionesDelUsuario(context: Context, usuario: Usuario, callback: (MutableList<Notificacion>) -> Unit) {
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/notificaciones-invitaciones/"

        val request = JsonArrayRequest(
            Request.Method.GET, url + UsuarioLogueado.usuario.idUsuario, null,

            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder()
                    .setDateFormat(Constants.DATE_FORMAT)
                    .create()

                val notificaciones = gson.fromJson(response.toString(), Array<Notificacion>::class.java)

                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Suceeded")

                Log.i("InvitacionesActivity","Invitaciones response: "+ response.toString())
                callback(notificaciones.toMutableList())
            },
            Response.ErrorListener {
                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    fun lambdaManejoErrores(context: Context, statusCode: Int) {
        if (statusCode == 400) {
            Toast.makeText(context, "Error inesperado al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
        }
    }

    fun aceptarInvitacion(context: Context, invitacion: Notificacion, callback: () -> Unit) {
        val queue = Volley.newRequestQueue(context)

        Log.i("InvitacionesActivity",invitacion.idNotificacion.toString())

        val url = "${Constants.BASE_URL}/invitaciones-aceptar/"

        val request = JsonObjectRequest(
            Request.Method.POST, url + invitacion.idNotificacion, null,

            Response.Listener<JSONObject> { response ->
                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Suceeded")
                callback()
            },
            Response.ErrorListener {
                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    fun rechazarInvitacion(context: Context, invitacion: Notificacion, callback: () -> Unit) {
        val queue = Volley.newRequestQueue(context)

        Log.i("InvitacionesActivity",invitacion.idNotificacion.toString())

        val url = "${Constants.BASE_URL}/invitaciones-rechazar/"

        val request = JsonObjectRequest(
            Request.Method.POST, url + invitacion.idNotificacion, null,

            Response.Listener<JSONObject> { response ->
                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Suceeded")
                callback()
            },
            Response.ErrorListener {
                Log.i("InvitacionesActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    //No tiene callback xq si hubo un error al mandar una notificacion no se mostrara error
    fun enviarNotificacionMensaje(context: Context, notificacion: Notificacion){
        val queue = Volley.newRequestQueue(context)

        val url = "${Constants.BASE_URL}/enviar-notificacion"

        val request = JsonObjectRequest(
            Request.Method.POST, url, Notificacion().toJson(notificacion),

            Response.Listener<JSONObject> { response ->
                Log.i("MensajesActivity", "[DEBUG]:Communication with API Rest Suceeded")
            },
            Response.ErrorListener {
                Log.i("MensajesActivity", "[DEBUG]:Communication with API Rest Failed")
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)

    }

    /*
    fun aceptarCandidato(context: Context, invitacion: Notificacion, callback: () -> Unit) {
        val queue = Volley.newRequestQueue(context)

        Log.i("CandidatosActivity",invitacion.idNotificacion.toString())

        val url = "${Constants.BASE_URL}/candidato-aceptar/"

        val request = JsonObjectRequest(
            Request.Method.POST, url + invitacion.idNotificacion, null,

            Response.Listener<JSONObject> { response ->
                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Suceeded")
                callback()
            },
            Response.ErrorListener {
                Log.i("CandidatosActivity", "[DEBUG]:Communication with API Rest Failed")
                handleError(context, it, ::lambdaManejoErrores)
            })
        request.retryPolicy = defaultPolicy

        queue.add(request)
    }

    */



}